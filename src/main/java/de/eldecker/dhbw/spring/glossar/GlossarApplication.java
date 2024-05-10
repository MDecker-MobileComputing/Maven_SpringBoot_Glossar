package de.eldecker.dhbw.spring.glossar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Einstiegspunkt der Web-App.
 * <br><br>
 * 
 * Diese Klasse kann in Eclipse im Debug-Modus gestartet werden. 
 */
@SpringBootApplication
public class GlossarApplication {

	public static void main( String[] args ) {

	    SpringApplication.run( GlossarApplication.class, args );
	}

}
