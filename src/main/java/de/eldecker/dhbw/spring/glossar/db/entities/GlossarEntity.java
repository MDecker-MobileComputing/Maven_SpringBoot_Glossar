package de.eldecker.dhbw.spring.glossar.db.entities;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


/**
 * Ein Objekt dieser Klasse repräsentiert einen Glossareintrag in der zugehörigen DB-Tabelle.
 */
@Entity
@Table(name = "GLOSSAR_EINTRAEGE")
public class GlossarEntity {
    
    /**  
     * Primärschlüssel, muss von uns nicht selbst befüllt werden, deshalb
     * gibt es auch keinen Setter für dieses Attribut. 
     */ 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long _id;
    
    /** Begriff, den der Glossareintrag erklärt. */
    @Column(name = "begriff")
    private String _begriff;
    
    /** Text mit Erklärung zu {@code begriff}. */
    @Column(name = "erklaerung")
    private String _erklaerung;

    @Column(name = "zeitpunkt_erzeugung")
    private LocalDateTime _zeitpunktErzeugung;
    
    
    /**
     * Default-Konstruktor, wird von JPA benötigt.
     */
    public GlossarEntity() {
        
        _begriff    = "";
        _erklaerung = "";
    }

    
    /**
     * Konstruktor, mit dem alle Attribute außer der ID gesetzt
     * werden können. 
     */
    public GlossarEntity(String begriff, String erklaerung) {
        
        _begriff            = begriff;
        _erklaerung         = erklaerung;
        _zeitpunktErzeugung = now();
    }
    
    
    public Long getId() {
        
        return _id;
    }

    public String getBegriff() {
        
        return _begriff;
    }


    public void setBegriff(String begriff) {
        
        _begriff = begriff;
    }


    public String getErklaerung() {
        
        return _erklaerung;
    }


    public void setErklaerung(String erklaerung) {
        
        _erklaerung = erklaerung;
    }
    
    public LocalDateTime getZeitpunktErzeugung() {
        
        return _zeitpunktErzeugung;
    }

    public void setZeitpunktErzeugung(LocalDateTime zeitpunkt) {
        
        _zeitpunktErzeugung = zeitpunkt;
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

}
