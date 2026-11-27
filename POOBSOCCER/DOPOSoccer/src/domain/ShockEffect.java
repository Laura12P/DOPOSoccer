package domain;

/**
 * electrocutado, no salta y va a la mitad de velocidad
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ShockEffect extends Effect {

    public double getSpeedFactor() {
        return 0.5;
    }

    public boolean canJump() {
        return false;
    }
}