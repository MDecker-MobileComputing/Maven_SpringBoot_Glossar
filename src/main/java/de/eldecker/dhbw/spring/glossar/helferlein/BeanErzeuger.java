package de.eldecker.dhbw.spring.glossar.helferlein;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;


/**
 * Das Objekt dieser Klasse dient dazu, bestimmte Beans zur Verfügung zu stellen.
 * Die darin enthaltenen Methoden müssen deshalb mit der Annotation {@code Bean}
 * versehen sein. 
 */
@Configuration
public class BeanErzeuger {

    /**
     * Liefert konfiguriertes ObjectMapper-Objekt zurück, welches für Object-nach-JSON (Serialisierung)
     * oder JSON-nach-Objekt (Deserialisierung) benötigt wird.
     * <br><br>
     *
     * Konfiguration:
     * <ul>
     * <li>Kein Fehler, wenn beim Deserialisierung ein Feld im JSON gefunden wird, das nicht in der Zielklasse
     *     definiert ist</li>
     *  <li>Das erzeugte JSOn wird für bessere Lesbarkeit durch Einrückungen formatiert.</li>
     * </ul>
     *
     * @return Konfigurierter Object-Mapper
     */
    @Bean
    public ObjectMapper objectMapper() {

        return JsonMapper.builder()
                         .disable( FAIL_ON_UNKNOWN_PROPERTIES ) 
                         .enable(  INDENT_OUTPUT              ) 
                         .build();
    }
}
