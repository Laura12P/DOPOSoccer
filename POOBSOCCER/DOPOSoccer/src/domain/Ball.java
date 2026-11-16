package domain;
import java.util.ArrayList;
import java.util.Random;

/**
 * el balon del partido
 * cae con la gravedad, rebota en el suelo, el techo y las paredes
 * y rebota cuando choca con un jugador
 * tambien sale disparado con los tiros de poder
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Ball extends GameObject {
    private static final int RADIUS = 12;
    private static final double GRAVITY = 0.25;
    private static final double BOUNCE = 0.8;
    private static final double FRICTION = 0.99;
    private double speedX;
    private double speedY;
    private Power shotPower;
    private Player shotOwner;
    private Player shotTarget;
    private int shotDirection;
    private ArrayList<Ball> fakes;
    private int fakeTime;

    /**
     * CONSTRUCTOR
     * crea el balon quieto en el suelo
     */
    public Ball() {
        x = 0;
        height = RADIUS;
        speedX = 0;
        speedY = 0;
        fakes = new ArrayList<Ball>();
    }

    /**
     * pone el balon en un lugar y lo deja quieto
     * @param x es la posicion horizontal del centro
     * @param height es la altura del centro sobre el suelo
     */
    public void place(double x, double height) {
        this.x = x;
        this.height = height;
        speedX = 0;
        speedY = 0;
        shotPower = null;
        fakes.clear();
        fakeTime = 0;
    }

    /**
     * mueve el balon un paso: gravedad y rebotes
     * mientras sale un tiro de poder no le afecta la gravedad
     * @param left es el limite de la izquierda
     * @param right es el limite de la derecha
     * @param ceiling es la altura maxima
     */
    public void update(int left, int right, int ceiling) {
        if (shotPower == null) {
            speedY = speedY - GRAVITY;
        }
        x = x + speedX;
        height = height + speedY;
        if (height <= RADIUS) {
            height = RADIUS;
            speedY = -speedY * BOUNCE;
            if (shotPower == null) {
                speedX = speedX * FRICTION;
            }
            if (speedY < 1) {
                speedY = 0;
            }
        }
        if (height >= ceiling - RADIUS) {
            height = ceiling - RADIUS;
            speedY = -speedY * BOUNCE;
        }
        if (x <= left + RADIUS) {
            x = left + RADIUS;
            speedX = -speedX * BOUNCE;
            shotPower = null;
        }
        if (x >= right - RADIUS) {
            x = right - RADIUS;
            speedX = -speedX * BOUNCE;
            shotPower = null;
        }
        if (fakeTime > 0) {
            fakeTime = fakeTime - 1;
            if (fakeTime == 0) {
                fakes.clear();
            }
        }
        for (int i = 0; i < fakes.size(); i++) {
            fakes.get(i).update(left, right, ceiling);
        }
    }

    /**
     * si el balon toca al jugador, lo saca de ahi y lo manda hacia afuera
     * el jugador se toma como dos circulos, el cuerpo y la cabeza
     * si es un tiro de poder y le pega al rival, le hace el daño y el efecto
     * @param player es el jugador con el que se revisa el choque
     * @return true si el balon toco al jugador
     */
    public boolean collideWithPlayer(Player player) {
        boolean touched = false;
        double[] heights = {20, 65};
        double[] radii = {20, 25};
        for (int i = 0; i < 2; i++) {
            double centerX = player.getX();
            double centerY = player.getHeight() + heights[i];
            double dx = x - centerX;
            double dy = height - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minimum = radii[i] + RADIUS;
            if (distance < minimum && distance > 0) {
                touched = true;
                double nx = dx / distance;
                double ny = dy / distance;
                x = centerX + nx * minimum;
                height = centerY + ny * minimum;
                double speed = Math.sqrt(speedX * speedX + speedY * speedY);
                if (speed < 3) {
                    speed = 3;
                }
                if (speed > 8) {
                    speed = 8;
                }
                speedX = nx * speed;
                speedY = ny * speed;
                if (shotPower != null && player == shotTarget) {
                    shotPower.hit(shotOwner, player, shotDirection, this);
                    shotPower = null;
                }
            }
        }
        return touched;
    }

    /**
     * le da una patada al balon, si venia un tiro de poder se acaba
     * @param speedX es la velocidad horizontal del balon cuando se da la patada
     * @param speedY es la velocidad vertical del balon cuando se da la patada
     */
    public void kick(double speedX, double speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
        shotPower = null;
    }

    /**
     * lanza el balon con un tiro de poder, recto y sin gravedad
     * @param power es el tiro de poder
     * @param owner es el jugador que lo lanza
     * @param target es el rival al que puede golpear
     * @param direction es 1 si va a la der, y -1 si va a la izq
     */
    public void powerShot(Power power, Player owner, Player target, int direction) {
        shotPower = power;
        shotOwner = owner;
        shotTarget = target;
        shotDirection = direction;
        speedX = direction * power.getSpeed();
        speedY = 0;
        if (power.isGroundShot()) {
            height = RADIUS;
        }
    }

    /**
     * divide el balon en 8: este es el real y se crean 7 falsos
     * @param frames es cuanto duran los balones falsos, en pasos del reloj
     */
    public void split(int frames) {
        Random random = new Random();
        fakes.clear();
        for (int i = 0; i < 7; i++) {
            Ball fake = new Ball();
            fake.place(x, height);
            fake.kick(random.nextDouble() * 16 - 8, random.nextDouble() * 10);
            fakes.add(fake);
        }
        fakeTime = frames;
    }

    /**
     * @return los balones falsos que hay en este momento
     */
    public ArrayList<Ball> getFakes() {
        return fakes;
    }
}