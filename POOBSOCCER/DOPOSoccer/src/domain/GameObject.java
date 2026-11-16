package domain;

/**
 * objeto que tiene una posicion en la cancha
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public abstract class GameObject {
    protected double x;
    protected double height;

    /**
     * @return la posicion horizontal del centro
     */
    public double getX() {
        return x;
    }

    /**
     * @return la altura sobre el suelo
     */
    public double getHeight() {
        return height;
    }
}