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
    public static final String NOT_ENOUGH_POINTS = "no tiene puntos suficientes para que esa mejora";
    public static final String MAX_LEVEL = "esta estadistica ya esta en el nivel maximo";
    public static final String UNKNOWN_STAT = "no existe esa estadistica";
    public static final String INVALID_SERIES = "la serie debe ser mejor de 1, 3 o 5, o mas de 0 puntos";
    public static final String INVALID_EXTENSION = "la partida debe ser un archivo .dat";

    /**
     * CONSTRUCTOR
     * @param message el tipo de excepcion
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}
