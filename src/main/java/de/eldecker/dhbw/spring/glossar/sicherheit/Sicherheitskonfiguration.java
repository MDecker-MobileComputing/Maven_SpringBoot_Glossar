package de.eldecker.dhbw.spring.glossar.sicherheit;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Konfiguration von Web-Security: auf bestimmte Pfad soll man nur nach Authentifizierung
 * zugreifen dürfen.
 */
@Configuration
@EnableWebSecurity
public class Sicherheitskonfiguration {

    private final static Logger LOG = LoggerFactory.getLogger( Sicherheitskonfiguration.class );
        
    /** Array mit Pfaden, auf die auch ohne Authentifizierung zugegriffen werden kann. */
    private final static String[] OEFFENTLICHE_PFADE_ARRAY = { "/index.html"     ,
                                                               "/abgemeldet.html",
                                                               "/styles.css"     ,
                                                               "/anmeldungGescheitert.html",
                                                               "/h2-console/**"  ,
                                                               "/app/hauptseite" ,
                                                               "/app/eintrag/**"
                                                             };

    /** Objekt mit Event-Handler-Methode, die ausgeführt wird, wenn ein Nutzer sich erfolgreich angemeldet hat. */
    private final NutzerAngemeldetHandler _nutzerAngemeldetHandler;

    /** Objekt mit Event-Handler-Methode, die ausgeführt wird, wenn eine Nutzeranmeldung fehlgeschlagen ist. */
    private final AnmeldungFehlgeschlagenHandler _anmeldungFehlgeschlagenHandler;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public Sicherheitskonfiguration( NutzerAngemeldetHandler nutzerAngemeldetHandler,
                                     AnmeldungFehlgeschlagenHandler anmeldungFehlgeschlagenHandler ) {

        _nutzerAngemeldetHandler        = nutzerAngemeldetHandler;
        _anmeldungFehlgeschlagenHandler = anmeldungFehlgeschlagenHandler;
    }


    /**
     * Konfiguration Sicherheit für HTTP (formularbasierte Authentifizierung).
     */
    @Bean
    public SecurityFilterChain httpKonfiguration( HttpSecurity http ) throws Exception {

        final AntPathRequestMatcher[] oeffentlichPfadMatcherArray = getMatcherFuerOeffentlichePfade();

        return http.csrf( (csrf) -> csrf.disable() )
                   .authorizeHttpRequests( auth -> auth.requestMatchers( oeffentlichPfadMatcherArray ).permitAll()
                                                       .anyRequest().authenticated() )
                   .formLogin( formLogin -> formLogin.successHandler( _nutzerAngemeldetHandler        ) // im Handler wird auch Weiterleitung auf Hauptseite gemacht
                		                             .failureHandler( _anmeldungFehlgeschlagenHandler ) 
                		     ) 
                   .logout(logout -> logout
                                           .logoutUrl( "/logout" )
                                           .logoutSuccessUrl("/abgemeldet.html")
                                           .invalidateHttpSession( true )
                                           .deleteCookies( "JSESSIONID" )
                          )
                   .headers( headers -> headers.disable() ) // damit h2-console funktioniert
                   .build();
    }


    /**
     * Erzeugt für öffentliche Pfade aus String-Array in einen Array von {@code AntPathRequestMatcher}-Objekten.
     *
     * @return Array mit Matcher-Objekten für die öffentliche Pfade (Pfade, die ohne Authentifizierung aufgerufen werden können)
     */
    private static AntPathRequestMatcher[] getMatcherFuerOeffentlichePfade() {

        final int anzahlOeffentlichePfade = OEFFENTLICHE_PFADE_ARRAY.length;
        final AntPathRequestMatcher[] ergebnisArray = new AntPathRequestMatcher[ anzahlOeffentlichePfade ];
        for ( int i = 0; i < anzahlOeffentlichePfade; i++ ) {

            ergebnisArray[ i ] = antMatcher( OEFFENTLICHE_PFADE_ARRAY[i] );
        }
        LOG.info( "Anzahl öffentlicher Pfade: {}", anzahlOeffentlichePfade );

        return ergebnisArray;
    }

}