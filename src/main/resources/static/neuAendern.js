"use strict";


/**
 * Event-Handler-Funktion für Klick auf den "Speichern"-Button.
 *
 * @returns {Boolean} Immer Wert `false`, um Laden einer anderen Seite zu verhindern.
 */
function onSpeichernButton() {

    const eingabeBegriff    = document.getElementById( "eingabe_begriff"    );
    const eingabeErklaerung = document.getElementById( "eingabe_erklaerung" );
    if ( !eingabeBegriff || !eingabeErklaerung ) {

        alert( "Interner Fehler: Referenz(en) auf Eingabe-Elemente nicht gefunden." );
        return false;
    }

    let begriff = eingabeBegriff.value;
    if ( begriff === undefined || begriff === null ) {

        alert( "Bitte einen Begriff eingeben!" );
        return false;
    }

    let erklaerung = eingabeErklaerung.value;
    if ( erklaerung === undefined || erklaerung === null ) {

        alert( "Bitte einen Erklärung eingeben!" );
        return false;
    }

    begriff    = begriff.trim();
    erklaerung = erklaerung.trim();

    const payloadObjekt = {
        begriff:    begriff,
        erklaerung: erklaerung
    };
    const jsonPayload = JSON.stringify(payloadObjekt);

    fetch( "/api/v1/neu", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: jsonPayload
    })
    .then( response => {

        if (!response.ok) {

            const statusText = `${response.statusText} (${response.status})`;
            throw new Error( `REST-Endpunkt hat Fehlercode zurückgeliefert: ${statusText}` );

        } else {

            return response.text() ;
        }
    })
    .then( data => {

        console.log( "Erfolg:", data );
        window.location.href = "hauptseite";
    })
    .catch( (fehler) => {

        console.error( "Fehler bei HTTP-POST-Request mit Glossareintrag:", fehler );
        alert( "Fehler beim Speichern: " + fehler );
    });

    return false;
}
