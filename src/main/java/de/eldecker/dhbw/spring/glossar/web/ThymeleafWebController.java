package de.eldecker.dhbw.spring.glossar.web;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;


/**
 * Controller (kein RestController!), der die Anfragen für die Thymeleaf-Views bearbeitet.
 * Alle Pfade beginnen mit {@code /app/}.
 */
@Controller
@RequestMapping( "/app" )
public class ThymeleafWebController {

    private static final Logger LOG = LoggerFactory.getLogger( ThymeleafWebController.class );
        
    /** Attribut-Key für bool'schen Wert, der gdw. {@code true} ist, wenn der Nutzer angemeldet. */
    private static final String ATTRIBUT_ANGEMELDET = "ist_angemeldet";
    
    /** Attribut-Key für Template "hauptseite"; referenziert leeren String, wenn kein Nutzer angemeldet. */
    private static final String ATTRIBUT_NUTZER = "nutzername";
    
    /** Attribut-Key für Template "hauptseite" mit Liste der Einträge. */
    private static final String ATTRIBUT_EINTRAEGE_LISTE = "eintraege";

    /** Attribut-Key für Template "begriff" mit Begriff (Lemma) das erklärt werden soll. */
    private static final String ATTRIBUT_BEGRIFF = "begriff";

    
    /** Attribut-Key für Template "eintrag" mit Erklärung zu einem Glossareintrag. */
    private static final String ATTRIBUT_ERKLAERUNG = "erklaerung";
    
    /** Attribut-Key für Template "eintrag" mit Fehlermeldung. */
    private static final String ATTRIBUT_FEHLERMELDUNG = "fehlermeldung";
    
    /** Repository-Bean für Zugriff auf Datenbank. */
    private final Datenbank _datenbank;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */    
    @Autowired
    public ThymeleafWebController( Datenbank datenbank ) {
        
        _datenbank = datenbank;
    }
    
    
    /**
     * Übersichtsseite mit Liste der Glossareinträge (aber ohne Erklärungen) anzeigen. 
     * 
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     * 
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden. 
     * 
     * @return Name (ohne Suffix) der Template-Datei {@code hauptseite.html}, die angezeigt
     *         werden soll; wird in Ordner {@code src/main/resources/templates/} gesucht.
     */
    @GetMapping( "/hauptseite" )
    public String hauptseiteAnzeigen( Authentication authentication,
                                      Model model ) {
                        
        final boolean nutzerIstAngemeldet = authentication != null && 
                                            authentication.isAuthenticated();        
        if ( nutzerIstAngemeldet ) {
            
            final String nutzername = authentication.getName();
            LOG.info( "Zugriff auf Hauptseite von Nutzer \"{}\".", nutzername );
            model.addAttribute( ATTRIBUT_NUTZER    , nutzername );
            model.addAttribute( ATTRIBUT_ANGEMELDET, true       );
            
        } else {
        
            LOG.info( "Zugriff auf Hauptseite von unangemeldetem Nutzer." );
            model.addAttribute( ATTRIBUT_NUTZER    , ""    );
            model.addAttribute( ATTRIBUT_ANGEMELDET, false );
        }
                
        final List<GlossarEntity> begriffListe = _datenbank.getGlossarBegriffe();
        model.addAttribute( ATTRIBUT_EINTRAEGE_LISTE, begriffListe );        
                               
        return "hauptseite";
    }

    
    /**
     * Einzelnen Glossareintrag anzeigen.
     * 
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     *                       
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden. 
     *              
     * @param idStr ID (Nummer) des Glossareintrags als String, sollte sich nach long parsen lassen
     * 
     * @return Name (ohne Suffix) der Template-Datei {@code eintrag.html}, die angezeigt
     *         werden soll; wird in Ordner {@code src/main/resources/templates/} gesucht.
     */
    @GetMapping( "/eintrag/{id}")
    public String eintragAnzeigen( Authentication authentication,
                                   Model model,
                                   @PathVariable("id") String idStr ) {
        
        final boolean nutzerIstAngemeldet = authentication != null && 
                      authentication.isAuthenticated();        
        if ( nutzerIstAngemeldet ) {

            final String nutzername = authentication.getName();
            LOG.info( "Zugriff auf Eintrag mit ID={} von Nutzer \"{}\".", idStr, nutzername );
            model.addAttribute( ATTRIBUT_NUTZER    , nutzername );
            model.addAttribute( ATTRIBUT_ANGEMELDET, true       );

        } else {

            LOG.info( "Zugriff auf Eintrag mit ID={} von anonymen Nutzer.", idStr );
            model.addAttribute( ATTRIBUT_NUTZER    , ""    );
            model.addAttribute( ATTRIBUT_ANGEMELDET, false );
        }
        
        long idLong = -1;
        try {
            
            idLong = Long.parseLong( idStr );
            
            final Optional<GlossarEntity> entityOptional = _datenbank.getEintragById( idLong );
            if ( entityOptional.isPresent() ) {
                
                final GlossarEntity entity = entityOptional.get(); 

                model.addAttribute( ATTRIBUT_BEGRIFF   , entity.getBegriff()    );
                model.addAttribute( ATTRIBUT_ERKLAERUNG, entity.getErklaerung() );
                
                LOG.info( "Glossareintrag {} aufgelöst: {}", idLong, entity.getBegriff() );
                
            } else {

                model.addAttribute( ATTRIBUT_BEGRIFF   , "???" );
                model.addAttribute( ATTRIBUT_ERKLAERUNG, ""    );
                
                model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "Kein Eintrag mit ID=" + idLong + " gefunden." );
                LOG.error( "Kein Glossareintrag mit ID={} gefunden.", idLong );            
            }                   
            
        } catch ( NumberFormatException ex ) {
            
            model.addAttribute( ATTRIBUT_BEGRIFF      , "???" );
            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "ID \"" + idStr + "\" übergeben, ist kein gültiger long-Wert." );                    
            LOG.error( "Pfadparameter für ID \"{}\" konnte nicht nach long geparst werden.", idStr, ex);        
        }                          
        
        return "eintrag";
    }
    
}
