package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.util.Scanner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de la clase Log
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class LogTest {

    /**
     * borra el archivo del log antes de cada prueba
     */
    @Before
    public void setUp() {
        new File(Log.FILE_NAME).delete();
    }

    /**
     * borra el archivo del log despues de cada prueba
     */
    @After
    public void tearDown() {
        new File(Log.FILE_NAME).delete();
    }

    /**
     * el log debe crear el archivo con el mensaje de la excepcion
     * @throws Exception si no se puede leer el archivo
     */
    @Test
    public void shouldWriteTheException() throws Exception {
        Log.record(new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MACHINE));
        File file = new File(Log.FILE_NAME);
        assertTrue(file.exists());
        Scanner reader = new Scanner(file);
        String line = reader.nextLine();
        reader.close();
        assertTrue(line.contains(DOPOSoccerException.UNKNOWN_MACHINE));
    }

    /**
     * el log debe agregar cada excepcion sin borrar las anteriores
     * @throws Exception si no se puede leer el archivo
     */
    @Test
    public void shouldKeepPreviousExceptions() throws Exception {
        Log.record(new DOPOSoccerException("uno"));
        Log.record(new DOPOSoccerException("dos"));
        Scanner reader = new Scanner(new File(Log.FILE_NAME));
        int lines = 0;
        while (reader.hasNextLine()) {
            reader.nextLine();
            lines++;
        }
        reader.close();
        assertEquals(2, lines);
    }
}