package de.eldecker.dhbw.spring.glossar.sicherheit;

import static java.lang.String.format;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;

import jakarta.transaction.Transactional;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Diese Bean enthält eine Methode, die periodisch aufgerufen wird.
 *
 * Zu <i>Scheduled Tasks</i> siehe auch
 * <a href="https://www.baeldung.com/spring-scheduled-tasks">diesen Artikel auf baeldung.com</a>.
 */
@Configuration
@EnableScheduling
public class NutzerSperrenPeriodicTask {

    private final static Logger LOG = LoggerFactory.getLogger( NutzerSperrenPeriodicTask.class );

    /**
     * Konfiguration aus {@code application.properties}: Anzahl Minuten nach letztem Login,
     * nachdem ein Nutzer gesperrt wird.
     */
    @Value( "${de.eldecker.glossar.inaktivitaet.minuten:99999}" )
    private int _konfigurationMinutenInaktivitaet;

    /** Bean für Datenbankzugriff. */
    private Datenbank _datenbank;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public NutzerSperrenPeriodicTask( Datenbank datenbank ) {

        _datenbank = datenbank;
    }


    /**
     * Da Konfigurationen erst nach der Ausführung des Konstruktors verfügbar sind
     * wird der konfigurierte Wert in dieser mit {@code PostConstruct} annotierten
     * Methode geloggt.
     */
    @PostConstruct
    private void loggeKonfiguration() {

        final String anzahlMinutenFormatiert = format( "%,d", _konfigurationMinutenInaktivitaet );

        LOG.info( "Zeitspanne nach letztem Login, nach der ein Nutzer gesperrt wird: {} Minuten",
                  anzahlMinutenFormatiert );
    }


    /**
     * Diese Methode wird alle 3 Minuten aufgerufen und sperrt Nutzer,
     * die sich zu lange nicht mehr eingeloggt haben.
     * Es gibt aber keine parallel laufenden Instanzen dieser Methode.
     *
     * Es werden Autoren gesperrt, die sich seit mindestens 5 Minuten
     * nicht mehr angemeldet haben (für eine produktive Anwendungen
     * wäre dies ein Zeitraum von mehreren Wochen oder Monaten).
     */
    @Scheduled(fixedRate = 3*60*1000)
    @Transactional
    public void nutzerSperren() {

        final List<AutorEntity> inaktivenAutorenList =
                                    _datenbank.getInaktiveAutoren( _konfigurationMinutenInaktivitaet );

        LOG.info( "Anzahl Autoren/Nutzer gefunden, die lange nicht mehr angemeldet waren: {}",
                  inaktivenAutorenList.size() );

        inaktivenAutorenList.forEach( autor -> {

            autor.setIstAktiv( false );
            _datenbank.updateAutor( autor );

            LOG.info( "Autor/Nutzer \"{}\" wird gesperrt wegen langer Inaktivität.",
                      autor.getNutzername() );
        });
    }

}
