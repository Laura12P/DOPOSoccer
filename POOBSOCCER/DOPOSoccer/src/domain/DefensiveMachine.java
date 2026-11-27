package domain;

/**
 * maquina defensiva
 * lo que intenta hacer siempre es defender el arco
 * se queda cerca del arco
 * y solo despeja el balon
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class DefensiveMachine extends Machine {
    private static final int HOME = 120;

    /**
     * CONSTRUCTOR
     */
    public DefensiveMachine() {
        super("Defensiva", "su prioridad es defender el arco");
    }
    /**
     * despeja el balon si esta cerca
     * si no solo se queda en el arco
     * @param game es el partido
     * @param number es el numero del jugador
     */
    public void play(DOPOSoccer game, int number) {
        if (isBallInMyHalf(game, number)) {
            attack(game, number);
        } else {
            int ownGoal = getOwnGoal(game, number);
            moveTo(game, number, ownGoal + getDirection(number) * HOME);
            kickOrJump(game, number);
        }
    }
    /**
     * @param game es el partido
     * @param number es el numero del jugador
     * retorna true si el balon esta en la parte del que defiende
     */
    public boolean isBallInMyHalf(DOPOSoccer game, int number) {
        double distance = Math.abs(game.getBall().getX() - getOwnGoal(game, number));
        return distance < game.getWidth() / 2;
    }

    /**
     * @return una maquina nueva de este tipo
     */
    public Machine copy() {
        return new DefensiveMachine();
    }
}
