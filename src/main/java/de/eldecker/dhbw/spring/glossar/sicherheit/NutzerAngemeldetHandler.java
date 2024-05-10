package de.eldecker.dhbw.spring.glossar.sicherheit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


/**
 * Ein Objekt dieser Klasse wird in {@link Sicherheitskonfiguration} registriert.
 */
public class NutzerAngemeldetHandler implements AuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger( NutzerAngemeldetHandler.class );

    
    /**
     * Methode wird aufgerufen, wenn sich ein Nutzer erfolgreich angemeldet hat.
     * 
     * @param request HTTP-Request
     * 
     * @param response HTTP-Antwort, für Weiterleitung auf Hauptseite
     * 
     * @param authentication Zum Auslesen von Nutzername 
     */
    @Override
    public void onAuthenticationSuccess( HttpServletRequest request, 
                                         HttpServletResponse response,
                                         Authentication authentication ) 
           throws IOException, ServletException {
                                                          
        LOG.info( "Nutzer hat sich gerade angemeldet: " + authentication.getName() );
        
        response.sendRedirect( "/app/hauptseite" );
    }

}
