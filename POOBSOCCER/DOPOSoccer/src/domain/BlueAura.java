package domain;

/**
 * aura azul, tiro recto y horizontal que empuja al rival hacia su arco
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class BlueAura extends Power {

    /**
     * CONSTRUCTOR
     */
    public BlueAura() {
        super("Aura Azul", 40, 16, 1.5, false);
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        rival.applyPush(direction, getFrames());
    }
}