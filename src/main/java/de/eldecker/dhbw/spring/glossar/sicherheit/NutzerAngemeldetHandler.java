package de.eldecker.dhbw.spring.glossar.sicherheit;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;


/**
 * Ein Objekt dieser Klasse wird in {@link Sicherheitskonfiguration} registriert.
 */
@Service
public class NutzerAngemeldetHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger( NutzerAngemeldetHandler.class );

    /** Repository-Bean für Datenbankzugriff. */
    private Datenbank _datenbank;    
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public NutzerAngemeldetHandler( Datenbank datenbank ) {

        _datenbank = datenbank;
    }    
    
    
    /**
     * Methode wird aufgerufen, wenn sich ein Nutzer erfolgreich angemeldet hat:
     * <ul>
     * <li>Zeitpunkt letzte Anmeldung des Nutzers wird aktualisiert.</li>
     * <li>Zähler für fehlgeschlagene Login-Versuche wird auf 0 zurückgesetzt.</li>
     * </ul>
     * 
     * @param request HTTP-Request (wird hier nicht verwendet)
     * 
     * @param response HTTP-Antwort, für Weiterleitung auf Hauptseite
     * 
     * @param authentication Zum Auslesen von Nutzername 
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess( HttpServletRequest request, 
                                         HttpServletResponse response,
                                         Authentication authentication ) 
           throws IOException, ServletException {
                                      
        final String nutzername = authentication.getName();
        
        LOG.info( "Nutzer \"{}\" hat sich gerade angemeldet.", nutzername );
        
        final Optional<AutorEntity> autorOptional = _datenbank.getAutorByName( nutzername );
        if ( autorOptional.isEmpty() ) {
            
            LOG.error( "Nutzer \"{}\" hat sich erfolgreich angemeldet, wurde aber nicht auf DB gefunden.", 
                       nutzername );
            
        } else { // Zeitpunkt der letzten Anmeldung für Nutzer aktualisieren
            
            final LocalDateTime jetzt = now();
            
            final AutorEntity autor = autorOptional.get();
            autor.setLetzteAnmeldung( jetzt );
            
            autor.setAnmeldungGescheitert( 0 );
            
            _datenbank.updateAutor( autor );
        }
        
        response.sendRedirect( "/app/hauptseite" );
    }

}
