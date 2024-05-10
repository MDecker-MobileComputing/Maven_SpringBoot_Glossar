package de.eldecker.dhbw.spring.glossar.sicherheit;

import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;


/**
 * Implementierung Interface {@code UserDetailsService}, von der bei Anmeldevorgang 
 * ein Nutzerobjekt für einen bestimmten Nutzernamen abgefragt.
 */
@Service
public class MeinUserDetailsService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger( MeinUserDetailsService.class );
    
    /** Rolle für Nutzer, die Glossareinträge erstellen und ändern dürfen. */
    public static final String ROLLE_AUTOR = "autor";
        
    /** Objekt für Kodierung Passwort. */
    private final PasswordEncoder _passwordEncoder = createDelegatingPasswordEncoder();
    
    /** Repository-Bean für Datenbankzugriff. */
    private final Datenbank _datenbank;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public MeinUserDetailsService( Datenbank datenbank ) {

        _datenbank = datenbank;
    }
    
        
    /**
     * Diese Methode wird aufgerufen, wenn ein Nutzer {@code nutzername} und {@code passwort} 
     * im Anmeldeformular eingegeben hat. Es wird dann in der Datenbank nachgeschaut, ob es
     * einen aktiven Nutzer mit {@code nutzername} gibt. Ein inaktiver Nutzer wird wie ein
     * nicht existierender Nutzer behandelt (es wird aber eine Warnung auf den Logger
     * geschrieben). 
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
        
        final Optional<AutorEntity> autorOptional = _datenbank.getAutorByName( nutzername );
        if ( autorOptional.isEmpty() ) {
            
            throw new UsernameNotFoundException( "Nutzer \"" + nutzername + "\" nicht in DB gefunden.");
        }

        final AutorEntity autor = autorOptional.get();
        
        if ( autor.isIstAktiv() == false ) {
            
            final String fehlerNachricht = 
                    String.format( "Nutzer \"%s\" auf Datenbank gefunden, ist aber inaktiv.", 
                                    nutzername );
            
            LOG.warn( fehlerNachricht );
            
            throw new UsernameNotFoundException( fehlerNachricht );
        }
                
        final String passwortEncoded = _passwordEncoder.encode( autor.getPasswort() );
        
        final UserDetails userDetails = User.withUsername( nutzername )
                                            .password( passwortEncoded )
                                            .roles( ROLLE_AUTOR )
                                            .build();
        
        LOG.info( "Nutzer mit Name \"{}\" aus DB-Tabelle geladen.", nutzername );
        
        return userDetails;
    }

}
