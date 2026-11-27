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
        super("Aura Azul", 40, 16, 1.5, false, new int[] {0, 120, 255, 100});
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        rival.applyEffect(new PushEffect(direction), getFrames());
    }
}
