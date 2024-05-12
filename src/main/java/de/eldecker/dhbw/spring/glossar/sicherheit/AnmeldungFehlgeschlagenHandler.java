package de.eldecker.dhbw.spring.glossar.sicherheit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Default-Implementierung: {@code org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler}
 */
@Component
public class AnmeldungFehlgeschlagenHandler extends SimpleUrlAuthenticationFailureHandler {

    private final static Logger LOG = LoggerFactory.getLogger( AnmeldungFehlgeschlagenHandler.class );

    @Override
    public void onAuthenticationFailure( HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception )
                    throws IOException, ServletException {

    	final String username = request.getParameter("username");
    	
    	LOG.info( "Anmeldung fehlgeschlagen für Nutzer \"{}\".", username );
    	
    	super.onAuthenticationFailure(request, response, exception);
    	
    	/*
        LOG.info( "Anmeldung von Nutzer fehlgeschlagen: " + exception.getMessage() );

        response.setStatus( HttpStatus.UNAUTHORIZED.value() );
        response.getWriter().write("Anmeldung fehlgeschlagen");
        */
    }
}