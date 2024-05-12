package de.eldecker.dhbw.spring.glossar.sicherheit;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;

import java.util.List;

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

    /** Bean für Datenbankzugriff. */
    private Datenbank _datenbank;

    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    public NutzerSperrenPeriodicTask( Datenbank datenbank ) {

        _datenbank = datenbank;
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
    public void nutzerSperren() {

        final List<AutorEntity> inaktivenAutorenList = _datenbank.getInaktiveAutoren( 3 );

        LOG.info( "Anzahl Autoren/Nutzer gefunden, die lange nicht mehr angemeldet waren: {}",
                  inaktivenAutorenList.size() );
    }

}
