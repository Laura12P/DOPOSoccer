package domain;

/**
 * tornado, remolino a ras de suelo que deja al rival inconsciente en el aire
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Tornado extends Power {

    /**
     * CONSTRUCTOR
     */
    public Tornado() {
        super("Tornado", 40, 12, 3, true, new int[] {150, 150, 150, 120});
    }

    protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
        rival.applyEffect(new KnockOutEffect(), getFrames());
    }
}