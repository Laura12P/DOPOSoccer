package domain;
import java.util.Random;

/**
 * maquina Torpe
 * escoge al azar lo que hace y lo repite antes de cambiar
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */

public class ClumsyMachine extends Machine {
    private static final int ACTIONS = 5;
    private static final int MIN_STEPS = 10;
    private static final int MORE_STEPS = 20;
    private Random random;
    private int action;
    private int steps;

    /**
     * CONSTRUCTOR
     */
    public ClumsyMachine() {
        super("Torpe", "escoge sus movimientos al azar");
        random = new Random();
        steps = 0;
    }
    /**
     * si ya acabo la accion escoge otra accion al azar
     * accion: izq, der, saltar, patear o quieto
     * @param game es el partidp
     * @param number es el numero del jugador
     */
    public void play(DOPOSoccer game, int number) {
        if (steps == 0) {
            action = random.nextInt(ACTIONS);
            steps = MIN_STEPS + random.nextInt(MORE_STEPS);
        }
        steps = steps -1;
        if (action == 0){
            game.moveLeft(number);
        } else if (action == 1) {
            game.moveRight(number);
        } else if (action == 2) {
            game.jump(number);
        } else if (action == 3) {
            game.kick(number);
        }
    }
    /**
     * retorna la accion que se esta haciendo
     */
    public int getAction() {
        return action;
    }

    /**
     * @return una maquina nueva de este tipo
     */
    public Machine copy() {
        return new ClumsyMachine();
    }

}
