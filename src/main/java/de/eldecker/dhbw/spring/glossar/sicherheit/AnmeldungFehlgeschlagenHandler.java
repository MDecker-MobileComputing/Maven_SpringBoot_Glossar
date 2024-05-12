package de.eldecker.dhbw.spring.glossar.sicherheit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@Component
public class AnmeldungFehlgeschlagenHandler implements AuthenticationFailureHandler {

    private final static Logger LOG = LoggerFactory.getLogger( AnmeldungFehlgeschlagenHandler.class );

    @Override
    public void onAuthenticationFailure( HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception )
                    throws IOException, ServletException {

        LOG.info( "Anmeldung von Nutzer fehlgeschlagen: " + exception.getMessage() );

        response.setStatus( HttpStatus.UNAUTHORIZED.value() );
        response.getWriter().write("Anmeldung fehlgeschlagen");
    }
}