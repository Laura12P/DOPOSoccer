package domain;

import java.io.Serializable;

/**
 * efecto que un poder le deja a un jugador
 * por defecto no cambia nada, cada efecto sobreescribe lo que necesite
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public abstract class Effect implements Serializable {

    /**
     * @return por cuanto se multiplica la velocidad del jugador
     */
    public double getSpeedFactor() {
        return 1;
    }

    /**
     * @return true si el jugador puede saltar con este efecto
     */
    public boolean canJump() {
        return true;
    }

    /**
     * @return true si el jugador no se puede mover, saltar ni patear
     */
    public boolean isStunned() {
        return false;
    }

    /**
     * @return true si los controles de movimiento quedan invertidos
     */
    public boolean invertsControls() {
        return false;
    }

    /**
     * se llama una vez, cuando el efecto le llega al jugador
     * @param player es el jugador afectado
     */
    public void start(Player player) {
    }

    /**
     * se llama en cada paso del reloj mientras dura el efecto
     * @param player es el jugador afectado
     * @param left es el limite de la izquierda de la cancha
     * @param right es el limite de la derecha de la cancha
     */
    public void step(Player player, int left, int right) {
    }

    /**
     * @return por cuanto se multiplica el daño que recibe el jugador
     */
    public double getDamageFactor() {
        return 1;
    }

    /**
     * @return por cuanto se multiplica la altura del salto
     */
    public double getJumpFactor() {
        return 1;
    }

    /**
     * @return true si el jugador puede patear con este efecto
     */
    public boolean canKick() {
        return true;
    }
}