package de.eldecker.dhbw.spring.glossar.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;


@Component
public class BeispielDatenLadenRunner implements ApplicationRunner {
    
    private Logger LOG = LoggerFactory.getLogger( BeispielDatenLadenRunner.class );
    
    private EntityManager _em;
    
    
    @Autowired
    public BeispielDatenLadenRunner(EntityManager em) {
        
        _em = em;
    }
    
    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {

        final GlossarEntity eintrag1 = new GlossarEntity( "Maven", "Build-Management-Tool für Java-Projekte" );        
        final GlossarEntity eintrag2 = new GlossarEntity( "pom.xml", "Zentrale Konfigurationsdatei für ein Maven-Projekt" );
        
        GlossarEntity ergebnis = null;
        
        ergebnis = _em.merge(eintrag1);
        LOG.info( "Beispieldatensatz gemerged: {}", ergebnis);
                
        _em.merge(eintrag2);
        LOG.info( "Beispieldatensatz gemerged: {}", ergebnis);        
    }
        
}
