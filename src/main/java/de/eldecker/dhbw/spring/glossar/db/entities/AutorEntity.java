package de.eldecker.dhbw.spring.glossar.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Nutzer, der Glossareinträge anlegen
 * und ändern kann.
 */
@Entity
@Table(name = "AUTOREN")
public class AutorEntity {
    
    /**
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keinen Setter für dieses Attribut.
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long _id;
        
    /** Nutzername des Autors (mit der sich anmeldet). */
    @Column(name = "nutzername")
    private String _nutzername;    
    
    /** Passwort (darf für Produktivbetrieb nicht im Klartext gespeichert werden!) */
    @Column(name = "passwort")
    private String _passwort;    
    
    /** Ein Autorenkonto kann auch auf inaktiv geschaltet werden, dann ist dieses Attribut {@code false}. */
    @Column(name = "ist_aktiv")
    private boolean _istAktiv;
    
    /** Zeitpunkt (Datum+Uhrzeit) der letzten Anmeldung. */
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

        
    public String getPasswort() {
        
        return _passwort;
    }


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
