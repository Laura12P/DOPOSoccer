package domain;

import java.io.Serializable;

/**
 * tiro de poder de un personaje
 * sale cuando el jugador con el poder activo toca el balon
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public abstract class Power implements Serializable {
    private static final int FRAMES_PER_SECOND = 60;
    private String name;
    private int damage;
    private double speed;
    private double seconds;
    private boolean groundShot;

    /**
     * CONSTRUCTOR
     * @param name es el nombre del tiro de poder
     * @param damage es el daño base del tiro, antes del multiplicador de patada
     * @param speed es la velocidad con la que sale el balon
     * @param seconds es cuanto dura el efecto sobre el rival
     * @param groundShot es true si el balon sale a ras de suelo
     */
    public Power(String name, int damage, double speed, double seconds, boolean groundShot) {
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.seconds = seconds;
        this.groundShot = groundShot;
    }

    /**
     * @return el nombre del tiro de poder
     */
    public String getName() {
        return name;
    }

    /**
     * @return la velocidad con la que sale el balon
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @return true si el balon sale a ras de suelo
     */
    public boolean isGroundShot() {
        return groundShot;
    }

    /**
     * @return lo que dura el efecto, en pasos del reloj
     */
    public int getFrames() {
        return (int) (seconds * FRAMES_PER_SECOND);
    }

    /**
     * el tiro golpea al rival: le hace daño y le pone el efecto
     * @param user es el jugador que hizo el tiro
     * @param rival es el jugador golpeado
     * @param direction es 1 si el tiro va a la der, y -1 si va a la izq
     * @param ball es el balon del partido
     */
    public void hit(Player user, Player rival, int direction, Ball ball) {
        rival.receiveDamage((int) Math.round(damage * user.getKickMultiplier()));
        applyEffect(rival, direction, ball);
    }

    /**
     * pone el efecto propio de este tiro
     * @param rival es el jugador golpeado
     * @param direction es 1 si el tiro va a la der, y -1 si va a la izq
     * @param ball es el balon del partido
     */
    protected abstract void applyEffect(Player rival, int direction, Ball ball);
}