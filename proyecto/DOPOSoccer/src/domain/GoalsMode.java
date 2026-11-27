package domain;

/**
 * modo por goles, gana el primero que llegue a la meta
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class GoalsMode extends MatchMode {
    private int target;

    /**
     * CONSTRUCTOR
     * @param target son los goles para ganar
     */
    public GoalsMode(int target) {
        this.target = target;
    }

    /**
     * termina el partido si alguien llego a la meta
     * @param game es el partido
     */
    public void goalScored(DOPOSoccer game) {
        if (game.getPlayerOne().getGoals() >= target
                || game.getPlayerTwo().getGoals() >= target) {
            game.finish();
        }
    }

    /**
     * @param game es el partido
     * @return la meta de goles
     */
    public String getText(DOPOSoccer game) {
        return "Meta: " + target + " goles";
    }

    /**
     * @return true, este modo es por goles
     */
    public boolean isByGoals() {
        return true;
    }

    /**
     * @return los goles para ganar
     */
    public int getAmount() {
        return target;
    }
}