package de.eldecker.dhbw.spring.glossar.db;

import static de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity.NIE_ANGEMELDET_DATUM;

import static java.time.LocalDateTime.now;

import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
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

    private static final Logger LOG = LoggerFactory.getLogger( BeispielDatenImportRunner.class );

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

            // Glossareinträge auf DB schreiben
            final LocalDateTime jetzt = now();

            final GlossarEntity ge1 = new GlossarEntity( "Maven"  , "Build-Management-Tool für Java-Projekte."           , jetzt );
            final GlossarEntity ge2 = new GlossarEntity( "Phase"  , "Ein Maven-Lifecycle besteht aus mehreren Phasen."   , jetzt );
            final GlossarEntity ge3 = new GlossarEntity( "pom.xml", "Zentrale Konfigurationsdatei für ein Maven-Projekt.", jetzt );
            
            _datenbank.neuerGlossarEintrag( ge1 );
            _datenbank.neuerGlossarEintrag( ge2 );
            _datenbank.neuerGlossarEintrag( ge3 );
            
            // Autoren(Nutzer) auf DB schreiben
            final AutorEntity autor1 = new AutorEntity( "alice", "g3h3im", true, NIE_ANGEMELDET_DATUM );
            final AutorEntity autor2 = new AutorEntity( "bob"  , "s3cr3t", true, NIE_ANGEMELDET_DATUM );
            
            _datenbank.neuerAutor( autor1 );             
            _datenbank.neuerAutor( autor2 );            
        }
    }

}
