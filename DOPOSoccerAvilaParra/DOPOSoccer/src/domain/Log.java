package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * guarda en un archivo las excepciones que ocurren en DOPOSoccer
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Log {
    public static final String FILE_NAME = "DOPOSoccer.log";

    /**
     * escribe una excepcion en el archivo con la fecha y hora
     * si no se puede escribir el archivo no hace nada
     * @param e es la excepcion que se quiere guardar
     */
    public static void record(Exception e) {
        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.write(new Date() + " - " + e.getClass().getName() + ": " + e.getMessage() + "\n");
            writer.close();
        } catch (IOException ex) {
            System.out.println("No se pudo escribir el log");
        }
    }
}