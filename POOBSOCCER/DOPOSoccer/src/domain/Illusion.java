package domain;

/**
 * ilusion, el balon se divide en 8 balones iguales y 7 son falsos
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Illusion extends Power {

    /**
     * CONSTRUCTOR
     */
    public Illusion() {
        super("Ilusion", 30, 12, 2.5, false, new int[] {170, 60, 220, 100});
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        ball.split(getFrames());
    }
}
