package domain;

import java.io.Serializable;

/**
 * tipo de jugador que maneja el computador
 * segun el tipo de maquina el jugador tiene ciertas
 * capasidades
 * @author jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public abstract class Machine implements Serializable {
    protected static final int MARGIN = 6;
    protected static final int BEHIND = 30;
    protected static final int REACH = 45;
    protected static final int LOW_BALL = 60;
    protected static final int HIGH_BALL = 140;
    private String name;
    private String description;

    /**
     * CONSTRUCTOR
     * @param name es el nombre del tipo de maquina
     * @param description es lo que hace esta maquina, en una frase
     */
    public Machine(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * retorna el nombre del tipo de maquina
     */
    public String getName() {
        return name;
    }
    /**
     * decide y hace la jugada a hacer en cada situacion
     * @param game es el partido que se esta jugando
     * @param number es el numero del jugador que esta manejando cada maquina
     */
    public abstract void play(DOPOSoccer game, int number);

    /**
     * @param game es el partido
     * @param number es el numero del jugador
     * retorna es el jugador que maneja la maquina
     */
    protected Player getMe(DOPOSoccer game, int number) {
        if (number == 1) {
            return game.getPlayerOne();
        }
        return game.getPlayerTwo();
    }
    /**
     * @param game es el partido
     * @param number es el numero del jugador
     * retorna el jugador contrario
     */
    protected Player getRival(DOPOSoccer game, int number) {
        if (number == 1) {
            return game.getPlayerTwo();
        }
        return game.getPlayerOne();
    }
    /**
     * @param number es el numero del jugador
     * retorna 1 si se ataca a la derecha y -1 si es al otro lado
     */
    protected int getDirection(int number) {
        if (number == 1) {
            return 1;
        }
        return -1;
    }
    /**
     * @param game es el partido
     * @param number es el numero del jugador
     * retorna la posicion horizontal del arco que esta defendiendo
     */
    protected int getOwnGoal(DOPOSoccer game, int number) {
        if(number == 1) {
            return 0;
        }
        return game.getWidth();
    }
    /**
     * camina para un lado
     * @param game es el partido
     * @param number es el numero del jugador
     * @param target es la posicion ala que quiere llegar
     */
    protected void moveTo(DOPOSoccer game, int number, double target) {
        Player me = getMe(game, number);
        if (me.getX() < target - MARGIN) {
            game.moveRight(number);
        } else if (me.getX() > target + MARGIN) {
            game.moveLeft(number);
        }
    }
    /**
     * se coloca detras del balon para empujar el balon para un lado
     * @param game es el partido
     * @param number es el numero del jugador
     */
    protected void attack(DOPOSoccer game, int number) {
        Ball ball = game.getBall();
        int direction = getDirection(number);
        moveTo(game, number, ball.getX() - direction * BEHIND);
        kickOrJump(game, number);
    }
    /**
     * patea el balon si el balon esta abajo y a sus pies 
     * si esta alto o cerca
     * @param game es el partido
     * @param number es el numero del jugador
     */
    protected void kickOrJump(DOPOSoccer game, int number) {
        Player me = getMe(game, number);
        Ball ball = game.getBall();
        double front = (ball.getX() - me.getX()) * getDirection(number);
        if (front > 0 && front < REACH && ball.getHeight() < LOW_BALL) {
            game.kick(number);
        } else if (Math.abs(ball.getX() - me.getX()) < REACH && ball.getHeight() > LOW_BALL
                && ball.getHeight() < HIGH_BALL) {
            game.jump(number);
        }
    }
    /**
     * marca como listo el poder si la barra esta cargada
     * @param game es el partido
     * @param number es el numero del jugador
     */
    protected void usePowerIfReady(DOPOSoccer game, int number) {
        Player me = getMe(game, number);
        if (me.getPowerBar() >= me.getMaxPower() && !me.isPowerArmed()) {
            game.activatePower(number);
        }
    }
    
    /**
     * @return lo que hace esta maquina, en una frase
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return una maquina nueva del mismo tipo, sin lo que haya aprendido o recordado
     */
    public abstract Machine copy();
}
