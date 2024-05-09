package de.eldecker.dhbw.spring.glossar.db;

import static java.time.LocalDateTime.now;

import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * Diese {@code run()}-Methode dieser Bean wird beim Start automatisch ausgeführt,
 * weil die Klasse das Interface {@code ApplicationRunner} implementiert.
 */
@Component
public class BeispielDatenImportRunner implements ApplicationRunner {

    private Logger LOG = LoggerFactory.getLogger( BeispielDatenImportRunner.class );

    /** Repository-Bean für Datenbankzugriff. */
    private Datenbank _datenbank;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public BeispielDatenImportRunner( Datenbank datenbank ) {

        _datenbank = datenbank;
    }

    
    /**
     * Die Methode schreibt einige Datensätze in die Datenbank, aber nur, wenn es
     * noch keinen einzigen Glossareintrag gibt. 
     */
    @Override
    @Transactional
    public void run( ApplicationArguments args ) throws Exception {

        final int anzahlEintraege = _datenbank.getAnzahlGlossareintraege();
        LOG.info( "Anzahl der Glossareinträge in der Datenbank: {}", anzahlEintraege );

        if ( anzahlEintraege > 0 ) {

            LOG.info( "Beispiel-Daten werden nicht geladen, da bereits Einträge vorhanden sind." );            
            
        } else {

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

}
