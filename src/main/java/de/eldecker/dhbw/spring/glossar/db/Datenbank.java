package de.eldecker.dhbw.spring.glossar.db;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

import de.eldecker.dhbw.spring.glossar.db.entities.AutorEntity;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;
import de.eldecker.dhbw.spring.glossar.model.AutorArtikelAnzahl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


/**
 * Repository-Bean, die alle Methoden für Zugriff auf Datenbank enthält.
 * <br><br>
 *
 * Achtung: Alle Methodenaufrufe für schreibende Zugriff müssen in
 * einer Transaktion stattfinden, v.a. in einer mit {@code Transactional}
 * annotierten Methode.
 */
@Repository
public class Datenbank {

    private final static Logger LOG = LoggerFactory.getLogger( Datenbank.class );

    /** Zentrales Objekt von JPA für Datenbankzugriffe. */
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
     * <br><br>
     *
     * Intern wird die Methode {@code find()} der JPA-Entity-Manager-Klasse
     * verwendet, was aber nur funktioniert, wenn man den Primärschlüssel
     * kennt.
     *
     * @param id Primärschlüssel/ID von Glossareintrag
     *
     * @return Optional enthält Eintrag wenn gefunden; enthält auch bereits
     *         die referenzierten Autoren (eager loading).
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
     * Glossareintrag anhand Begriff suchen.
     * <br><br>
     *
     * Intern verwendet die Methode eine JPQL-Query, die als Annotation
     * in der Entity-Klasse {@link GlossarEntity} definiert ist.
     *
     * @param begriff Begriff nach dem (case-insensitive) gesucht wird
     *
     * @return Optional enthält Eintrag (mit allen Attributen gefüllt) wenn gefunden
     */
    public Optional<GlossarEntity> getEintragByBegriff( String begriff ) {

        final TypedQuery<GlossarEntity> query = _em.createNamedQuery( "GlossarEntity.findByBegriff",
                                                                      GlossarEntity.class );
        query.setParameter( "begriff", begriff );

        try {

            final GlossarEntity result = query.getSingleResult();
            return Optional.ofNullable( result );
        }
        catch( NoResultException ex ) {

            return Optional.empty();
        }
    }


    /**
     * Fügt einen neuen Glossareintrag in die Datenbank ein.
     *
     * @param eintrag Neuer Glossareintrag
     *
     * @return ID des neuen Eintrags
     */
    public long neuerGlossarEintrag( GlossarEntity eintrag ) {

        _em.persist( eintrag );
        LOG.info( "Neuer Glossareintrag mit ID={} in Datenbank gespeichert: {}",
                  eintrag.getId(),
                  eintrag.getBegriff() );

        return eintrag.getId();
    }


    /**
     * Glossareintrag auf DB aktualisieren.
     *
     * @param eintrag Zu aktualisierender Eintrag, die ID muss gefüllt sein.
     *
     * @return Neuer Zustand des Objekts
     */
    public GlossarEntity updateGlossarEintrag( GlossarEntity eintrag ) {

        final GlossarEntity ergebnis = _em.merge( eintrag );

        LOG.info( "Glossareintrag für Begriff \"{}\" aktualisiert.", eintrag.getBegriff() );

        return ergebnis;
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
     * <br><br>
     *
     * Intern wird die Methode {@code getSingleResult()} verwendet,
     * weil wir davon ausgehen, dass der Nutzername eindeutig ist.
     * Wenn es aber doch mehrere Nutzer mit gleichem Nutzernamen gibt,
     * dann wird eine {@link NoResultException} (ungeprüfte Exception)
     * geworfen.
     *
     * @param nutzername Nutzername nach dem gesucht wird (case-sensitive!)
     *
     * @return Optional ist leer, wenn Nutzer nicht gefunden; sonst ist der Nutzer
     *         mit allen Feldern enthalten.
     */
    public Optional<AutorEntity> getAutorByName(final String nutzername) {

        final String jpqlStr = "SELECT a FROM AutorEntity a WHERE a._nutzername = :nutzername";

        final TypedQuery<AutorEntity> query = _em.createQuery( jpqlStr, AutorEntity.class );

        query.setParameter( "nutzername", nutzername );

        try {

            final AutorEntity result = query.getSingleResult();
            return Optional.of( result );

        } catch ( NoResultException e ) {

            LOG.warn( "Kein Nutzer mit Nutzername \"{}\" gefunden.", nutzername );
            return Optional.empty();
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

        final AutorEntity ergebnis = _em.merge( autorNutzer );

        LOG.info( "Autor mit Nutzername \"{}\" aktualisiert.",
                  ergebnis.getNutzername() );

        return ergebnis;
    }


    /**
     * Anzahl der von jedem Autor angelegten Artikel zählen.
     *
     * @return Liste enthält für jeden Autor ein Objekt mit der Anzahl der Artikel, die er ganz neu angelegt hat;
     *         es sind nur Autoren enthalten, die mindestens einen Artikel angelegt haben.
     *         Die Liste ist nach Anzahl der Artikel absteigend sortiert.
     */
    public List<AutorArtikelAnzahl> getGlossarCountPerAuthor() {

        final TypedQuery<AutorArtikelAnzahl> query =
                    _em.createNamedQuery( "AutorEntity.GlossarCountPerAuthor",
                              AutorArtikelAnzahl.class );

        final List<AutorArtikelAnzahl> results = query.getResultList();

        return results;
    }


    /**
     * Gibt Autoren zurück, für die {@code ist_active=true} gilt, deren
     * letzte Anmeldung aber schon mehr als {@code anzahlMinuten}
     * zurückliegt. Diese Autoren sollen aus Sicherheitsgründen
     * deaktiviert werden.
     * <br><br>
     *
     * Die Methode verwendet intern die <i>Criteria API</i> anstelle
     * von JPQL.
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

        final Order orderLetzteAnmeldung = cBuilder.asc( rootAutor.get("_letzteAnmeldung") );

        cQuery.select( rootAutor ).where( predikatKombiniert ).orderBy( orderLetzteAnmeldung );

        final TypedQuery<AutorEntity> query = _em.createQuery( cQuery );

        return query.getResultList();
    }

}
