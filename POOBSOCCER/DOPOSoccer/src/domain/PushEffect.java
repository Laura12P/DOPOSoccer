package domain;

/**
 * empuje, el jugador es empujado hacia un lado en cada paso
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class PushEffect extends Effect {
    private static final int PUSH_SPEED = 10;
    private int direction;

    /**
     * CONSTRUCTOR
     * @param direction es 1 si lo empujan a la der, y -1 a la izq
     */
    public PushEffect(int direction) {
        this.direction = direction;
    }

    public void step(Player player, int left, int right) {
        player.push(direction * PUSH_SPEED, left, right);
    }
}