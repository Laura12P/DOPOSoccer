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
        super("Rayo", 60, 14, 3, false, new int[] {255, 220, 0, 110});
    }

    protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
        rival.applyEffect(new ShockEffect(), getFrames());
    }
}
