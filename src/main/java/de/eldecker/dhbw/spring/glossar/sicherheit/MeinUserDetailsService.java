package de.eldecker.dhbw.spring.glossar.sicherheit;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Implementierung Interface {@code UserDetailsService}, von der bei Anmeldevorgang 
 * ein Nutzerobjekt für einen bestimmten Nutzernamen abgefragt.
 */
@Service
public class MeinUserDetailsService implements UserDetailsService {

    private Logger LOG = LoggerFactory.getLogger( MeinUserDetailsService.class );
    
    /** Rolle für Nutzer, die Glossareinträge erstellen und ändern dürfen. */
    public static final String ROLLE_AUTOR = "autor";
        
    /** Objekt für Kodierung Passwort. */
    final PasswordEncoder _passwordEncoder = createDelegatingPasswordEncoder();
    
    
    /**
     * Diese Methode wird aufgerufen, wenn ein Nutzer {@code nutzername} und {@code passwort} 
     * im Anmeldeformular eingegeben hat.
     * 
     * @param nutzername Im Anmeldeformular eingegebener Nutzername
     * 
     * @return Nutzer-Objekt für {@code nutzername}, enthält u.a. Passwort.
     * 
     * @throws UsernameNotFoundException Es gibt keinen Nutzer mit {@code nutzername}
     */
    @Override
    public UserDetails loadUserByUsername( String nutzername ) throws UsernameNotFoundException {

        LOG.info( "Laden von Nutzername \"{}\" angefordert.", nutzername );
        
        if ( nutzername.equals( "alice") == false ) {
            
            throw new UsernameNotFoundException( "Nutzer \"" + nutzername + "\" gibt es nicht.");
        }
                
        UserDetails userDetails = User.withUsername( "alice" )
                   .password( _passwordEncoder.encode( "g3h3im") )
                   .roles( ROLLE_AUTOR )
                   .build();
        
        LOG.info( "Nutzerdetails geladen: {}", userDetails );
        
        return userDetails;
    }

}
