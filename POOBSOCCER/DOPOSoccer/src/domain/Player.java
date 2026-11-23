package domain;

/**
 * Un jugador que se puede elegir para el partido
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Player extends GameObject {
    private static final int BASE_LIFE = 100;
    public static final int KICK_TIME = 12;
    private static final int KICK_DAMAGE = 10;
    private static final int MAX_POWER = 100;
    private static final int POWER_CHARGE = 10;
    private static final int STUN_TIME = 180;
    private static final int PUSH_SPEED = 10;
    private static final int KNOCK_LIFT = 15;
    private static final int NONE = 0;
    private static final int PUSH = 1;
    private static final int SHOCK = 2;
    private static final int KNOCK = 3;
    private static final double BASE_SPEED = 5;
    private static final double BASE_JUMP = 12;
    private static final int CONFUSE = 4;

    private String name;
    private String country;
    private int speed;
    private int kick;
    private int jump;
    private int survival;
    private Power power;
    private double verticalSpeed;
    private int goals;
    private int life;
    private int kickTime;
    private boolean kickUsed;
    private int powerBar;
    private boolean powerArmed;
    private int stunTime;
    private int effect;
    private int effectTime;
    private int pushDirection;
    private int points;

    /**
     * CONSTRUCTOR
     * @param name es el nombre del jugador
     * @param country es el pais del personaje
     * @param speed es la velocidad, de 1 a 10
     * @param kick es la patada, de 1 a 10
     * @param jump es el salto, de 1 a 10
     * @param survival es la vida, de 1 a 10
     * @param power es el tiro de poder del personaje
     */
    public Player(String name, String country, int speed, int kick, int jump, int survival, Power power){
        this.name = name;
        this.country = country;
        this.speed = speed;
        this.kick = kick;
        this.jump = jump;
        this.survival = survival;
        this.power = power;
        life = getMaxLife();
    }
    /**
     * cambia las stats del jugador por las de su perfil
     * @param speed es la velocidad
     * @param kick es la patada
     * @param jump es el salto
     * @param survival es la vida
     */
    public void setStats(int speed, int kick, int jump, int survival) {
        this.speed = speed;
        this.kick = kick;
        this.jump = jump;
        this.survival = survival;
        life = getMaxLife();

    }

    /**
     * @return el nombre del jugador
     */
    public String getName(){
        return name;
    }

    /**
     * @return el pais del personaje
     */
    public String getCountry(){
        return country;
    }

    /**
     * @return la velocidad, de 1 a 10
     */
    public int getSpeed(){
        return speed;
    }

    /**
     * @return la patada, de 1 a 10
     */
    public int getKick() {
        return kick;
    }

    /**
     * @return el salto, de 1 a 10
     */
    public int getJump() {
        return jump;
    }

    /**
     * @return la vida, de 1 a 10
     */
    public int getSurvival() {
        return survival;
    }

    /**
     * @return el tiro de poder del personaje
     */
    public Power getPower() {
        return power;
    }

    /**
     * hace una copia del jugador para usarla en un partido
     * @return es un jugador nuevo con los mismos datos
     */
    public Player copy() {
        return new Player(name, country, speed, kick, jump, survival, power);
    }

    /**
     * pone al jugador en su lugar de salida
     * @param x es la posicion horizontal de salida
     */
    public void place(int x) {
        this.x = x;
        height = 0;
        verticalSpeed = 0;
        kickTime = 0;
    }

    /**
     * mueve al jugador a la izquierda segun su velocidad
     * si esta electrocutado va a la mitad
     * @param limit es lo mas a la izquierda que puede llegar
     */
    public void moveLeft(int limit) {
        if (!isStunned()) {
            double step = BASE_SPEED * (1 + 0.10 * (speed - 1));
            if (effect == SHOCK) {
                step = step / 2;
            }
            x = x - step;
            if (x < limit) {
                x = limit;
            }
        }
    }

    /**
     * mueve al jugador a la derecha segun su velocidad
     * si esta electrocutado va a la mitad
     * @param limit es lo mas a la derecha que puede llegar
     */
    public void moveRight(int limit) {
        if (!isStunned()) {
            double step = BASE_SPEED * (1 + 0.10 * (speed - 1));
            if (effect == SHOCK) {
                step = step / 2;
            }
            x = x + step;
            if (x > limit) {
                x = limit;
            }
        }
    }

    /**
     * empuja al jugador hacia el lado sin salir de la cancha
     * @param distance distancia que se mueve un jugador horizontalmente
     * @param left es el maximo que se puede avanzar a la izq
     * @param right es el maximo que se puede avanzar a la der
     */
    public void push(int distance, int left, int right){
        x = x + distance;
        if (x < left){
            x = left;
        }
        if (x > right){
            x = right;
        }
    }

    /**
     * hace saltar al jugador, solo si esta en el suelo
     * si esta electrocutado no puede saltar
     */
    public void jump() {
        if (!isStunned() && effect != SHOCK && height == 0 && verticalSpeed == 0) {
            verticalSpeed = BASE_JUMP * (1 + 0.10 * (jump - 1));
        }
    }

    /**
     * actualiza el salto, la gravedad le quita 1 a la velocidad en cada paso
     * tambien baja el tiempo de la patada, del aturdimiento y del efecto del rival
     * cuando termina el aturdimiento por vida, la vida vuelve al maximo
     * @param left es el limite de la izquierda para el empuje
     * @param right es el limite de la derecha para el empuje
     */
    public void update(int left, int right) {
        if (height > 0 || verticalSpeed > 0) {
            height = height + verticalSpeed;
            verticalSpeed = verticalSpeed - 1;
            if (height <= 0) {
                height = 0;
                verticalSpeed = 0;
            }
        }
        if (kickTime > 0){
            kickTime = kickTime - 1;
        }
        if (stunTime > 0) {
            stunTime = stunTime - 1;
            if (stunTime == 0) {
                life = getMaxLife();
            }
        }
        if (effectTime > 0) {
            if (effect == PUSH) {
                push(pushDirection * PUSH_SPEED, left, right);
            }
            effectTime = effectTime - 1;
            if (effectTime == 0) {
                effect = NONE;
            }
        }
    }

    /**
     * le suma un gol al jugador
     */
    public void addGoal() {
        goals = goals + 1;
    }

    /**
     * @return los goles que lleva el jugador en el partido
     */
    public int getGoals() {
        return goals;
    }

    /**
     * le suma puntos al jugador, se ganan al terminar un partido
     * @param amount son los puntos que se le suman
     */
    public void addPoints(int amount) {
        points = points + amount;
    }

    /**
     * @return los puntos que ha ganado el jugador
     */
    public int getPoints() {
        return points;
    }

    /**
     * principio de una patada si no esta pateando ya
     */
    public void startKick() {
        if (!isStunned() && kickTime == 0) {
            kickTime = KICK_TIME;
            kickUsed = false;
        }
    }

    /**
     * @return true si el jugador esta con el pie estirado
     */
    public boolean isKicking(){
        return kickTime > 0;
    }

    /**
     * @return los pasos que le quedan a la patada
     */
    public int getKickTime() {
        return kickTime;
    }

    /**
     * @return true si aun no ha pegado a nada la patada
     */
    public boolean canHit() {
        return kickTime > 0 && !kickUsed;
    }

    /**
     * la patada le esta pegando a algo
     */
    public void useKick() {
        kickUsed = true;
    }

    /**
     * cada punto de patada por encima de 1 suma un 10% mas de daño
     * @return el multiplicador de la patada
     */
    public double getKickMultiplier() {
        return 1 + 0.10 * (kick - 1);
    }

    /**
     * @return el daño que hace una patada
     */
    public int getKickDamage() {
        return (int) Math.round(KICK_DAMAGE * getKickMultiplier());
    }

    /**
     * le quita vida al jugador, la vida nunca baja de cero
     * si llega a cero queda aturdido 3 segundos
     * un jugador aturdido no recibe daño
     * @param damage es la vida que va perdiendo el jugador
     */
    public void receiveDamage(int damage) {
        if (!isStunned()) {
            life = life - damage;
            if (life <= 0) {
                life = 0;
                stunTime = STUN_TIME;
            }
        }
    }

    /**
     * @return la vida que le queda a un jugador
     */
    public int getLife() {
        return life;
    }

    /**
     * cada punto de vida por encima de 1 suma un 10% mas de vida
     * @return la vida maxima que tiene el jugador
     */
    public int getMaxLife() {
        return (int) Math.round(BASE_LIFE * (1 + 0.10 * (survival - 1)));
    }

    /**
     * @return true si no se puede mover, saltar ni patear
     */
    public boolean isStunned() {
        return stunTime > 0 || effect == KNOCK;
    }

    /**
     * @return true si esta electrocutado
     */
    public boolean isShocked() {
        return effect == SHOCK;
    }

        /**
     * @return true si esta confundido, o sea con los controles de movimiento invertidos
     */
    public boolean isConfused() {
        return effect == CONFUSE;
    }

    /**
     * lo confunde: sus controles de movimiento quedan invertidos
     * reemplaza el efecto anterior
     * @param frames es cuanto dura el efecto, en pasos del reloj
     */
    public void applyConfusion(int frames) {
        effect = CONFUSE;
        effectTime = frames;
    }

    /**
     * lo empuja hacia un lado, reemplaza el efecto anterior
     * @param direction es 1 si lo empujan a la der, y -1 a la izq
     * @param frames es cuanto dura el efecto, en pasos del reloj
     */
    public void applyPush(int direction, int frames) {
        effect = PUSH;
        effectTime = frames;
        pushDirection = direction;
    }

    /**
     * lo electrocuta: no salta y va a la mitad de velocidad
     * reemplaza el efecto anterior
     * @param frames es cuanto dura el efecto, en pasos del reloj
     */
    public void applyShock(int frames) {
        effect = SHOCK;
        effectTime = frames;
    }

    /**
     * lo lanza al aire y lo deja inconsciente, reemplaza el efecto anterior
     * @param frames es cuanto dura el efecto, en pasos del reloj
     */
    public void applyKnockOut(int frames) {
        effect = KNOCK;
        effectTime = frames;
        verticalSpeed = KNOCK_LIFT;
    }

    /**
     * activa el poder: la barra se vacia y queda el aura
     * solo si la barra esta llena y no tiene ya otro poder activo
     */
    public void armPower() {
        if (powerBar >= MAX_POWER && !powerArmed && !isStunned()) {
            powerArmed = true;
            powerBar = 0;
        }
    }

    /**
     * @return true si tiene el aura, o sea el poder listo para el siguiente toque al balon
     */
    public boolean isPowerArmed() {
        return powerArmed;
    }

    /**
     * lanza el tiro de poder si tiene el aura, y el aura desaparece
     * @param rival es el jugador al que puede golpear el tiro
     * @param ball es el balon del partido
     * @param direction es 1 si el tiro va a la der, y -1 si va a la izq
     */
    public void usePower(Player rival, Ball ball, int direction) {
        if (powerArmed) {
            ball.powerShot(power, this, rival, direction);
            powerArmed = false;
        }
    }

    /**
     * le suma carga a la barra de poder, sin pasar del maximo
     * @param amount es lo que se le suma
     */
    public void chargePower(int amount) {
        powerBar = powerBar + amount;
        if (powerBar > MAX_POWER) {
            powerBar = MAX_POWER;
        }
    }

    /**
     * pasa un segundo: la barra se llena en 10 segundos
     */
    public void passSecond() {
        chargePower(POWER_CHARGE);
    }

    /**
     * @return lo que lleva cargada la barra de poder
     */
    public int getPowerBar() {
        return powerBar;
    }

    /**
     * @return lo que tiene la barra de poder cuando esta llena
     */
    public int getMaxPower() {
        return MAX_POWER;
    }
}