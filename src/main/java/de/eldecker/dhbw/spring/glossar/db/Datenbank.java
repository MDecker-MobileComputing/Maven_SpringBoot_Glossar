package de.eldecker.dhbw.spring.glossar.db;

import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

/**
 * Repository-Bean, die alle Methoden für Zugriff auf Datenbank enthält.
 */
@Repository
public class Datenbank {
    
    private final static Logger LOG = LoggerFactory.getLogger( Datenbank.class );

    /** Zentrale Objekt von JPA für Datenbankzugriffe. */
    private EntityManager _em;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public Datenbank( EntityManager em ) {

        _em = em;
    }


    /**
     * Liefert die Anzahl der Glossareinträge in der Datenbank.
     *
     * @return Anzahl der Glossareinträge.
     */
    public int getAnzahlGlossareintraege() {

        final Query query = _em.createQuery( "SELECT COUNT(g) FROM GlossarEntity g" );
        
        final Long ergebnisLong = (long) query.getSingleResult();
        
        final int ergebnisInt = ergebnisLong.intValue();
        
        return ergebnisInt;
    }
    
    
    /**
     * Glossareintrag anhand ID auslesen (mit allen Attributen).
     * 
     * @param id Primärschlüssel/ID von Glossareintrag
     * 
     * @return Optional enthält Eintrag wenn gefunden
     */
    public Optional<GlossarEntity> getEintragById( Long id ) {
    
        try {
        
            GlossarEntity ergebnis = _em.find( GlossarEntity.class, id );
            return Optional.ofNullable( ergebnis );
            
        }
        catch ( IllegalArgumentException ex ) {
         
            LOG.error( "Fehler bei Lesen von Glossareintrag anhand ID.", ex );
            return Optional.empty();
        }        
    }


    /**
     * Fügt einen neuen Glossareintrag in die Datenbank ein.
     *
     * @param eintrag Der neue Glossareintrag.
     */
    public void neuerGlossarEintrag( GlossarEntity eintrag ) {

        _em.persist( eintrag );
    }
    
    
    /**
     * Liste aller Glossarbegriffe, aber nur ID und Begriff (nicht aber Erklärung und weitere Attribute)
     * sind gefüllt.
     * <br><br>
     * 
     * Für diese Methode wird ein spezieller Konstruktor der Entity-Klasse {@link GlossarEntity} verwendet,
     * der nur die ID und den Begriff füllt.
     * 
     * @return Liste aller Glossarbegriffe, alphabetisch sortiert; nur die Attribute ID und Begriff (Lemma)
     *         sind gefüllt.
     */
    public List<GlossarEntity> getGlossarBegriffe() {
        
        final String jpqlStr = 
                """
                SELECT new de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity( g._id, g._begriff ) 
                       FROM GlossarEntity g
                       ORDER BY g._begriff ASC
                """;

        final TypedQuery<GlossarEntity> query = _em.createQuery( jpqlStr, GlossarEntity.class );

        return query.getResultList();
    }    

}
