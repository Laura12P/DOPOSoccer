package domain;

/**
 * rayo, tiro recto que electrocuta al rival: no salta y va a la mitad de velocidad
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Lightning extends Power {

    /**
     * CONSTRUCTOR
     */
    public Lightning() {
        super("Rayo", 60, 14, 3, false);
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        rival.applyShock(getFrames());
    }
}