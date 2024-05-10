package de.eldecker.dhbw.spring.glossar.db;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


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

        final Query query = _em.createQuery( "SELECT COUNT(g) FROM GlossarEntity g" ); // Query mit JPQL

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

            final GlossarEntity ergebnis = _em.find( GlossarEntity.class, id );
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
     * @param eintrag Neuer Glossareintrag
     */
    public void neuerGlossarEintrag( GlossarEntity eintrag ) {

        _em.persist( eintrag );
        LOG.info( "Neuer Glossareintrag in Datenbank gespeichert: {}", eintrag.getBegriff() );
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


    /**
     * Fügt neuen Autor (Nutzer) in die Datenbank ein.
     *
     * @param autor Neuer Autor/Nutzer
     */
    public void neuerAutor( AutorEntity autor ) {

        _em.persist( autor );
        LOG.info( "Neuer Autor (Nutzer) in Datenbank gespeichert: {}", autor.getNutzername() );
    }


    /**
     * Autor/Nutzer anhand {@code nutzername} holen.
     *
     * @param nutzername Nutzername nach dem gesucht wird (case-sensitive!)
     *
     * @return Optional ist leer, wenn Nutzer nicht gefunden; sonst ist der Nutzer
     *         mit allen Feldern enthalten.
     */
    public Optional<AutorEntity> getAutorByName( String nutzername ) {

        final String jpqlStr = "SELECT a FROM AutorEntity a WHERE a._nutzername = :nutzername";
        
        final TypedQuery<AutorEntity> query = _em.createQuery( jpqlStr, AutorEntity.class );
        query.setParameter( "nutzername", nutzername );

        final List<AutorEntity> results = query.getResultList(); // eigentliche Query ausführen

        if ( results.isEmpty() ) {

            LOG.warn( "Kein Nutzer mit Nutzername '{}' gefunden.", nutzername );
            return Optional.empty();

        } else {

            return Optional.of( results.get(0) );
        }
    }
    
    
    /**
     * Objekt mit Autor/Nutzer auf DB aktualisieren.
     * 
     * @param autorNutzer Nutzer mit mind. einem neuen Attributwert (außer der ID), 
     *                    ID muss gefüllt sein.  
     * 
     * @return Neuer Zustand des Objekts
     */
    public AutorEntity updateAutor( AutorEntity autorNutzer ) {
    
        return _em.merge( autorNutzer );
    }
    
    
    /**
     * Gibt Autoren zurück, für die {@code ist_active=true} gilt, deren
     * letzte Anmeldung aber schon mehr als {@code anzahlMinuten}
     * zurückliegt.  Diese Autoren sollten aus Sicherheitsgründen
     * deaktiviert werden.
     * 
     * @param anzahlMinuten Anzahl der Minuten, die der Autor inaktiv 
     *                      gewesen sein muss 
     * 
     * @return Liste der inaktiven Autoren; kann leer sein, ist aber nicht
     *         {@code null}
     */
    public List<AutorEntity> getInaktiveAutoren( int anzahlMinuten ) {
        
        final LocalDateTime zeitSchwellwert = now().minus( anzahlMinuten, MINUTES );                                                           
        
        final CriteriaBuilder            cBuilder = _em.getCriteriaBuilder();
        final CriteriaQuery<AutorEntity> cQuery   = cBuilder.createQuery( AutorEntity.class );
        
        final Root<AutorEntity> rootAutor = cQuery.from( AutorEntity.class );
        
        final Predicate predikatIstAktiv        = cBuilder.isTrue(   rootAutor.get( "_istAktiv"        ) );
        final Predicate predikatLetzteAnmeldung = cBuilder.lessThan( rootAutor.get( "_letzteAnmeldung" ), 
                                                                     zeitSchwellwert );
        
        final Predicate predikatKombiniert = cBuilder.and( predikatIstAktiv, predikatLetzteAnmeldung );
        cQuery.select( rootAutor ).where( predikatKombiniert );
        
        final TypedQuery<AutorEntity> query = _em.createQuery( cQuery ); 
        
        return query.getResultList();
    }    
         
}
