package domain;

/**
 * excepciones de DOPOSoccer
 * @author Jeronimo Avila Castillo
 * @author Luara Juliana Parra Velandia
 */
public class DOPOSoccerException extends Exception {
    public static final String UNKNOWN_MACHINE = "no existe esta maquina";
    /**
     * CONSTRUCTOR
     * @param message el tipo de excepcion
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}