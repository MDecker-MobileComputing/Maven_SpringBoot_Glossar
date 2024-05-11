"use strict";


/**
 * Event-Handler-Funktion für Klick auf den "Speichern"-Button.
 * Wenn das (hidden) Attribut `eintrag_id` leer ist, wird ein neuer Eintrag angelegt,
 * ansonsten wird ein bestehender Eintrag geändert.
 *
 * @returns {Boolean} Immer Wert `false`, um Laden einer anderen Seite zu verhindern.
 */
function onSpeichernButton() {

    const eingabeBegriff    = document.getElementById( "eingabe_begriff"    );
    const eingabeErklaerung = document.getElementById( "eingabe_erklaerung" );
    const eingabeID         = document.getElementById( "eintrag_id"         );
    if ( !eingabeBegriff || !eingabeErklaerung || !eingabeID ) {

        alert( "Interner Fehler: Referenz auf mindestens ein Eingabe-Element nicht gefunden." );
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


    let eingabeIDWert = eingabeID.value;
    if ( !eingabeIDWert ) {

        console.log( "ID nicht gesetzt, also wird neuer Eintrag angelegt." );
        eingabeIDWert = "";
    }


    begriff    = begriff.trim();
    erklaerung = erklaerung.trim();

    const payloadObjekt = {
        begriff    : begriff,
        erklaerung : erklaerung,
        id         : eingabeIDWert
    };
    const jsonPayload = JSON.stringify( payloadObjekt );

    fetch( "/api/v1/speichern", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: jsonPayload
    })
    .then( response => {

        if (!response.ok) {

            if (response.status === 409) {

                throw new Error( `Es gibt bereits einen Eintrag für den Begriff \"${begriff}\".` );

            } else {

                throw new Error( `Fehler vom Server (Code ${response.status})` );   
            }

        } else {

            return response.text() ;
        }
    })
    .then( data => {

        console.log( "Erfolg:", data );
        window.location.href = "/app/hauptseite";
    })
    .catch( (fehler) => {

        console.error( "Fehler bei HTTP-POST-Request mit Glossareintrag:", fehler );
        alert( fehler );
    });

    return false;
}
