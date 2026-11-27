package domain;

/**
 * inconsciente, sale lanzado al aire y no se puede mover
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class KnockOutEffect extends Effect {
    private static final double LIFT = 15;

    public boolean isStunned() {
        return true;
    }

    public void start(Player player) {
        player.launch(LIFT);
    }
}