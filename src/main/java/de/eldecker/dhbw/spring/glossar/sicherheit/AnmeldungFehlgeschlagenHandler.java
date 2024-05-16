package de.eldecker.dhbw.spring.glossar.sicherheit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.Optional;


/**
 * Eigene Implementierung von Interface {@code AuthenticationFailureHandler} für Event-Handling wenn Anmeldeversuch fehlgeschlagen ist.
 * <br><br>
 * 
 * Default-Implementierung: {@code org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler}
 */
@Service
public class AnmeldungFehlgeschlagenHandler implements AuthenticationFailureHandler {

    private final static Logger LOG = LoggerFactory.getLogger( AnmeldungFehlgeschlagenHandler.class );
	
    /**
     * Konfigurationswert für max. Anzahl Fehlerversuche bevor Nutzer inaktiviert wird;
     * ist in Datei {@code application.properties} definiert.
     */
    @Value( "${de.eldecker.glossar.login.fehlerversuch.max:999}" )
    private int _konfigurationMaxAnzahlFehlerversuche;
	
    /** Bean für Zugriff auf Datenbanktabelle mit Nutzerinformationen. */
    private Datenbank _datenbank;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>. 
     */
    @Autowired
    public AnmeldungFehlgeschlagenHandler( Datenbank datenbank ) {
    	
    	_datenbank = datenbank;
    }

    
    /**
     * Da Konfigurationen erst nach der Ausführung des Konstruktors verfügbar sind,
     * wird der konfigurierte Wert in dieser mit {@code PostConstruct} annotierten
     * Methode geloggt.
     */
    @PostConstruct
    private void loggeKonfiguration() {

        LOG.info( "Zeitspanne nach letztem Login, nachdem ein Nutzer gesperrt wird: {} Minuten",
        	      _konfigurationMaxAnzahlFehlerversuche );
    }
    
    
    /**
     * Diese Methode wird für jeden gescheiterten Anmeldeversuch aufgerufen:
     * <ul>
     * <li>Nachricht in Logfile schreiben.</li>
     * <li>Wenn ein gültiger Nutzername eingegeben wurde: Zähler fehlgeschlagener Anmeldeversuche
     *     wird erhöht.</li>
     * <li>Wenn Anzahl fehlgeschlagener Anmeldeversuche den konfigurierten Schwellwert erreicht hat, 
     *     dann wird der Nutzer auf inaktiv gesetzt, also gesperrt.</li>
     * <li>Auf jeden Fall wird am Ende auf die statische Fehlerseite {@code anmeldungGescheitert.html}
     *     weitergeleitet.</li>
     * </ul>
     */
    @Override
    @Transactional
    public void onAuthenticationFailure( HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception )
                    throws IOException, ServletException {

    	final String nutzername = request.getParameter( "username" );

    	final Optional<AutorEntity> autorOptional = _datenbank.getAutorByName( nutzername );
    	if ( autorOptional.isEmpty() ) {
    		
    		LOG.warn( "Anmeldung fehlgeschlagen für unbekannten Nutzer \"{}\".", nutzername );
    		
    	} else {
    		
    		final AutorEntity autor = autorOptional.get();
    		
    		int anzahlFehlerversuche = autor.getAnmeldungGescheitert();
    		
    		LOG.warn( "Anmeldung fehlgeschlagen für bekannten Nutzer \"{}\" mit bisher {} Fehlerversuchen.", 
    				  nutzername, anzahlFehlerversuche );
    		
    		anzahlFehlerversuche++;
    		
    		if ( anzahlFehlerversuche >= _konfigurationMaxAnzahlFehlerversuche ) {
    			
    			autor.setIstAktiv( false );
    			LOG.warn( "Nutzer \"{}\" gesperrt wegen zu vielen Fehlerversuchen bei Anmeldung.", 
    					  nutzername );
    		}
    		
    		autor.setAnmeldungGescheitert( anzahlFehlerversuche );
    		_datenbank.updateAutor( autor );
    	}
    	
    	response.sendRedirect( "/anmeldungGescheitert.html" );
    }

}