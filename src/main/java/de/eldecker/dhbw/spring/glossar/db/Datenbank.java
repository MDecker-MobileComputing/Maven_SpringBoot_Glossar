package de.eldecker.dhbw.spring.glossar.db;

import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;


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

        final Query query = _em.createQuery("SELECT COUNT(g) FROM GlossarEntity g");
        Long l = (long) query.getSingleResult();
        return l.intValue();
    }


    /**
     * Fügt einen neuen Glossareintrag in die Datenbank ein.
     *
     * @param eintrag Der neue Glossareintrag.
     */
    public void neuerGlossarEintrag( GlossarEntity eintrag ) {

        _em.persist( eintrag );
    }

}
