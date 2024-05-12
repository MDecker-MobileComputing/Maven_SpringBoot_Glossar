package de.eldecker.dhbw.spring.glossar.sicherheit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


/**
 * Eigene Implementierung für Event-Handling wenn Anmeldeversuch fehlgeschlagen ist.
 * 
 * Default-Implementierung: {@code org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler}
 */
@Component
public class AnmeldungFehlgeschlagenHandler implements AuthenticationFailureHandler {

    private final static Logger LOG = LoggerFactory.getLogger( AnmeldungFehlgeschlagenHandler.class );

    /**
     * Methode leitet weiter auf statische Fehlerseite {@code anmeldungGescheitert.html}. 
     */
    @Override
    public void onAuthenticationFailure( HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception )
                    throws IOException, ServletException {

    	final String username = request.getParameter("username");

    	LOG.info( "Anmeldung fehlgeschlagen für Nutzer \"{}\".", username );
    	
    	response.sendRedirect("/anmeldungGescheitert.html");
    }

}