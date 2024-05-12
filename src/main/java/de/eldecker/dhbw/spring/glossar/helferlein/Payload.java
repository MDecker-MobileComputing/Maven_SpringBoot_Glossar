package de.eldecker.dhbw.spring.glossar.helferlein;

import static java.lang.Long.parseLong;

import java.util.Optional;


/** 
 * Zielklasse für Deserialisierung der über HTTP-POST-Request empfangenen Payload.
 * 
 * @param id ID des zu ändernden Eintrag, wird nach {@code long} geparst
 *  
 * @param begriff Begriff des Eintrags (darf nicht leer sein)
 *  
 * @param erklaerung Erklärung zu {@code begriff}
 */
public record Payload( String id, 
                       String begriff, 
                       String erklaerung ) { 
    
    /**
     * Methode parst die ID (wenn vorhanden) von {@code String} nach {@code long}.
     * Für neue Einträge ist keine ID gesetzt, sondern nur für Änderungen.
     * 
     * @return Optional enthält ID des Eintrags wenn die ID gesetzt ist
     *         und nach {@code long} geparst werden kann.
     */
    public Optional<Long> holeID() {
        
        if ( id == null || id.isBlank() ) {
            
            return Optional.empty();
        }
        
        try {
            
            long idLong = parseLong( id );
            return Optional.of( idLong );
        }
        catch ( NumberFormatException ex ) {
            
            return Optional.empty(); 
        }
    }
}
