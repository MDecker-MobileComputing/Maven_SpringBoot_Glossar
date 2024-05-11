package de.eldecker.dhbw.spring.glossar.web;

import static java.lang.Long.parseLong;
import static java.lang.String.format;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.eldecker.dhbw.spring.glossar.db.Datenbank;
import de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity;


/**
 * Controller (kein RestController!), der die Anfragen für die Thymeleaf-Views bearbeitet.
 * Alle Pfade beginnen mit {@code /app/}.
 * Die Mapping-Methoden geben immer den Namen (ohne Datei-Endung) der darzustellenden Template-Datei
 * zurück, der im Ordner {@code src/main/resources/templates/} gesucht wird. 
 * Neben Mapping-Methoden enthält die Klasse aber auch noch Hilfsmethoden, die von mehreren
 * Mapping-Methoden aufgerufen werden.
 * <br><br>
 * 
 * Einige der Attribut-Keys (Konstanten {@code ATTRIBUT_...}) werden in mehreren Templates
 * verwendet.
 */
@Controller
@RequestMapping( "/app" )
public class ThymeleafWebController {

    private static final Logger LOG = LoggerFactory.getLogger( ThymeleafWebController.class );

    /** 
     * Attribut-Key für bool'schen Wert, der gdw. {@code true} ist, wenn der Nutzer angemeldet;
     * siehe auch {@link #ATTRIBUT_NUTZER}. 
     */
    private static final String ATTRIBUT_ANGEMELDET = "ist_angemeldet";

    /** 
     * Attribut-Key für Template: referenziert Nutzername oder (wenn kein Nutzer angemeldet) leeren String.
     * Für Ersteres referenziert Key {@link #ATTRIBUT_ANGEMELDET} den Wert {@code true},
     * sonst {@code false}.  
     */
    private static final String ATTRIBUT_NUTZER = "nutzername";

    /** Attribut-Key für Platzhalter in Template, der die Liste der Einträge enthält. */
    private static final String ATTRIBUT_EINTRAEGE_LISTE = "eintraege";

    /** Attribut-Key für Platzhalter in Template, der den Glossarbegriff (Lemma) enthält.  */ 
    private static final String ATTRIBUT_BEGRIFF = "begriff";

    /** Attribut-Key für Platzhalter in Template, der die Erklärung für einen Glossarbegriff enthält.  */
    private static final String ATTRIBUT_ERKLAERUNG = "erklaerung";

    /** Attribut-Key für Platzhalter in Template, der den Zeitpunkt der Erzeugung des Eintrags enthält. */
    private static final String ATTRIBUT_ZEITPUNKT_ANGELEGT = "zeitpunkt_angelegt";

    /** Attribut-Key für Platzhalter in Template, der den Zeitpunkt der letzten Änderung eines Eintrags enthält. */
    private static final String ATTRIBUT_ZEITPUNKT_GEAENDERT = "zeitpunkt_geaendert";

    /** Attribut-Key für Platzhalter in Template, der die ID des Eintrags enthält. */
    private static final String ATTRIBUT_ID = "eintrag_id";

    /** Attribut-Key für Platzhalter in Template, der ggf. eine Fehlermeldung enthält. */
    private static final String ATTRIBUT_FEHLERMELDUNG = "fehlermeldung";

    /** Attribut-Key für Platzhalter in Template, der einen dynamisch erzeugten Seitentitel enthält. */
    private static final String ATTRIBUT_SEITENTITEL = "seitentitel";
    

    /** Repository-Bean für Zugriff auf Datenbank. */
    private final Datenbank _datenbank;


    /**
     * Konstruktor für <i>Dependency Injection</i>.
     */
    @Autowired
    public ThymeleafWebController( Datenbank datenbank ) {

        _datenbank = datenbank;
    }


    /**
     * Übersichtsseite mit Liste der Glossareinträge (aber ohne Erklärungen) anzeigen.
     *
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     *
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden.
     *
     * @return "hauptseite" (Name von Template-Datei ohne Datei-Endung)
     */
    @GetMapping( "/hauptseite" )
    public String hauptseiteAnzeigen( Authentication authentication,
                                      Model model ) {

        authentifzierungAufloesen( authentication, model );

        final List<GlossarEntity> begriffListe = _datenbank.getGlossarBegriffe();
        model.addAttribute( ATTRIBUT_EINTRAEGE_LISTE, begriffListe );

        return "hauptseite";
    }

      
    /**
     * Einzelnen Glossareintrag anzeigen.
     *
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     *
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden.
     *
     * @param idStr ID (Nummer) des Glossareintrags als String, sollte sich nach {@code long}
     *              parsen lassen
     *
     * @return "eintrag" (Name von Template-Datei ohne Datei-Endung)
     */
    @GetMapping( "/eintrag/{id}")
    public String eintragAnzeigen( Authentication authentication,
                                   Model model,
                                   @PathVariable("id") String idStr ) {

        authentifzierungAufloesen( authentication, model );

        final Optional<Long> idOptional = parseID( idStr );
        if ( idOptional.isEmpty() ) {
            
            return "eintrag";
        }
        
        long id = idOptional.get();
        
        holeEntityFuerID( id, model );

        return "eintrag";
    }
        

