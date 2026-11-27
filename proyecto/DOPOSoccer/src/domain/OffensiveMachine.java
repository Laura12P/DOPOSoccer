package domain;

/**
 * maquina ofensiva
 * hace todo para anotar gol
 * siempre va por el valon y patea hacia el otro lado
 * usa el poder cuando lo tiene
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 * 
 */
public class OffensiveMachine extends Machine {
    /**
     * CONSTRUCTOR
     */
    public OffensiveMachine() {
        super("Ofensiva", "su prioridad es anotar");
    }
    /**
     * va detras del balon, patea y usa el poder cuando pueda
     * @param game es el partido
     * @param number es el numero del jugador
     */
    public void play(DOPOSoccer game, int number) {
        usePowerIfReady(game, number);
        attack(game, number);
    }

    /**
     * @return una maquina nueva de este tipo
     */
    public Machine copy() {
        return new OffensiveMachine();
    }
}