package de.eldecker.dhbw.spring.glossar.sicherheit;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


/**
 * Konfiguration von Web-Security: auf bestimmte Pfad soll man nur nach Authentifizierung zugreifen dürfen.
 */
@Configuration
@EnableWebSecurity
public class Sicherheitskonfiguration {
        
    private final static NutzerAngemeldetHandler NUTZER_ANGEMELDET_HANDLER = new NutzerAngemeldetHandler();
    
    /** Array mit Pfaden, auf die auch ohne Authentifizierung zugegriffen werden kann. */
    private final static AntPathRequestMatcher[] OEFFENTLICHE_PFADE_ARRAY = { antMatcher( "/index.html"      ),
                                                                              antMatcher( "/abgemeldet.html" ),
                                                                              antMatcher( "/styles.css"      ), // wird von index.html und abgemeldet.html benötigt
                                                                              antMatcher( "/h2-console/**"   ),
                                                                              antMatcher( "/app/hauptseite"  )
                                                                            };

    /**
     * Konfiguration Sicherheit für HTTP (formularbasierte Authentifizierung).
     */
    @Bean
    public SecurityFilterChain httpKonfiguration( HttpSecurity http ) throws Exception {

         
        
        return http.csrf( (csrf) -> csrf.disable() )
                   .authorizeHttpRequests( auth -> auth.requestMatchers( OEFFENTLICHE_PFADE_ARRAY ).permitAll()
                                                       .anyRequest().authenticated() )
                   .formLogin( formLogin -> formLogin.successHandler( NUTZER_ANGEMELDET_HANDLER ) ) // im Handler wird auch Weiterleitung auf Hauptseite gemacht                                                      
                   .logout(logout -> logout
                                           .logoutUrl( "/logout" )
                                           .logoutSuccessUrl("/abgemeldet.html")
                                           .invalidateHttpSession( true )
                                           .deleteCookies( "JSESSIONID" )
                       )
                   .headers( headers -> headers.disable() ) // damit h2-console funktioniert
                   .build();
    }

}