    /**
     * Neuen Glossareintrag anlegen; nur für authentifzierte Nutzer!
     *
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     *
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden.
     *
     * @return "neu_bearbeiten" (Name von Template-Datei ohne Datei-Endung)
     *         oder "fehler" wenn Nutzer nicht angemeldet ist.
     */
    @GetMapping( "/neu" )
    public String eintragErzeugen( Authentication authentication,
                                   Model model ) {
        
        final boolean istNutzerAngemeldet = authentifzierungAufloesen ( authentication, model );
        if ( istNutzerAngemeldet == false) {
            
            // sollte nie passieren wenn Spring Security richtig konfiguriert
            LOG.warn( "Unangemeldeter Nutzer hat Pfad /neu aufgerufen." );            
            return "fehler";
        }
        
        model.addAttribute( ATTRIBUT_SEITENTITEL, "Neuen Eintrag im Glossar anlegen" );

        return "neu_bearbeiten";
    }


    /**
     * Einzelnen Glossareintrag bearbeiten; nur für authentifizierte Nutzer!
     *
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer.
     *
     * @param model Objekt, in das die Werte für die Platzhalter in der Template-Datei
     *              geschrieben werden.
     *
     * @param idStr ID (Nummer) des Glossareintrags als String, sollte sich nach {@code long}
     *              parsen lassen
     *
     * @return Name (ohne Suffix) der Template-Datei {@code bearbeiten.html}, die angezeigt
     *         werden soll; wird in Ordner {@code src/main/resources/templates/} gesucht.
     */
    @GetMapping( "/bearbeiten/{id}")
    public String eintragBearbeiten( Authentication authentication,
                                     Model model,
                                     @PathVariable("id") String idStr ) {

        final Optional<Long> idOptional = parseID( idStr );
        if ( idOptional.isEmpty() ) {
            
            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, 
                                "Seite mit ungültigem String \"" + idStr + 
                                "\"für Pfadparameter für ID aufgerufen." );
            return "eintrag";
        }               
        
        final long idLong = idOptional.get();
        
        final boolean eintragGefunden = holeEntityFuerID( idLong, model );
        if ( eintragGefunden == false ) {
            
            // ATTRIBUT_FEHLERMELDUNG wurde in holeEntityFuerID() gesetzt
            return "eintrag";
        }
        
        
        final boolean istNutzerAngemeldet = authentifzierungAufloesen ( authentication, model );
        if ( istNutzerAngemeldet == false) {
            
            // sollte nie passieren wenn Spring Security richtig konfiguriert
            LOG.warn( "Unangemeldeter Nutzer hat Pfad /bearbeiten aufgerufen." );            
            return "fehler";
        }
                
        model.addAttribute( ATTRIBUT_SEITENTITEL, "Eintrag im Glossar bearbeiten" );
        model.addAttribute( ATTRIBUT_ID         , idStr                           );

