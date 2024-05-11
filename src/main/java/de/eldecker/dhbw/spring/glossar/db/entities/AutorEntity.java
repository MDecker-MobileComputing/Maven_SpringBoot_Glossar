package de.eldecker.dhbw.spring.glossar.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.time.LocalDateTime.ofEpochSecond;
import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Nutzer, der Glossareinträge anlegen
 * und ändern kann.
 */
@Entity
@Table(name = "AUTOREN",
         indexes = { @Index( name = "idx_nutzername", columnList = "nutzername" ) }
)
public class AutorEntity {

    /** Dummy-Datum/Zeit am 1.1.1970 als Wert für "Nie angemeldet". */
    public static final LocalDateTime NIE_ANGEMELDET_DATUM = ofEpochSecond( 0, 0, UTC );

    /**
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keinen Setter für dieses Attribut.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long _id;

    /**
     * Nutzername des Autors (mit der sich anmeldet).
     * <br><br>
     * Für diese Spalte wird explizit ein Index definiert, siehe Annotation an der Klasse.
     */
    @Column(name = "nutzername")
    private String _nutzername;

    /** Passwort (darf für Produktivbetrieb nicht im Klartext gespeichert werden!) */
    @Column(name = "passwort")
    private String _passwort;

    /** Ein Autorenkonto kann auch auf inaktiv geschaltet werden, dann ist dieses Attribut {@code false}. */
    @Column(name = "ist_aktiv")
    private boolean _istAktiv;

    /**
     * Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung; wenn der Wert im Jahr 1970 liegt, dann
     * gab es noch nie eine Anmeldung.
     */
    @Column(name = "letzte_anmeldung")
    private LocalDateTime _letzteAnmeldung;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public AutorEntity() {

        _nutzername      = "";
        _istAktiv        = false;
        _letzteAnmeldung = null;
    }

    /**
     * Konstruktor, mit dem alle Attribute bis auf die ID (Primärschüssel, wird von JPA vergeben)
     * gesetzt werden.
     */
    public AutorEntity( String nutzername, String passwort, boolean istAktiv, LocalDateTime letzteAnmeldung ) {

        _nutzername      = nutzername;
        _passwort        = passwort;
        _istAktiv        = istAktiv;
        _letzteAnmeldung = letzteAnmeldung;
    }


    /**
     * Getter für ID (Primärschlüssel) der Entity; es gibt keine zugehörige Setter-Methode!
     *
     * @return Primärschlüssel
     */
    public Long getId() {

        return _id;
    }


    /**
     * Getter für Anmeldename des Nutzers
     *
     * @return Anmeldename des Nutzers, z.B. "alice"
     */
    public String getNutzername() {

        return _nutzername;
    }

    /**
     * Setter für Anmeldename des Nutzers
     *
     * @param nutzername Anmeldename des Nutzers, z.B. "alice"
     */
    public void setNutzername( String nutzername ) {

        _nutzername = nutzername;
    }


    /**
     * Getter für Passwort.
     * <br>
     * <b>Passwort darf im Produktivbetrieb nie im Klartext gespeichert werden!</b>
     *
     * @return Passwort für Nutzer
     */
    public String getPasswort() {

        return _passwort;
    }


    /**
     * Setter für Passwort
     *
     * @param passwort Passwort im Klartext (im Produktivbetrieb ist das nicht erlaubt!)
     */
    public void setPasswort( String passwort ) {

        _passwort = passwort;
    }


    /**
     * Getter für Flag "ist aktiv".
     *
     * @return {@code true}, gdw. der Nutzer sich noch anmelden kann;
     *         {@code false}, gdw. der Nutzer gesperrt ist.
     */
    public boolean isIstAktiv() {

        return _istAktiv;
    }


    /**
     * Setter für Flag "ist aktiv".
     *
     * @param istAktiv {@code true}, gdw. der Nutzer sich noch anmelden kann;
     *                 {@code false}, gdw. der Nutzer gesperrt ist.
     */
    public void setIstAktiv( boolean istAktiv ) {

        _istAktiv = istAktiv;
    }


    /**
     * Getter für Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung des Nutzers;
     * wenn Datum im Jahr 1970 liegt, dann hat sich der Nutzer noch nie angemeldet.
     * Wenn dieser Zeitpunkt zu lange in der Vergangenheit liegt, dann kann der
     * Nutzer automatisch auf inaktiv gesetzt werden.
     *
     * @return Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung
     */
    public LocalDateTime getLetzteAnmeldung() {

        return _letzteAnmeldung;
    }


    /**
     * Getter für Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung des Nutzers;
     * wenn der Nutzer sich noch nie angemeldet hat, dann auf
     * {@link #NIE_ANGEMELDET_DATUM} (Datum im Jahr 1970) setzen.
     *
     * @param letzteAnmeldung Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung
     */
    public void setLetzteAnmeldung( LocalDateTime letzteAnmeldung ) {

        _letzteAnmeldung = letzteAnmeldung;
    }


    /**
     * Liefert String-Repräsentation des Objekts zurück.
     *
     * @return String enthält u.a. Name des Autors
     */
    @Override
    public String toString() {

        return "Autor mit Nutzername \"" + _nutzername + "\"";
    }


    /**
     * Berechnet Hashwert für das Objekt.
     *
     * @return Hashwert für alle Attribute des Objekts (bis auf ID) ein.
     */
    @Override
    public int hashCode() {

        return Objects.hash( _nutzername,
                             _passwort,
                             _istAktiv,
                             _letzteAnmeldung );
    }


    /**
     * Vergleicht dieses Objekt mit {@code obj}.
     *
     * @return {@code true} gdw. wenn {@code obj} ist eine Instanz von {@code AutorEntity}
     *         und alle Attribute bis auf den Primärschlüssel/ID denselben Wert haben.
     */
    @Override
    public boolean equals( Object obj ) {

        if ( this == obj ) {

            return true;
        }
        if ( obj == null ) {

            return false;
        }
        if ( getClass() != obj.getClass() ) {

            return false;
        }

        final AutorEntity other = (AutorEntity) obj;

        return _istAktiv == other._istAktiv                                &&
                Objects.equals( _letzteAnmeldung, other._letzteAnmeldung ) &&
                Objects.equals( _nutzername     , other._nutzername      ) &&
                Objects.equals( _passwort       , other._passwort        );
    }

}
