package domain;

/**
 * excepciones de DOPOSoccer
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class DOPOSoccerException extends Exception {
    public static final String UNKNOWN_MACHINE = "no existe esta maquina";
    public static final String FILE_NOT_FOUND = "no se encontro el archivo";
    public static final String INVALID_FILE = "el archivo no es una partida valida";
    public static final String CANNOT_SAVE = "no se pudo guardar el archivo";

    /**
     * CONSTRUCTOR
     * @param message el tipo de excepcion
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}