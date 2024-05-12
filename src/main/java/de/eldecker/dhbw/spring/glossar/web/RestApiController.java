package de.eldecker.dhbw.spring.glossar.web;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;
import de.eldecker.dhbw.spring.glossar.model.Payload;


/**
 * REST-Controller-Klasse zur Bereitstellung des REST-Endpunkt,
 * der vom JavaScript-Code im Frontend für das Anlegen und Ändern
 * von Glossareinträgen aufgerufen wird.
 */
@RestController
@RequestMapping( "/api/v1" )
public class RestApiController {

    private static Logger LOG = LoggerFactory.getLogger( RestApiController.class );

    /** Repository-Bean für Zugriff auf Datenbank. */
    private final Datenbank _datenbank;

    /** Bean für Deserialisierung von JSON-Playload. */
    private final ObjectMapper _objectMapper;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public RestApiController( Datenbank datenbank,
                              ObjectMapper objectMapper ) {

        _datenbank    = datenbank;
        _objectMapper = objectMapper;
    }


    /**
     * Endpunkt für HTTP-POST-Request für Erzeugung oder Änderung Eintrag.
     *
     * @param jsonPayload JSON-Payload mit Begriff und Erklärung; für Änderung auch ID.
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer.
     *
     * @return Wenn erfolgreich dann HTTP-Status-Code 201 (neuer Eintrag) bzw. 
     *         200 (Änderung); wenn der aktuelle Nutzer in der DB nicht gefunden
     *         wird, dann wird 500 (Internal Server Error) zurückgegeben. 
     */
    @PostMapping( "/speichern" )
    @Transactional
    public ResponseEntity<String> eintragNeuAendern( @RequestBody String jsonPayload,
                                                     Authentication authentication ) {

        if ( authentication == null || authentication.isAuthenticated() == false ) {

            // sollte nicht passieren wenn Spring Security richtig konfiguriert
            LOG.warn( "Versuch neuen Eintrag anzulegen, aber Nutzer ist nicht authentifziert." );
            return new ResponseEntity<>( "Keine Berechtigung einen neuen Eintrag anzulegen", UNAUTHORIZED ); // HTTP-Status-Code 401
        }
        
        final String nameAutor = authentication.getName();
        
        final Optional<AutorEntity> autorOptional = _datenbank.getAutorByName( nameAutor );
        if ( autorOptional.isEmpty() ) {
            
            LOG.error( "Aktueller Nutzer \"{}\" nicht in Datenbank gefunden.", nameAutor );
            
            return new ResponseEntity<>( "Interner Fehler: Aktueller Nutzer nicht in Datenbank gefunden.", 
                                         INTERNAL_SERVER_ERROR );
        }
        final AutorEntity autor = autorOptional.get();

        Payload payloadObjekt = null;
        try {

            payloadObjekt = _objectMapper.readValue( jsonPayload, Payload.class );
        }
        catch ( JsonProcessingException ex ) {

            LOG.error( "Fehler bei Deserialisierung von HTTP-Payload mit neuem Eintrag.", ex );
            return new ResponseEntity<>( "Ungültige JSON-Payload.", BAD_REQUEST );
        }
        
        final Optional<Long> idOptional = payloadObjekt.holeID();
        
        if ( idOptional.isPresent() ) {
            
            return eintragAendern( payloadObjekt, autor );
            
        } else {
            
            return eintragNeu( payloadObjekt, autor );
        }
    }
    
    
    /**
     * Neuen Eintrag anlegen.
     * 
     * @param payload Payload mit Details für neuen Eintrag
     * 
     * @param autor Der Nutzer, der den Eintrag anlegen will
     * 
     * @return HTTP-Status-Code 201 (Created) wenn erfolgreich, 409 (Conflict) wenn 
     *         Eintrag schon vorhanden. 
     */
    private ResponseEntity<String> eintragNeu( Payload payload, AutorEntity autor ) {
                                                      
        final String begriffNeu = payload.begriff();
        final Optional<GlossarEntity> eintragAlt = _datenbank.getEintragByBegriff( begriffNeu );
        
        if ( eintragAlt.isPresent() ) {

            LOG.warn( "Glossareintrag mit Begriff \"{}\" bereits vorhanden.", begriffNeu );
            return new ResponseEntity<>( "Eintrag mit Begriff bereits vorhanden.", CONFLICT );
        }

        final LocalDateTime jetzt = now();
        
        final GlossarEntity eintragNeu = new GlossarEntity( payload.begriff(),
                                                            payload.erklaerung(),
                                                            jetzt,
                                                            autor );                                                             

        final long idNeu = _datenbank.neuerGlossarEintrag( eintragNeu );
        
        final String ergebnisText = format( "Neuer Glossareintrag mit ID=%d gespeichert: \"%s\"", 
                                             idNeu, eintragNeu.getBegriff() ); 
                        
        return new ResponseEntity<>( ergebnisText, CREATED ); // HTTP-Status-Code 201                                      
    }
    
    
    /**
     * Einträg ändern. 
     * 
     * @param payload Payload mit Details für zu ändernden Eintrag.
     *                es muss vor dem Aufruf überprüft worden sein, dass
     *                die Methode {@link Payload#holeID()} eine ID
     *                zurückgibt.
     * 
     * @param autor Der Nutzer, der den Eintrag ändern will
     * 
     * @return HTTP-Status-Code 200 (OK) wenn Änderung erfolgreich,
     *         400 (Bad Request) wenn es keinen Glossareintrag mit der ID
     *         aus {@code payload} gibt oder wenn das Feld {@code begriff}
     *         in diesem Objekt leer ist.
     */
    private ResponseEntity<String> eintragAendern( Payload payload, AutorEntity autor ) {
                                        
        if ( payload.begriff().isBlank() ) {
            
            return new ResponseEntity<>( "Begriff darf für Änderung von Glossareintrag nicht leer sein.",
                                         BAD_REQUEST );            
        }
                
        final long id = payload.holeID().get();
        
        final Optional<GlossarEntity> eintragOptional = _datenbank.getEintragById( id );
        if ( eintragOptional.isEmpty() ) {
            
            return new ResponseEntity<>( "Kein Glossareintrag mit ID=" + id + " zum Ändern gefunden.",
                                          BAD_REQUEST );
        }
        
        final GlossarEntity eintrag = eintragOptional.get();
        
        eintrag.setBegriff(    payload.begriff()    );
        eintrag.setErklaerung( payload.erklaerung() );
        eintrag.setZeitpunktAenderung( now() );
        eintrag.setAutorAenderung( autor );
        
        _datenbank.updateGlossarEintrag( eintrag );
        
        final String ergebnisText = format( "Glossareintrag mit ID=%d geändert: \"%s\"", 
                                            id, payload.begriff() );
                        
        return new ResponseEntity<>( ergebnisText, OK ); // HTTP-Status-Code 201                                      
    }
}
