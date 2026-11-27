package domain;

import java.io.Serializable;

/**
 * revisa los choques entre jugadores, pies y balon
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Collisions implements Serializable {
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 90;
    private static final int FOOT_REACH = 35;
    private static final int FOOT_HEIGHT = 15;
    private static final int FOOT_SIZE = 14;

    /**
     * revisa si dos jugadores se estan tocando
     * si uno pasa por encima del otro no se chocan
     * @param a es un jugador
     * @param b es el otro jugador
     * @return true si se estan tocando
     */
    public boolean playersTouch(Player a, Player b) {
        return Math.abs(a.getHeight() - b.getHeight()) < PLAYER_HEIGHT
                && Math.abs(a.getX() - b.getX()) < PLAYER_WIDTH;
    }

    /**
     * revisa si el pie del jugador que patea toca el balon
     * @param kicker es el jugador que patea
     * @param ball es el balon
     * @param direction es 1 si patea a la der y -1 si patea a la izq
     * @return true si el pie toca el balon
     */
    public boolean footTouchesBall(Player kicker, Ball ball, int direction) {
        double footX = kicker.getX() + direction * FOOT_REACH;
        double footY = kicker.getHeight() + FOOT_HEIGHT;
        double dx = ball.getX() - footX;
        double dy = ball.getHeight() - footY;
        return Math.sqrt(dx * dx + dy * dy) < FOOT_SIZE + ball.getRadius();
    }

    /**
     * revisa si el pie del jugador que patea toca al rival
     * @param kicker es el jugador que patea
     * @param rival es el otro jugador
     * @param direction es 1 si patea a la der y -1 si patea a la izq
     * @return true si el pie toca al rival
     */
    public boolean footTouchesPlayer(Player kicker, Player rival, int direction) {
        double footX = kicker.getX() + direction * FOOT_REACH;
        return Math.abs(rival.getX() - footX) < PLAYER_WIDTH / 2
                && Math.abs(rival.getHeight() - kicker.getHeight()) < PLAYER_HEIGHT;
    }
}
