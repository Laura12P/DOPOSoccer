package domain;
/**
 * maquina experta
 * va decidiendo segun como va el partido
 * si va perdiendo ataca
 * si va ganando defiende
 * si el rival esta quieto por un poder ataca
 * @author Jeroniom Avila
 * @author Laura Juliana Parra Velandia
 */
public class ExpertMachine extends Machine {
    private OffensiveMachine offensive;
    private DefensiveMachine defensive;
    private boolean attacking;
    /**
     * CONSTRUCTOR
     */
    public ExpertMachine() {
        super("Experta", "decide segun como va el partido");
        offensive = new OffensiveMachine();
        defensive = new DefensiveMachine();
        attacking = true;
    }
    /**
     * revisa el marcador
     * y decide si atacar o defender
     * @param game es el partido
     * @param number es el numero del jugador
     * 
     */
    public void play(DOPOSoccer game, int number) {
        Player me = getMe(game, number);
        Player rival = getRival(game, number);
        attacking = me.getGoals() <= rival.getGoals() || rival.isStunned();
        if (attacking) {
            offensive.play(game, number);
        } else {
            defensive.play(game, number);
        }
    }
    /**
     * retorna true si en el ultimo paso se decide atacar
     */
    public boolean isAttacking() {
        return attacking;
    }

    /**
     * @return una maquina nueva de este tipo
     */
    public Machine copy() {
        return new ExpertMachine();
    }
}
