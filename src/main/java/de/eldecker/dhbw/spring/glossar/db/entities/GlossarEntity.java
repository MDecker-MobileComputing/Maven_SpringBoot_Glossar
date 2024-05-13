package de.eldecker.dhbw.spring.glossar.db.entities;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Glossareintrag in der zugehörigen DB-Tabelle.
 */
@Entity
@Table(
	name = "GLOSSAR_EINTRAEGE",
    indexes = { @Index( name = "idx_begriff", columnList = "begriff" ) }
)
@NamedQuery(name = "GlossarEntity.findByBegriff",
            query = "SELECT g FROM GlossarEntity g WHERE LOWER(g._begriff) = LOWER(:begriff)"
)
@NamedQuery(name = "GlossarEntity.getGlossarEintraegeFuerAutor",
            query = "SELECT new de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity( g._id, g._begriff ) " +            
                    "FROM GlossarEntity g " +
                    "WHERE g._autorErzeugung._nutzername = :autorName " +
                    "ORDER BY g._begriff ASC"            
)
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
     * Eine Glossar-Entity verweist auf den einen Autor, der den Eintrag ursprünglich angelegt hat; 
     * dieser Autor ändert sich auch nicht, wenn der Eintrag von anderen Nutzern geändert wird.
     * Verschiedene Glossar-Entity-Objekte können aber denselben Autor referenzieren, deshalb
     * handelt es sich um eine "Many-to-one"-Beziehung (siehe gleichnamige Annotation).
     * <br><br>
     * 
     * Die Information für diese Beziehung wird in der Tabelle mit den Glossareinträgen gespeichert,
     * die Glossareinträge sind also die "owning side" und kriegen daher die {@code JoinColumn} annotation.
     * <br><br>
     * 
     * Der definierte Fetch-Typ {@code EAGER} ist der Default-Wert (könnte also auch weggelassen
     * werden ohne das Verhalten des Programms zu verändern) und sorgt dafür, dass beim
     * Laden der Entity mit der Methode {@code find()} von {@code EntityManager} sofort geladen
     * wird (bei {@code LAZY} würde zuerst ein Proxy-Objekt für den Autor referenziert, das
     * beim ersten Zugriff durch die Werte von der Datenbank ersetzt wird). 
     */
    @ManyToOne( fetch = EAGER )
    @JoinColumn( name = "autor_erzeuger_fk", referencedColumnName = "id" )
    private AutorEntity _autorErzeugung;            

    /**
     * Referenz auf den letzten Änderer; für neu angelegte Einträge ist der letzte Änderer
     * derselbe Autor wie in {@link #_autorErzeugung}. 
     */
    @ManyToOne( fetch = EAGER )
    @JoinColumn( name = "autor_aenderer_fk", referencedColumnName = "id" )
    private AutorEntity _autorAenderung;            

    
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
    public GlossarEntity( Long id, String begriff ) {

        this( begriff, "", null, null, null, null );
        _id = id;
    }


    /**
     * Konstruktor, mit dem alle Attribute außer der ID gesetzt
     * werden können.
     */
    public GlossarEntity( String begriff,
                          String erklaerung,
                          LocalDateTime zeitpunktErzeugung,
                          LocalDateTime zeitpunktAenderung,
                          AutorEntity autorErzeugung,
                          AutorEntity autorAenderung ) {

        _begriff            = begriff;
        _erklaerung         = erklaerung;
        _zeitpunktErzeugung = zeitpunktErzeugung;
        _zeitpunktAenderung = zeitpunktAenderung;
        _autorErzeugung     = autorErzeugung;
        _autorAenderung     = autorAenderung;
    }

    
    /**
     * Convenience-Konstruktor: Es können alle Attribute außer der ID gesetzt
     * werden, aber {@code zeitpunktErzeugung} wird auch als Änderungszeitpunkt
     * verwendet, und {@code autorErzeugung} wird auch als Autor der letzten
     * Änderung verwendet; dies ist sinnvoll, wenn ein Eintrag ganz neu angelegt
     * wird.
     */
    public GlossarEntity( String begriff,
                          String erklaerung,
                          LocalDateTime zeitpunktErzeugung,
                          AutorEntity autorErzeugung ) {
        
        _begriff            = begriff;
        _erklaerung         = erklaerung;
        _zeitpunktErzeugung = zeitpunktErzeugung;
        _zeitpunktAenderung = zeitpunktErzeugung; // sic!
        _autorErzeugung     = autorErzeugung;
        _autorAenderung     = autorErzeugung; // sic !
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
     * Getter für den Autor, der den Eintrag neu angelegt hat.
     * 
     * @return Autor/Nutzer
     */
    public AutorEntity getAutorErzeugung() {
        
        return _autorErzeugung;
    }


    /**
     * Setter für den Autor, der den Eintrag neu angelegt hat.
     * 
     * @param autorErzeugung Autor/Nutzer
     */
    public void setAutorErzeugung( AutorEntity autorErzeugung ) {
        
        _autorErzeugung = autorErzeugung;
    }
    
    
    /**
     * Getter für den Autor, der die letzte Änderung am Eintrag vorgenommen hat.
     * 
     * @return Autor/Nutzer
     */
    public AutorEntity getAutorAenderung() {
        
        return _autorAenderung;
    }
    
    
    /**
     * Getter für den Autor, der die letzte Änderung am Eintrag vorgenommen hat.
     * 
     * @param autorAenderung Autor/Nutzer
     */
    public void setAutorAenderung( AutorEntity autorAenderung ) {
        
        _autorAenderung = autorAenderung;
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
     * <br><br>
     * 
     * Die ID darf nicht ein Input-Wert für die Hash-Berechnung sein,
     * weil Sie für neue Objekte erst beim Persistieren von JPA
     * gesetzt wird. 
     *
     * @return Hashwert basierend auf allen Attribute außer der ID.
     */        
    @Override
    public int hashCode() {

        return Objects.hash( _begriff,
                             _erklaerung,
                             _zeitpunktErzeugung,
                             _zeitpunktAenderung,
                             _autorErzeugung,
                             _autorAenderung );
    }


    /**
     * Vergleicht alle Felder bis auf Primärschlüssel/ID.
     * <br><br>
     * 
     * Vergleich von ID alleine reicht nicht aus, weil diese für neue Objekte
     * erst beim Persistieren gesetzt wird; entsprechend darf die ID auch
     * nicht beim Vergleich der Attribute berücksichtigt werden.
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
               Objects.equals( _zeitpunktAenderung, other._zeitpunktAenderung ) &&
               Objects.equals( _autorErzeugung    , other._autorErzeugung     ) &&
               Objects.equals( _autorAenderung    , other._autorAenderung     );
    }

}
