package de.eldecker.dhbw.spring.glossar.model;


/**
 * Ein Objekt dieser Record-Klasse enthält den Namen eines Autors/Nutzers und die Anzahl
 * der Glossareinträge, die er angelegt hat.
 * 
 * @param name Nutzername/Autor
 * 
 * @param anzahl Anzahl der Glossareinträge, die der Nutzer ganz neu angelegt hat
 */
public record AutorArtikelAnzahl( String name, 
                                  long anzahl 
                                ) {
}