        return "neu_bearbeiten";
    }
    
    
    /** 
     * <b>Hilfsmethode:</b> 
     * Platzhalterwerte in {@code model} in Abhängigkeit ob ein Nutzer
     * angemeldet ist oder nicht setzen.
     * <br><br>
     * 
     * Die in der Methode dem Argument {@code model} hinzugefügten 
     * Key-Value-Paare sind wegen "Call By Reference" auch für den
     * Aufrufer sichtbar. 
     * 
     * @param authentication Objekt zur Abfrage, ob Nutzer authentifiziert ist;
     *                       ACHTUNG: ist {@code null} für unangemeldete Nutzer. 
     * 
     * @param model Objekt, in das die Werte für die Platzhalter in der 
     *              Template-Datei geschrieben werden. Es werden
     *              Werte für die folgenden Keys gesetzt:
     *              {@link #ATTRIBUT_NUTZER},
     *              {@link #ATTRIBUT_ANGEMELDET}
     * 
     * @return {@code true} gdw. ein Nutzer angemeldet ist
     */
    private boolean authentifzierungAufloesen( Authentication authentication,
                                               Model model ) {
        
        final boolean nutzerIstAngemeldet = authentication != null &&
                                            authentication.isAuthenticated();
        if ( nutzerIstAngemeldet ) {
        
            final String nutzername = authentication.getName();
                        
            model.addAttribute( ATTRIBUT_NUTZER    , nutzername );
            model.addAttribute( ATTRIBUT_ANGEMELDET, true       );
        
        } else {

            model.addAttribute( ATTRIBUT_NUTZER    , ""    );
            model.addAttribute( ATTRIBUT_ANGEMELDET, false );
        }          
        
        return nutzerIstAngemeldet;
    }
    
    
    /**
     * <b>Hilfsmethode:</b>
     * Parsen von String der als Pfadparameter übergeben wurde und die ID (Long-Zahl) eines
     * Glossareintrags enthalten sollte.
     * 
     * @param idString String, der als URL-Parameter für die ID eines Eintrags übergeben wurde
     * 
     * @return Optional enthält die ID als Long-Objekt wenn Sie geparst werden konnte
     */
    private Optional<Long> parseID( String idString ) {
        
        try {
        
            long idLong = parseLong( idString );
            
            return Optional.of( idLong );
        }
        catch ( NumberFormatException ex ) {
                                    
            LOG.error( "Als Pfadparameter für ID übergebener String ist keine gültige Long-Zahl: {}", 
                       idString );
            return Optional.empty();
        }        
    }
    
    
    /**
     * Glossareintrag anhand ID von Datenbank holen und Attribute mit zugehörigen    
     * Werten in {@code mode} setzen.
     * <br><br>
     * 
     * Im Argument {@code model} werden Werte für die folgenden Keys gesetzt:
     * <ul>
     * <li>{@link #ATTRIBUT_BEGRIFF}</li>
     * <li>{@link #ATTRIBUT_ERKLAERUNG}</li>
     * <li>{@link #ATTRIBUT_ZEITPUNKT_ANGELEGT}</li>
     * <li>{@link #ATTRIBUT_ZEITPUNKT_GEAENDERT}</li>
     * <li>{@link #ATTRIBUT_ID}</li>
     * <li>{@link #ATTRIBUT_FEHLERMELDUNG}</li>
     * </ul>
     * Für alle Keys wird zumindest ein leerer String gesetzt.
     * Wenn kein Glossareintrag mit {@code id} gefunden wurde, dann werden die Attribute mit Begriff, Erklärung und 
     * den Zeitpunkten mit einem leeren String gefüllt, dafür wird die Fehlermeldung gefüllt. 
     * Das Attribut für den Änderungszeitpunkt wird nur dann mit einen Zeitpunkt gefüllt, wenn dieser Zeitpunkt vom 
     * nicht dem Erzeugungszeitpunkt entspricht; wenn die beiden Zeitpunkte gleich sind, dann wird als Änderungszeitpunkt 
     * ein leerer String gesetzt.   
     * 
     * @param id ID des Glossareintrags
     * 
     * @param model In diesem Argument gesetzte Key-Value-Paare sind wegen "Call By Reference" für den Aufrufer sichtbar;
     *              siehe Beschreibung für Methode für die gesetzten Attribute.              
     * 
     * @return {@code true} gdw. ein Glossareintrag mit {@code id} gefunden wurde und
     *         die entsprechenden Key-Value-Paare in {@code model} geschrieben wurden.
     */
    private boolean holeEntityFuerID( long id, Model model ) {
        
        final Optional<GlossarEntity> entityOptional = _datenbank.getEintragById( id );
        if ( entityOptional.isEmpty() ) {
            
            model.addAttribute( ATTRIBUT_BEGRIFF            , "" );
            model.addAttribute( ATTRIBUT_ERKLAERUNG         , "" );
            model.addAttribute( ATTRIBUT_ZEITPUNKT_ANGELEGT , "" );
            model.addAttribute( ATTRIBUT_ZEITPUNKT_GEAENDERT, "" );
            model.addAttribute( ATTRIBUT_ID                 , "" );
            
            final String fehlerText = 
                    format( "Kein Glossareintrag mit ID=%d (Pfadparameter) gefunden.", id );                          
                                       
            
            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, fehlerText );
            LOG.error( fehlerText );
            
            return false;
        }
        
        final GlossarEntity glossarEintrag = entityOptional.get();
        
        LOG.info( "Glossareintrag für ID={} (Pfadparameter) gefunden: \"{}\"", 
                  id, glossarEintrag.getBegriff() );
       
        model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "" );

        model.addAttribute( ATTRIBUT_ID                 , id                             );
        model.addAttribute( ATTRIBUT_BEGRIFF            , glossarEintrag.getBegriff()    );
        model.addAttribute( ATTRIBUT_ERKLAERUNG         , glossarEintrag.getErklaerung() );
        
        final LocalDateTime zeitpunktAngelegt  = glossarEintrag.getZeitpunktErzeugung();
        final LocalDateTime zeitpunktAenderung = glossarEintrag.getZeitpunktAenderung();
        
        model.addAttribute( ATTRIBUT_ZEITPUNKT_ANGELEGT , zeitpunktAngelegt );
        if ( zeitpunktAngelegt.isEqual( zeitpunktAenderung )) {
        
            model.addAttribute( ATTRIBUT_ZEITPUNKT_GEAENDERT, "" );
            
        } else {
            
            model.addAttribute( ATTRIBUT_ZEITPUNKT_GEAENDERT, zeitpunktAenderung );
        }
                        
        return true;                
    }
    
}
