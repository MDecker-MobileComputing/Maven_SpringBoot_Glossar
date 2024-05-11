package de.eldecker.dhbw.spring.glossar.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Glossareintrag in der zugehörigen DB-Tabelle.
 */
@Entity
@Table(name = "GLOSSAR_EINTRAEGE",
         indexes = { @Index( name = "idx_begriff", columnList = "begriff" ) }
)
@NamedQuery(name = "GlossarEntity.findByBegriff",
            query = "SELECT g FROM GlossarEntity g WHERE LOWER(g._begriff) = LOWER(:begriff)")
public class GlossarEntity {

    /**
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keinen Setter für dieses Attribut.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long _id;

    /**
     * Begriff, den der Glossareintrag erklärt.
     * Für diese Spalte wird explizit ein Index definiert, siehe Annotation an der Klasse.
     */
    @Column(name = "begriff")
    private String _begriff;

    /**
     * Text mit Erklärung zu {@code begriff}.
     * Standardmäßig wird ein String-Typ in der DB mit einer Länge von 255 Zeichen
     * angelegt. Da die Erklärung aber länger sein kann, wird hier die Länge auf
     * 9999 Zeichen erhöht.
     */
    @Column(name = "erklaerung", length = 9999)
    private String _erklaerung;

    /** Zeitpunkt (Datum + Uhrzeit) der Erzeugung des Eintrags. */
    @Column(name = "zeitpunkt_erzeugung")
    private LocalDateTime _zeitpunktErzeugung;

    /**
     * Zeitpunkt (Datum + Uhrzeit) der letzten Änderung; darf nie vor Zeitpunkt der Erzeugung liegen.
     * Wenn Eintrag nur erzeugt aber noch nicht geändert wurde, dann muss der Änderungszeitpunkt dem
     * Erzeugungszeitpunkt entsprechen.
     */
    @Column(name = "zeitpunkt_aenderung")
    private LocalDateTime _zeitpunktAenderung;


    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public GlossarEntity() {

        _begriff    = "";
        _erklaerung = "";
    }


    /**
     * Konstruktor, wenn nur die Attribute {@code id} und {@code begriff}
     * gefüllt werden sollen (z.B. für reine Anzeige der Glossarbegriffe
     * ohne Erklärung, aber mit Link auf Detailseite).
     *
     * @param id Key des Glossareintrags
     *
     * @param begriff Glossarbegriff.
     */
    public GlossarEntity(Long id, String begriff) {

        this( begriff, "", null, null );
        _id = id;
    }


    /**
     * Konstruktor, mit dem alle Attribute außer der ID gesetzt
     * werden können.
     */
    public GlossarEntity( String begriff,
                          String erklaerung,
                          LocalDateTime zeitpunktErzeugung,
                          LocalDateTime zeitpunktAenderung ) {

        _begriff            = begriff;
        _erklaerung         = erklaerung;
        _zeitpunktErzeugung = zeitpunktErzeugung;
        _zeitpunktAenderung = zeitpunktAenderung;
    }


    /**
     * Getter für ID (Primärschlüssel) der Entity; es gibt keine zugehörige Setter-Methode,
     * weil die ID von JPA gemanaged wird. Ist für noch nicht persistierte Entities nicht
     * gesetzt.
     *
     * @return ID/Primärschlüsselwert
     */
    public Long getId() {

        return _id;
    }


    /**
     * Getter für Begriff, der erklärt werden soll.
     *
     * @return Zu erklärender Begriff (Lemma)
     */
    public String getBegriff() {

        return _begriff;
    }


    /**
     * Setter für Begriff.
     *
     * @param begriff Zu erklärender Begriff (Lemma)
     */
    public void setBegriff( String begriff ) {

        _begriff = begriff;
    }


    /**
     * Getter für Erklärung von {@code begriff}.
     *
     * @return Erklärung von {@code begriff}
     */
    public String getErklaerung() {

        return _erklaerung;
    }


    /**
     * Setter für Erklärung von {@code begriff}.
     *
     * @param erklaerung Erklärung von {@code begriff}
     */
    public void setErklaerung( String erklaerung ) {

        _erklaerung = erklaerung;
    }


    /**
     * Getter für Zeitpunkt, zu dem der Eintrag angelegt wurde.
     *
     * @return Datum+Zeit, zu der Eintrag angelegt wurde
     */
    public LocalDateTime getZeitpunktErzeugung() {

        return _zeitpunktErzeugung;
    }

    /**
     * Setter für Zeitpunkt, zu dem der Eintrag angelegt wurde.
     *
     * @param zeitpunkt Datum+Zeit, zu der Eintrag angelegt wurde
     */
    public void setZeitpunktErzeugung( LocalDateTime zeitpunkt ) {

        _zeitpunktErzeugung = zeitpunkt;
    }


    /**
     * Getter für Zeitpunkt (Datum+Uhrzeit) der letzten Änderung.
     *
     * @return Zeitpunkt der letzten Änderung
     */
    public LocalDateTime getZeitpunktAenderung() {

        return _zeitpunktAenderung;
    }


    /**
     * Setter für Zeitpunkt (Datum+Uhrzeit) der letzten Änderung.
     * <br><br>
     *
     * Darf nie vor Zeitpunkt der Erzeugung liegen; Zeitpunkt der letzten
     * Änderungen wenn noch nie eine Änderung stattgefunden hat ist der
     * Zeitpunkt der Erzeugung.
     *
     * @param zeitpunktAenderung Zeitpunkt der letzten Änderung
     */
    public void setZeitpunktAenderung( LocalDateTime zeitpunktAenderung ) {

        _zeitpunktAenderung = zeitpunktAenderung;
    }


    /**
     * String-Darstellung des Objekts.
     *
     * @return String mit Begriff und Erklärung
     */
    @Override
    public String toString() {

        return _begriff + ": " + _erklaerung;
    }


    /**
     * Berechnet Hashwert für Objekt.
     *
     * @return Hashwert basierend auf allen Attribute außer der ID.
     */
    @Override
    public int hashCode() {

        return Objects.hash( _begriff,
                             _erklaerung,
                             _zeitpunktErzeugung,
                             _zeitpunktAenderung );
    }


    /**
     * Vergleicht alle Felder bis auf Primärschlüssel/ID.
     *
     * @return {@code true} gdw. {@code obj} auch eine Instanz von {@link GlossarEntity}
     *         ist und alle Werte (bis auf ID/Primärschlüssel) dieselben sind.
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

        final GlossarEntity other = (GlossarEntity) obj;

        return Objects.equals( _begriff           , other._begriff            ) &&
               Objects.equals( _erklaerung        , other._erklaerung         ) &&
               Objects.equals( _zeitpunktErzeugung, other._zeitpunktErzeugung ) &&
               Objects.equals( _zeitpunktAenderung, other._zeitpunktAenderung );
    }

}
