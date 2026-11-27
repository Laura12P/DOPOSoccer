package domain;

import java.io.Serializable;

/**
 * modo de juego del partido, decide cuando se acaba y que dice el reloj
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public abstract class MatchMode implements Serializable {

    /**
     * crea el modo que corresponde a la configuracion de la pantalla
     * es el unico lugar donde se escoge la clase de un modo
     * @param byGoals es true si es por goles, y false si es por tiempo
     * @param amount son los goles para ganar o los minutos
     * @return el modo creado
     */
    public static MatchMode create(boolean byGoals, int amount) {
        if (byGoals) {
            return new GoalsMode(amount);
        }
        return new TimeMode(amount);
    }

    /**
     * se llama cada segundo que el reloj no esta congelado y el partido sigue
     * @param game es el partido
     */
    public void passSecond(DOPOSoccer game) {
    }

    /**
     * se llama despues de cada gol, cuando los jugadores ya volvieron a la salida
     * @param game es el partido
     */
    public void goalScored(DOPOSoccer game) {
    }

    /**
     * @param game es el partido
     * @return lo que dice la barra de arriba durante el partido
     */
    public abstract String getText(DOPOSoccer game);

    /**
     * @return true si el modo es por goles, false si es por tiempo
     */
    public abstract boolean isByGoals();

    /**
     * @return los goles para ganar o los minutos, segun el modo
     */
    public abstract int getAmount();
}