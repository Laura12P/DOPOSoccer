package domain;

/**
 * confusion, tiro recto que deja al rival confundido: sus controles de movimiento quedan invertidos
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Confusion extends Power {

    /**
     * CONSTRUCTOR
     */
    public Confusion() {
        super("Confusion", 30, 12, 8, false, new int[] {255, 80, 160, 110});
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        rival.applyEffect(new ConfusionEffect(), getFrames());
    }
}
