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
    private static final double BASE_SPEED = 5;
    private static final double BASE_JUMP = 12;

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
    private Effect effect;
    private int effectTime;
    private int points;
    private int penalty;

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
     * el efecto que tenga puede cambiar la velocidad
     * @param limit es lo mas a la izquierda que puede llegar
     */
    public void moveLeft(int limit) {
        if (!isStunned()) {
            double step = BASE_SPEED * (1 + 0.10 * (speed - 1));
            if (effect != null) {
                step = step * effect.getSpeedFactor();
            }
            x = x - step;
            if (x < limit) {
                x = limit;
            }
        }
    }

    /**
     * mueve al jugador a la derecha segun su velocidad
     * el efecto que tenga puede cambiar la velocidad
     * @param limit es lo mas a la derecha que puede llegar
     */
    public void moveRight(int limit) {
        if (!isStunned()) {
            double step = BASE_SPEED * (1 + 0.10 * (speed - 1));
            if (effect != null) {
                step = step * effect.getSpeedFactor();
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
     * si el efecto no lo deja saltar, no salta
     */
    public void jump() {
        if (!isStunned() && (effect == null || effect.canJump()) && height == 0 && verticalSpeed == 0) {
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
            effect.step(this, left, right);
            effectTime = effectTime - 1;
            if (effectTime == 0) {
                effect = null;
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
     * le quita un gol al jugador, nunca baja de cero
     */
    public void removeGoal() {
        if (goals > 0) {
            goals = goals - 1;
        }
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
     * le quita puntos al jugador, se descuentan de los que gana al terminar el partido
     * @param amount son los puntos que pierde
     */
    public void losePoints(int amount) {
        penalty = penalty + amount;
    }

    /**
     * @return los puntos que gano el jugador en el partido, sin bajar de cero
     */
    public int getPoints() {
        return Math.max(0, points - penalty);
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
        return stunTime > 0 || (effect != null && effect.isStunned());
    }

    /**
     * @return true si esta electrocutado
     */
    public boolean isShocked() {
        return effect instanceof ShockEffect;
    }

    /**
     * @return true si esta confundido, o sea con los controles de movimiento invertidos
     */
    public boolean isConfused() {
        return effect != null && effect.invertsControls();
    }

    /**
     * le pone un efecto al jugador, reemplaza el efecto anterior
     * @param effect es el efecto que recibe
     * @param frames es cuanto dura el efecto, en pasos del reloj
     */
    public void applyEffect(Effect effect, int frames) {
        this.effect = effect;
        effectTime = frames;
        effect.start(this);
    }

    /**
     * lanza al jugador al aire
     * @param speed es la velocidad vertical con la que sale
     */
    public void launch(double speed) {
        verticalSpeed = speed;
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
