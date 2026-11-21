package domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
/**
 * fachada del juego
 * usando patron de fachada
 * unica clase que tiene comunicacion
 * con presentacion
 * guarda los dos jugadores, el balon, el tiempo
 * y es encargado de marcar los goles y dice cual 
 * es el ganador
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class DOPOSoccer implements Serializable {
    public static final int GOAL_WIDTH = 60;
    public static final int GOAL_HEIGHT = 160;
    public static final int WIN_POINTS = 100;
    public static final int LOSE_POINTS = 25;
    public static final String[] MACHINES = {"Torpe", "Ofensiva", "Defensiva", "Experta"};
    private static final int SIDE_MARGIN = 25;
    private static final int BALL_START_HEIGHT = 200;
    private static final int BALL_RADIUS = 16;
    private static final int PLAYER_WIDTH = 50;
    private static final double KICK_SPEED_X = 10;
    private static final double KICK_SPEEED_Y = 7; 

    private Player playerOne;
    private Player playerTwo;
    private Ball ball;
    private Collisions collisions;
    private int width;
    private int height;
    private int seconds;
    private boolean goldenGoal;
    private boolean over;
    private boolean byGoals;
    private int goalTarget;
    private Machine machine;
    /**
     * CONSTRUCTOR
     * prepara el partido con los jugadores en su lugar
     * @param one es el jugador del lado izq
     * @param two es el jugador del lado der
     * @param width es el ancho de la cancha
     * @param height es la altura de la cancha
     */
    public DOPOSoccer (Player one, Player two, int width, int height) {
        this(one, two, width, height, false, 1);
    }
     /**
     * CONSTRUCTOR
     * prepara el partido con los jugadores en su lugar
     * @param one es el jugador del lado izq
     * @param two es el jugador del lado der
     * @param width es el ancho de la cancha
     * @param height es la altura de la cancha
     * @param byGoals es true si el partido es por goles, y false si es por tiempo
     * @param amount son los goles para ganar si es por goles, o los minutos si es por tiempo
     */
    public DOPOSoccer(Player one, Player two, int width, int height, boolean byGoals, int amount){
        playerOne = one;
        playerTwo = two;
        this.width = width;
        this.height = height;
        this.byGoals = byGoals;
        if (byGoals) {
            goalTarget = amount;
            seconds = 0;
        } else {
            seconds = amount * 60;
        }
        ball = new Ball();
        collisions = new Collisions();
        goldenGoal = false;
        over = false;
        reset();
    }

    /**
     * mueve a la izq al jugador, si tiene los controles invertidos va a la der
     * @param number es 1 el jugador izq, y 2 el de la der
     */
    public void moveLeft(int number){
        Player player = getPlayer(number);
        if (player.isConfused()) {
            player.moveRight(width - SIDE_MARGIN);
        } else {
            player.moveLeft(SIDE_MARGIN);
        }
    }
    /**
     * mueve a la der el jugador indicado, si tiene los controles invertidos va a la izq
     * @param number es 1 el jugador izq, y 2 el de la der
     */
    public void moveRight(int number){
        Player player = getPlayer(number);
        if (player.isConfused()) {
            player.moveLeft(SIDE_MARGIN);
        } else {
            player.moveRight(width - SIDE_MARGIN);
        }
    }
    /**
     * salta el jugador que se indique
     * @param number es 1 el jugador izq, y 2 el de la der
     */
    public void jump(int number){
        getPlayer(number).jump();
    }
    /**
     * hace que un jugador que esta indicado estire el pie para patear
     * @param number es 1 para el jugador izq y 2 para el jugador der
     */
    public void kick(int number) {
        getPlayer(number).startKick();
    }
    /**
     * deja listo el poder del jugador, se usa en su siguiente golpe al balon
     * solo funciona si la barra de poder esta llena
     * @param number es 1 para el jugador izq y 2 para el jugador der
     */
    public void activatePower(int number) {
        getPlayer(number).armPower();
    }

    /** 
     * mueve a los jugadores y al balon si hubo gol
     * cuando hacen gol todos vuelven a la posicion inicial
     * @return true si hubo gol
     */
    public boolean update() {
        boolean goal = false;
        if (machine != null) {
            machine.play(this, 2);
        }
        playerOne.update(SIDE_MARGIN, width - SIDE_MARGIN);
        playerTwo.update(SIDE_MARGIN, width - SIDE_MARGIN);
        collidePlayers();
        ball.collideWithPlayer(playerOne);
        ball.collideWithPlayer(playerTwo);
        checkKick(playerOne, playerTwo, 1);
        checkKick(playerTwo, playerOne, -1);
        ball.update(0, width, height);
        if (ball.getX() + BALL_RADIUS <= GOAL_WIDTH && ball.getHeight() + BALL_RADIUS <= GOAL_HEIGHT){
            playerTwo.addGoal();
            playerOne.chargePower(playerOne.getMaxPower() / 4);
            goal = true;
        } else if (ball.getX() - BALL_RADIUS >= width - GOAL_WIDTH - 1 && ball.getHeight() + BALL_RADIUS <= GOAL_HEIGHT){
            playerOne.addGoal();
            playerTwo.chargePower(playerTwo.getMaxPower() / 4);
            goal = true;
        }
        if (goal) {
            reset();
            if (goldenGoal || (byGoals && (playerOne.getGoals() >= goalTarget
                    || playerTwo.getGoals() >= goalTarget))) {
                finish();
            }
        }
        return goal;
    }

    /**
     * le va quitando segundos al partido y carga las barras de poder
     * si el tiempo llega a cero,y van empatados arranca el gol de oro
     * en un partido por goles no se acaba el tiempo
     */
    public void passSecond(){
        if (!over) {
            playerOne.passSecond();
            playerTwo.passSecond();
        }
        if (!over && !byGoals && !goldenGoal && seconds > 0){
            seconds = seconds -1;
            if (seconds == 0){
                if (playerOne.getGoals() == playerTwo.getGoals()){
                    goldenGoal = true;
                } else {
                    finish();
                }
            }
        }
    }

    /**
     * @return el jugador que va ganando
     */
    public Player getWinner(){
        if (playerOne.getGoals() > playerTwo.getGoals()){
            return playerOne;
        }
        return playerTwo;
    }

    /**
     * @return el jugador que va perdiendo
     */
    public Player getLoser(){
        if (getWinner() == playerOne){
            return playerTwo;
        }
        return playerOne;
    }

    /**
     * @return el jugador de la izq
     */
    public Player getPlayerOne(){
        return playerOne;
    }
    /**
     * @return el jugador de la der
     */
    public Player getPlayerTwo() {
        return playerTwo;
    }
    /**
     * @return el balon del partido
     */
    public Ball getBall(){
        return ball;
    }
    /**
     * @return los segundos que le quedan a la partida
     */
    public int getSeconds(){
        return seconds;
    }
    /**
     * @return true si esta en gol de oro
     */
    public boolean isGoldenGoal() {
        return goldenGoal;
    }
    /**
     * @return true si se acabo el partido
     */
    public boolean isOver() {
        return over;
    }

    /**
     * @param number es 1 el jugador izq, y 2 el de la der
     * @return el jugador que se pidio
     */
    private Player getPlayer(int number){
        if (number == 1){
            return playerOne;
        }
        return playerTwo;
    }
    /**
     * cuando los jugadores se tocan, se separan para que no se atraviesen
     * si pasa por encima no se choca
     */
    private void collidePlayers() {
        if (collisions.playersTouch(playerOne, playerTwo)) {
            if (playerOne.getX() <= playerTwo.getX()){
                separate(playerOne, playerTwo);
            } else {
                separate(playerTwo, playerOne);
            }
        }
    }
    /**
     * separa cuando los jugadores estan muy cerca
     * si estan contra la pared a la mitad
     * @param left es el jugador que esta mas a la izq
     * @param right es el jugador que esta a la der
     */
    private void separate(Player left, Player right) {
        int overlap = (int) (PLAYER_WIDTH - (right.getX() - left.getX()));
        if (overlap > 0) {
            int half = overlap / 2;
            left.push(-half, SIDE_MARGIN, width - SIDE_MARGIN);
            right.push(overlap - half, SIDE_MARGIN, width - SIDE_MARGIN);
            overlap = (int) (PLAYER_WIDTH - (right.getX() - left.getX()));
            if (overlap > 0) {
                left.push(-overlap, SIDE_MARGIN, width - SIDE_MARGIN);
            }   
        }
    }
    /**
     * revisa si el pie del jugador que patea toca el balon
     * o en su defecto toca al rival
     * si el poder esta listo, se usa al golpear el balon
     * @param kicker es el jugador que patea
     * @param rival es el otro jugador
     * @param direction es 1 si patea a der, y -1 si patea a izq
     */
    private void checkKick(Player kicker, Player rival, int direction) {
        if (kicker.canHit()) {
            if (collisions.footTouchesBall(kicker, ball, direction)){
                ball.kick(direction * KICK_SPEED_X, KICK_SPEEED_Y);
                if (kicker.isPowerArmed()) {
                    kicker.usePower(rival, ball, direction);
                }
                kicker.useKick();
            } else if (collisions.footTouchesPlayer(kicker, rival, direction)){
                rival.receiveDamage(kicker.getKickDamage());
                kicker.useKick();
            } 
        }
    }
    
    /**
     * pone a los jugadores y el balon en la salida
     */
    private void reset(){
        playerOne.place(width / 4);
        playerTwo.place(width * 3 / 4);
        ball.place(width / 2, BALL_START_HEIGHT);
    }

    /**
     * termina el partido y le da los puntos al ganador y al perdedor
     * solo funciona una vez
     */
    private void finish() {
        if (!over) {
            over = true;
            getWinner().addPoints(WIN_POINTS);
            getLoser().addPoints(LOSE_POINTS);
        }
    }

    /**
     * @return true si el partido es por goles, y false si es por tiempo
     */
    public boolean isByGoals() {
        return byGoals;
    }
    /**
     * pone una maquina a manejar el jugador de la der
     * @param profile es el tipo de maquina para asignar
     * @throws DOPOSoccerException si no existe ese tipo de maquina
     */
    public void setMachine(String profile) throws DOPOSoccerException {
        if (MACHINES[0].equals(profile)) {
            machine = new ClumsyMachine();
        } else if (MACHINES[1].equals(profile)) {
            machine = new OffensiveMachine();
        } else if (MACHINES[2].equals(profile)){
            machine = new DefensiveMachine();
        } else if (MACHINES[3].equals(profile)){
            machine = new ExpertMachine();
        } else {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MACHINE);
        }
    }
    /**
     * @param number es 1 para el jugador izq y 2 para el der
     * retorna true si a ese jugador lo maneja la maquina
     */
    public boolean isMachine(int number) {
        return number == 2 && machine != null;
    }
    /**
     * retorna el tipo de maquina que se esta jugando
     */
    public String getMachineName() {
        if (machine == null) {
            return null;
        }
        return machine.getName();
    }
    /**
     * retorna la altura de la cancha
     */
    public int getHeight() {
        return height;
    }
    /**
     * retorna el ancho de la cancha
     */
    public int getWidth() {
        return width;
    }
    /**
     * retorna los goles que se necesitan para ganar, solo en partidos por goles
     */
    public int getGoalTarget() {
        return goalTarget;
    }

    /**
     * guarda la partida en un archivo
     * @param fileName es el nombre del archivo donde se guarda
     * @throws DOPOSoccerException si no se pudo guardar el archivo
     */
    public void save(String fileName) throws DOPOSoccerException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(this);
            out.close();
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.CANNOT_SAVE);
        }
    }

    /**
     * abre una partida guardada en un archivo
     * @param fileName es el nombre del archivo que se abre
     * @return la partida que estaba guardada
     * @throws DOPOSoccerException si no existe el archivo o no es una partida valida
     */
    public static DOPOSoccer open(String fileName) throws DOPOSoccerException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            DOPOSoccer soccer = (DOPOSoccer) in.readObject();
            in.close();
            return soccer;
        } catch (FileNotFoundException e) {
            throw new DOPOSoccerException(DOPOSoccerException.FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        } catch (ClassNotFoundException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        } catch (ClassCastException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        }
    }
}