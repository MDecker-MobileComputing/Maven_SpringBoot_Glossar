package de.eldecker.dhbw.spring.glossar.web;

import static java.lang.Long.parseLong;

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
 * Die Methoden geben immer den Namen (ohne Datei-Endung) der darzustellenden Template-Datei
 * zurück, der im Ordner {@code src/main/resources/templates/} gesucht wird.
 */
@Controller
@RequestMapping( "/app" )
public class ThymeleafWebController {

    private static final Logger LOG = LoggerFactory.getLogger( ThymeleafWebController.class );

    /** Attribut-Key für bool'schen Wert, der gdw. {@code true} ist, wenn der Nutzer angemeldet. */
    private static final String ATTRIBUT_ANGEMELDET = "ist_angemeldet";

    /** Attribut-Key für Template "hauptseite"; referenziert leeren String, wenn kein Nutzer angemeldet. */
    private static final String ATTRIBUT_NUTZER = "nutzername";

    /** Attribut-Key für Template "hauptseite" mit Liste der Einträge. */
    private static final String ATTRIBUT_EINTRAEGE_LISTE = "eintraege";

    /** Attribut-Key für Template "begriff" mit Begriff (Lemma) das erklärt werden soll. */
    private static final String ATTRIBUT_BEGRIFF = "begriff";

    /** Attribut-Key für Template "eintrag" mit Erklärung zu einem Glossareintrag. */
    private static final String ATTRIBUT_ERKLAERUNG = "erklaerung";

    /** Attribut-Key für Template "eintrag" mit ID von gerade angezeigtem Glossareintrag. */
    private static final String ATTRIBUT_ID = "eintrag_id";

    /** Attribut-Key für Template "eintrag" und "bearbeiten" mit Fehlermeldung. */
    private static final String ATTRIBUT_FEHLERMELDUNG = "fehlermeldung";

    /** Attribut-Key für Template "neu_bearbeiten" mit Seitentitel. */
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

        final boolean nutzerIstAngemeldet = authentication != null &&
                                            authentication.isAuthenticated();
        if ( nutzerIstAngemeldet ) {

            final String nutzername = authentication.getName();
            LOG.info( "Zugriff auf Hauptseite von Nutzer \"{}\".", nutzername );
            model.addAttribute( ATTRIBUT_NUTZER    , nutzername );
            model.addAttribute( ATTRIBUT_ANGEMELDET, true       );

        } else {

            LOG.info( "Zugriff auf Hauptseite von unangemeldetem Nutzer." );
            model.addAttribute( ATTRIBUT_NUTZER    , ""    );
            model.addAttribute( ATTRIBUT_ANGEMELDET, false );
        }

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

        final boolean nutzerIstAngemeldet = authentication != null &&
                                            authentication.isAuthenticated();
        if ( nutzerIstAngemeldet ) {

            final String nutzername = authentication.getName();
            LOG.info( "Zugriff auf Eintrag mit ID={} von Nutzer \"{}\".", idStr, nutzername );
            model.addAttribute( ATTRIBUT_NUTZER    , nutzername );
            model.addAttribute( ATTRIBUT_ANGEMELDET, true       );

        } else {

            LOG.info( "Zugriff auf Eintrag mit ID={} von anonymen Nutzer.", idStr );
            model.addAttribute( ATTRIBUT_NUTZER    , ""    );
            model.addAttribute( ATTRIBUT_ANGEMELDET, false );
        }

        long idLong = -1;
        try {

            idLong = parseLong( idStr ); // throws NumberFormatException

            final Optional<GlossarEntity> entityOptional = _datenbank.getEintragById( idLong );
            if ( entityOptional.isPresent() ) {

                final GlossarEntity entity = entityOptional.get();

                model.addAttribute( ATTRIBUT_BEGRIFF   , entity.getBegriff()    );
                model.addAttribute( ATTRIBUT_ERKLAERUNG, entity.getErklaerung() );
                model.addAttribute( ATTRIBUT_ID        , entity.getId()         );

                LOG.info( "Glossareintrag {} aufgelöst: {}", idLong, entity.getBegriff() );

            } else {

                model.addAttribute( ATTRIBUT_BEGRIFF   , "???" );
                model.addAttribute( ATTRIBUT_ERKLAERUNG, ""    );

                model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "Kein Eintrag mit ID=" + idLong + " gefunden." );
                LOG.error( "Kein Glossareintrag mit ID={} gefunden.", idLong );
            }

        } catch ( NumberFormatException ex ) {

            model.addAttribute( ATTRIBUT_BEGRIFF      , "???" );
            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "ID \"" + idStr + "\" übergeben, ist kein gültiger long-Wert." );
            LOG.error( "Pfadparameter für ID \"{}\" konnte nicht nach long geparst werden.", idStr, ex);
        }

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
     *         oder "hauptseite" wenn Nutzer nicht angemeldet ist.
     */
    @GetMapping( "/neu" )
    public String eintragErzeugen( Authentication authentication,
                                   Model model ) {

        model.addAttribute( ATTRIBUT_SEITENTITEL, "Neuen Eintrag im Glossar anlegen" );

        if ( authentication == null || !authentication.isAuthenticated() ) {

            // Dieses Fall sollte nicht aufreten, da /neu durch Security-Konfiguration
            // nur für authentifizierte Nutzer erreichbar ist.

            LOG.warn( "Unangemeldeter Nutzer hat Seite für neuen Glossareintrag aufgerufen." );
            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "Sie sind nicht berechtigt neue Einträge anzulegen." );

            return "hauptseite";
        }

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
     *        parsen lassen
     *
     * @return Name (ohne Suffix) der Template-Datei {@code bearbeiten.html}, die angezeigt
     *         werden soll; wird in Ordner {@code src/main/resources/templates/} gesucht.
     */
    @GetMapping( "/bearbeiten/{id}")
    public String eintragBearbeiten( Authentication authentication,
                                     Model model,
                                     @PathVariable("id") String idStr ) {

        model.addAttribute( ATTRIBUT_SEITENTITEL, "Eintrag im Glossar bearbeiten" );

        if ( authentication == null || !authentication.isAuthenticated() ) {

            LOG.warn( "Unangemeldeter Nutzer hat Seite zum Bearbeiten von Glossareintrag mit ID \"{}\" aufgerufen.",
                      idStr );

            model.addAttribute( ATTRIBUT_FEHLERMELDUNG, "Sie sind nicht berechtigt Einträge zu bearbeiten." );

        } else {

            // ...
        }

        return "neu_bearbeiten";
    }

}
