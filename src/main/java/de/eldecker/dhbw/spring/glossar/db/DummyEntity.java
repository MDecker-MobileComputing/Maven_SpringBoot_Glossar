package de.eldecker.dhbw.spring.glossar.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;


/**
 * Dummy-Entity nur für {@code NamedQuery}-Annotationen.
 * Die Tabelle wird auf der Datenbank unter dem Namen {@code DUMMY_ENTITY}
 * angelegt, bleibt aber leer.
 */
@NamedQuery(name = "GlossarEntity.getGlossarEintraegeFuerAutor",
query = "SELECT new de.eldecker.dhbw.spring.glossar.db.entities.GlossarEntity( g._id, g._begriff ) " +
        "FROM GlossarEntity g " +
        "WHERE g._autorErzeugung._nutzername = :autorName " +
        "ORDER BY g._begriff ASC"
)
@Entity
public class DummyEntity {

    /**
     * Es muss ein mit {@code Id} annotiertes Attribut in einer mit {@code Entity} annotierten
     * Klasse geben.
     */
    @Id
    @GeneratedValue
    private Long id;

}
