package de.eldecker.dhbw.spring.glossar.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;


/**
 * Controller (kein RestController!), der die Anfragen für die Thymeleaf-Views bearbeitet.
 * Alle Pfade beginnen mit {@code /app/}.
 */
@Controller
@RequestMapping( "/app" )
public class ThymeleafWebController {

    private static final String ATTRIBUT_NAME_ANZAHL = "anzahl_eintraege";
    
    /** Repository-Bean für Zugriff auf Datenbank. */
    private final Datenbank _datenbank;
    
    
    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */    
    @Autowired
    public ThymeleafWebController( Datenbank datenbank ) {
        
        _datenbank = datenbank;
    }
    
    @GetMapping( "/hauptseite" )
    public String hauptseiteAnzeige( Authentication authentication,
                                     Model model ) {
                
        final long anzahl = _datenbank.getAnzahlGlossareintraege();        
        model.addAttribute( ATTRIBUT_NAME_ANZAHL, anzahl );
        
        return "hauptseite";
    }
    
}
