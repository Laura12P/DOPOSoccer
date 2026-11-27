package domain;

/**
 * modo por tiempo, si se acaba empatado sigue el gol de oro
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class TimeMode extends MatchMode {
    private int minutes;
    private int seconds;
    private boolean goldenGoal;

    /**
     * CONSTRUCTOR
     * @param minutes son los minutos del partido
     */
    public TimeMode(int minutes) {
        this.minutes = minutes;
        seconds = minutes * 60;
        goldenGoal = false;
    }

    /**
     * le quita un segundo al reloj
     * si llega a cero y van empatados arranca el gol de oro, si no termina el partido
     * @param game es el partido
     */
    public void passSecond(DOPOSoccer game) {
        if (!goldenGoal && seconds > 0) {
            seconds = seconds - 1;
            if (seconds == 0) {
                if (game.getPlayerOne().getGoals() == game.getPlayerTwo().getGoals()) {
                    goldenGoal = true;
                } else {
                    game.finish();
                }
            }
        }
    }

    /**
     * en el gol de oro el primer gol termina el partido
     * @param game es el partido
     */
    public void goalScored(DOPOSoccer game) {
        if (goldenGoal) {
            game.finish();
        }
    }

    /**
     * @param game es el partido
     * @return el tiempo que queda, o GOL DE ORO, y si esta congelado lo dice
     */
    public String getText(DOPOSoccer game) {
        String text = "GOL DE ORO";
        if (!goldenGoal) {
            int rest = seconds % 60;
            if (rest < 10) {
                text = "Tiempo: " + seconds / 60 + ":0" + rest;
            } else {
                text = "Tiempo: " + seconds / 60 + ":" + rest;
            }
        }
        if (game.isTimeFrozen()) {
            text = text + " (congelado)";
        }
        return text;
    }

    /**
     * @return false, este modo es por tiempo
     */
    public boolean isByGoals() {
        return false;
    }

    /**
     * @return los minutos con los que empezo el partido
     */
    public int getAmount() {
        return minutes;
    }
}