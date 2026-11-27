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
        super("Tornado", 40, 12, 3, true);
    }

    protected void applyEffect(Player rival, int direction, Ball ball) {
        rival.applyEffect(new KnockOutEffect(), getFrames());
    }
}
