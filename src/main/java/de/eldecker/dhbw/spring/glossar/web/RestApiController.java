package de.eldecker.dhbw.spring.glossar.web;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.CREATED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;
import jakarta.transaction.Transactional;


/**
 * REST-Controller-Klasse zur Bereitstellung der REST-Endpunkte,
 * die vom Frontend (HTML+JavaScript im Browser) angesprochen werden.
 */
@RestController
@RequestMapping( "/api/v1" )
public class RestApiController {

    private static Logger LOG = LoggerFactory.getLogger( RestApiController.class );

    /** Target object for deserialization of payload received via HTTP-POST. */
    public record Payload( String begriff, String erklaerung ) {}

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
     * Endpunkt für HTTP-POST-Request zum Anlegen neuer Eintrag.
     *
     * @param jsonPayload  JSON-Payload mit neuem Begriff und Erklärung.
     *
     * @param authentication Objekt für Abfrage authentifizierter Nutzer.
     *
     * @return HTTP-Status 201 wenn erfolgreich, oder 401 wenn keine Berechtigung.
     */
    @PostMapping( "/neu" )
    @Transactional
    public ResponseEntity<String> eintragNeuAendern( @RequestBody String jsonPayload,
                                                     Authentication authentication ) {

        if ( authentication == null || authentication.isAuthenticated() == false ) {

            LOG.warn( "Versuch neuen Eintrag anzulegen, aber Nutzer ist nicht authentifziert." );
            return new ResponseEntity<>( "Keine Berechtigung einen neuen Eintrag anzulegen", UNAUTHORIZED ); // HTTP-Status-Code 401
        }

        LOG.info( "JSON-Payload für neuen Glossareintrag über HTTP-POST erhalten: {}", jsonPayload );

        Payload payloadObjekt = null;
        try {

            payloadObjekt = _objectMapper.readValue( jsonPayload, Payload.class );
        }
        catch ( JsonProcessingException ex ) {

            LOG.error( "Fehler bei Deserialisierung von HTTP-Payload mit neuem Eintrag.", ex );
            return new ResponseEntity<>( "Ungültige JSON-Payload.", BAD_REQUEST);
        }

        // TODO: Überprüfen, dass es den Eintrag nicht schon gibt

        final LocalDateTime jetzt = now();

        final GlossarEntity ge = new GlossarEntity( payloadObjekt.begriff(),
                                                    payloadObjekt.erklaerung(),
                                                    jetzt, jetzt );

        final long idNeu = _datenbank.neuerGlossarEintrag( ge );
        LOG.info( "Neuer Glossareintrag mit ID {} angelegt.", idNeu );

        return new ResponseEntity<>( "Neuer Eintrag im Glossar gespeichert", CREATED ); // HTTP-Status-Code 201
    }
}
