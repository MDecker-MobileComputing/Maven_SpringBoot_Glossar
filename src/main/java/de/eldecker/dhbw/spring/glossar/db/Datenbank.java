package de.eldecker.dhbw.spring.glossar.db;

import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;


@Repository
public class Datenbank {

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
