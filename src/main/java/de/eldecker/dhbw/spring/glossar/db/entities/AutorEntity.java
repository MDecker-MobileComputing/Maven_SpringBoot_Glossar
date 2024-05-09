package de.eldecker.dhbw.spring.glossar.db.entities;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

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
     * Liefert String-Repräsentation des Objekts zurück.
     * 
     * @return String enthält u.a. Name des Autors
     */
    @Override
    public String toString() {
    
        return "Autor mit Nutzername \"" + _nutzername + "\"";
    }
}
