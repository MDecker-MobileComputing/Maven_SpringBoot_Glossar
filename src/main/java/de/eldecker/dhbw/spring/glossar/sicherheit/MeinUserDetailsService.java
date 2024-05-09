package de.eldecker.dhbw.spring.glossar.sicherheit;

import static de.eldecker.dhbw.spring.glossar.sicherheit.Sicherheitskonfiguration.ROLLE_AUTOR;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class MeinUserDetailsService implements UserDetailsService {

    private Logger LOG = LoggerFactory.getLogger( MeinUserDetailsService.class );
    
    
    final PasswordEncoder _passwordEncoder = createDelegatingPasswordEncoder();
    
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
