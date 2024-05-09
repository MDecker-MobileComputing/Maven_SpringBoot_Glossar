package de.eldecker.dhbw.spring.glossar.db;

import static java.time.LocalDateTime.now;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
public class BeispielDatenLadenRunner implements ApplicationRunner {

    private Logger LOG = LoggerFactory.getLogger( BeispielDatenLadenRunner.class );

    private Datenbank _datenbank;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public BeispielDatenLadenRunner( Datenbank datenbank ) {

        _datenbank = datenbank;
    }

    @Override
    @Transactional
    public void run( ApplicationArguments args ) throws Exception {

        final long anzahlEintraege = _datenbank.getAnzahlGlossareintraege();
        LOG.info( "Anzahl der Glossareinträge in der Datenbank: {}", anzahlEintraege );

        if ( anzahlEintraege > 0 ) {

            LOG.info( "Beispiel-Daten werden nicht geladen, da bereits Einträge vorhanden sind." );
            return;
        }



        final LocalDateTime jetzt = now();

        final GlossarEntity[] eintraege = {
                                             new GlossarEntity( "Maven"  , "Build-Management-Tool für Java-Projekte."           , jetzt ),
                                             new GlossarEntity( "Phase"  , "Ein Maven-Lifecycle besteht aus mehreren Phasen."   , jetzt ),
                                             new GlossarEntity( "pom.xml", "Zentrale Konfigurationsdatei für ein Maven-Projekt.", jetzt )
                                          };

        for ( GlossarEntity eintrag : eintraege ) {

            _datenbank.neuerGlossarEintrag( eintrag );
            LOG.info( "Beispieldatensatz eingefügt: {}", eintrag );
        }
    }

}
