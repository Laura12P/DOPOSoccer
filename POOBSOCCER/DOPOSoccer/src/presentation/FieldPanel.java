package presentation;
import domain.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import domain.Ball;
import domain.DOPOSoccer;

/**
 * cancha de juego
 * dibuja la cancha y mueve a los jugadores con el teclado
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class FieldPanel extends JPanel implements ActionListener, KeyListener {
    private static final Color GRASS = new Color(40, 120, 60);
    private static final Color LINES = Color.WHITE;
    private static final Color NET_FILL = new Color(255, 255, 255, 60);
    private static final Color NET_LINES = new Color(255, 255, 255, 150);
    private static final Color POST_SHADOW = new Color(0, 0, 0, 130);
    private static final Color COLOR_ONE = new Color(230, 190, 40);
    private static final Color COLOR_TWO = new Color(200, 40, 40);
    private static final int GROUND_MARGIN = 40;
    
    private GamePanel game;
    private DOPOSoccer soccer;
    private Timer timer;
    private Image background;
    private Image ballImage;
    private boolean leftOne;
    private boolean rightOne;
    private boolean jumpOne;
    private boolean leftTwo;
    private boolean rightTwo;
    private boolean jumpTwo;
    private boolean kickOne;
    private boolean kickTwo;
    private boolean powerOne;
    private boolean powerTwo;
    private Image headOne;
    private Image headTwo;

    /**
     * CONSTRUCTOR
     * hace la cancha con un fondo verde y prepara el teclado y el reloj
     * @param game es el panel del juego al que pertenece la cancha
     */
    public FieldPanel(GamePanel game) {
        this.game = game;
        setBackground(GRASS);
        background = new ImageIcon("imagenes/cancha.png").getImage();
        Image ball = new ImageIcon("imagenes/balon.png").getImage();
        if (ball.getWidth(null) > 0) {
            ballImage = new ImageIcon(ball.getScaledInstance(32, 32, Image.SCALE_SMOOTH)).getImage();
        }
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        timer = new Timer(16, this);
    }

    /**
     * recibe el partido, deja las teclas sin oprimir y arranca el reloj
     * @param soccer es el partido que se va a jugar
     */
    public void setSoccer(DOPOSoccer soccer) {
        headOne = loadHead(soccer.getPlayerOne().getName());
        headTwo = loadHead(soccer.getPlayerTwo().getName());
        this.soccer = soccer;
        releaseKeys();
        timer.start();
        repaint();
    }

    /**
     * para el reloj, se usa cuando se abandona el partido
     */
    public void stop() {
        timer.stop();
        releaseKeys();
    }

    /**
     * deja todas las teclas como no oprimidas
     */
    private void releaseKeys() {
        leftOne = false;
        rightOne = false;
        jumpOne = false;
        leftTwo = false;
        rightTwo = false;
        jumpTwo = false;
        kickOne = false;
        kickTwo = false;
        powerOne = false;
        powerTwo = false;
    }

    /**
     * en cada paso del reloj mueve a los jugadores segun las teclas oprimidas
     * si el jugador 2 es la maquina, sus teclas no hacen nada
     * @param e es el evento del reloj
     */
    public void actionPerformed(ActionEvent e) {
        if (leftOne) {
            soccer.moveLeft(1);
        }
        if (rightOne) {
            soccer.moveRight(1);
        }
        if (jumpOne) {
            soccer.jump(1);
        }
        if (!soccer.isMachine(2)) {
            if (leftTwo) {
                soccer.moveLeft(2);
            }
            if (rightTwo) {
                soccer.moveRight(2);
            }
            if (jumpTwo) {
                soccer.jump(2);
            }
        }
        if (kickOne) {
            soccer.kick(1);
            kickOne = false;
        }
        if (kickTwo && !soccer.isMachine(2)) {
            soccer.kick(2);
        }
        kickTwo = false;
        if (powerOne) {
            soccer.activatePower(1);
            powerOne = false;
        }
        if (powerTwo && !soccer.isMachine(2)) {
            soccer.activatePower(2);
        }
        powerTwo = false;
        if (soccer.update()) {
            game.updateScoreboard();
        }
        repaint();
    }

    /**
     * marca la tecla como oprimida
     * @param e es el evento del teclado
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                leftOne = true;
                break;
            case KeyEvent.VK_D:
                rightOne = true;
                break;
            case KeyEvent.VK_W:
                jumpOne = true;
                break;
            case KeyEvent.VK_LEFT:
                leftTwo = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightTwo = true;
                break;
            case KeyEvent.VK_UP:
                jumpTwo = true;
                break;
            case KeyEvent.VK_S:
                kickOne = true;
                break;
            case KeyEvent.VK_DOWN:
                kickTwo = true;
                break;
            case KeyEvent.VK_Q:
                powerOne = true;
                break;
            case KeyEvent.VK_SPACE:
                powerTwo = true;
                break;
        }
    }

    /**
     * marca la tecla como soltada
     * @param e es el evento del teclado
     */
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                leftOne = false;
                break;
            case KeyEvent.VK_D:
                rightOne = false;
                break;
            case KeyEvent.VK_W:
                jumpOne = false;
                break;
            case KeyEvent.VK_LEFT:
                leftTwo = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightTwo = false;
                break;
            case KeyEvent.VK_UP:
                jumpTwo = false;
                break;
        }
    }

    /**
     * no se usa
     * @param e es el evento del teclado
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * este metodo lo llaman cada vez que necesitan pintar el panel
     * @param g es el pincel con el que se dibuja sobre el panel
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int ground = getHeight() - GROUND_MARGIN;
        if (background.getWidth(null) > 0) {
            g.drawImage(background, 0, 0, width, getHeight(), this);
        } else {
            drawLines(g, width, ground);
        }
        drawGoals(g, width, ground);
        if (soccer != null) {
            Player playerOne = soccer.getPlayerOne();
            Player playerTwo = soccer.getPlayerTwo();
            Ball ball = soccer.getBall();
            int xOne = (int) playerOne.getX();
            int xTwo = (int) playerTwo.getX();
            int groundOne = ground - (int) playerOne.getHeight();
            int groundTwo = ground - (int) playerTwo.getHeight();
            Color colorOne = COLOR_ONE;
            Color colorTwo = COLOR_TWO;
            if (playerOne.isShocked()) {
                colorOne = Color.ORANGE;
            }
            if (playerTwo.isShocked()) {
                colorTwo = Color.ORANGE;
            }
            if (playerOne.isStunned()) {
                colorOne = Color.LIGHT_GRAY;
            }
            if (playerTwo.isStunned()) {
                colorTwo = Color.LIGHT_GRAY;
            }
            if (playerOne.isPowerArmed()) {
                drawAura(g, xOne, groundOne, playerOne);
            }
            if (playerTwo.isPowerArmed()) {
                drawAura(g, xTwo, groundTwo, playerTwo);
            }
            drawPlayer(g, xOne, groundOne, colorOne, headOne);
            drawPlayer(g, xTwo, groundTwo, colorTwo, headTwo);
            if (playerOne.isKicking()) {
                drawFoot(g, xOne, groundOne, 1, playerOne.getKickTime());
            }
            if (playerTwo.isKicking()) {
                drawFoot(g, xTwo, groundTwo, -1, playerTwo.getKickTime());
            }
            drawLife(g, playerOne, ground);
            drawLife(g, playerTwo, ground);
            drawPower(g, playerOne, ground);
            drawPower(g, playerTwo, ground);
            g.setColor(Color.WHITE);
            g.drawString(playerOne.getName(), xOne - 30, groundOne - 100);
            g.drawString(playerTwo.getName(), xTwo - 30, groundTwo - 100);
            drawBall(g, (int) ball.getX(), ground - (int) ball.getHeight());
            for (int i = 0; i < ball.getFakes().size(); i++) {
                Ball fake = ball.getFakes().get(i);
                drawBall(g, (int) fake.getX(), ground - (int) fake.getHeight());
            }
        }
    }

    /**
     * el encargado de dibujar las lineas del suelo
     * @param g es el pincel
     * @param width es el ancho del panel
     * @param ground es la altura de donde esta el suelo
     */
    private void drawLines(Graphics g, int width, int ground) {
        g.setColor(LINES);
        g.fillRect(0, ground, width, 4);
        g.fillRect(width / 2 - 2, 0, 4, ground);
        g.drawOval(width / 2 - 50, ground / 2 - 50, 100, 100);
    }

    /**
     * se dibujan los arcos
     * @param g es el pincel
     * @param width ancho actual del panel
     * @param ground es la altura del suelo
     */
    private void drawGoals(Graphics g, int width, int ground) {
        Graphics2D g2 = (Graphics2D) g;
        int top = ground - DOPOSoccer.GOAL_HEIGHT;
        int rightStart = width - DOPOSoccer.GOAL_WIDTH - 1;
        g.setColor(NET_FILL);
        g.fillRect(0, top, DOPOSoccer.GOAL_WIDTH, DOPOSoccer.GOAL_HEIGHT);
        g.fillRect(rightStart, top, DOPOSoccer.GOAL_WIDTH + 1, DOPOSoccer.GOAL_HEIGHT);
        g.setColor(NET_LINES);
        for (int y = top + 16; y < ground; y += 16) {
            g.drawLine(0, y, DOPOSoccer.GOAL_WIDTH, y);
            g.drawLine(rightStart, y, width - 1, y);
        }
        for (int x = 15; x < DOPOSoccer.GOAL_WIDTH; x += 15) {
            g.drawLine(x, top, x, ground);
            g.drawLine(rightStart + x, top, rightStart + x, ground);
        }
        for (int pass = 0; pass < 2; pass++) {
            if (pass == 0) {
                g2.setStroke(new BasicStroke(9));
                g.setColor(POST_SHADOW);
            } else {
                g2.setStroke(new BasicStroke(5));
                g.setColor(LINES);
            }
            g.drawLine(DOPOSoccer.GOAL_WIDTH, top, DOPOSoccer.GOAL_WIDTH, ground);
            g.drawLine(0, top, DOPOSoccer.GOAL_WIDTH, top);
            g.drawLine(rightStart, top, rightStart, ground);
            g.drawLine(rightStart, top, width, top);
        }
        g2.setStroke(new BasicStroke(1));
    }

    /**
     * dibuja el aura del jugador que tiene el poder activo
     * el color depende del poder del jugador
     * @param g es el pincel
     * @param x es la posicion horizontal del jugador
     * @param ground es la altura donde pisa el jugador
     * @param player es el jugador que tiene el aura
     */
    private void drawAura(Graphics g, int x, int ground, Player player) {
        int[] aura = player.getPower().getAura();
        g.setColor(new Color(aura[0], aura[1], aura[2], aura[3]));
        g.fillOval(x - 40, ground - 105, 80, 110);
    }

    private Image loadHead(String name) {
        String file = name.toLowerCase().replace(" ", "");
        Image head = new ImageIcon("imagenes/cabezas/" + file + ".png").getImage();
        if (head.getWidth(null) > 0) {
            return new ImageIcon(head.getScaledInstance(60, 60, Image.SCALE_SMOOTH)).getImage();
        }
        return null;
    }
    /**
     * dibuja un jugador sobre el campo
     * @param g es el pincel
     * @param x es la posicion horizontal
     * @param ground es la altura donde pisa el jugador
     * @param color es el color de la camiseta del jugador
     */
    private void drawPlayer(Graphics g, int x, int ground, Color color, Image head) {
        g.setColor(color);
        g.fillRect(x - 15, ground - 40, 30, 40);
        if (head != null) {
            g.drawImage(head, x - 30, ground - 95, this);
        } else {
            g.fillOval(x - 25, ground - 90, 50, 50);
            g.setColor(Color.BLACK);
            g.drawOval(x - 25, ground - 90, 50, 50);
        }
    }

    /**
     * dibuja la pierna del jugador al patear, sube y baja con la patada
     * @param g es el pincel
     * @param x es la posicion horizontal del jugador
     * @param ground es la altura de donde esta pisando el jugador
     * @param direction es 1 si patea a der, y -1 si patea a la izq
     * @param kickTime son los pasos que le quedan a la patada
     */
    private void drawFoot(Graphics g, int x, int ground, int direction, int kickTime) {
        double progress = (double) (Player.KICK_TIME - kickTime) / Player.KICK_TIME;
        double angle = Math.toRadians(-35 + 70 * Math.sin(Math.PI * progress));
        int hipY = ground - 12;
        int footX = x + direction * (int) (25 * Math.cos(angle));
        int footY = hipY - (int) (25 * Math.sin(angle));
        g.setColor(Color.BLACK);
        g.fillOval(footX - 12, footY - 7, 24, 14);
    }

    /**
     * dibuja la barra de vida de los jugadores
     * @param g es el pincel
     * @param player es el jugador que se le va a dibujar la vida
     * @param ground es la altura del suelo
     */
    private void drawLife(Graphics g, Player player, int ground) {
        int x = (int) player.getX() - 25;
        int y = ground - (int) player.getHeight() - 125;
        int full = 50 * player.getLife() / player.getMaxLife();
        g.setColor(Color.BLACK);
        g.fillRect(x, y, 50, 6);
        g.setColor(Color.RED);
        g.fillRect(x, y, full, 6);
    }

    /**
     * dibuja la barra de poder debajo de la vida
     * es amarilla cuando esta llena
     * @param g es el pincel
     * @param player es el jugador que se le va a dibujar la barra
     * @param ground es la altura del suelo
     */
    private void drawPower(Graphics g, Player player, int ground) {
        int x = (int) player.getX() - 25;
        int y = ground - (int) player.getHeight() - 116;
        int full = 50 * player.getPowerBar() / player.getMaxPower();
        g.setColor(Color.BLACK);
        g.fillRect(x, y, 50, 6);
        if (player.getPowerBar() >= player.getMaxPower()) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.CYAN);
        }
        g.fillRect(x, y, full, 6);
    }

    /**
     * es el que dibuja el balon, con la imagen balon.png, o un circulo blanco si no existe
     * @param g es el pincel
     * @param x es la posicion horizontal del centro del balon
     * @param y es la posicion vertical del centro del balon
     */
    private void drawBall(Graphics g, int x, int y) {
        if (ballImage != null) {
            g.drawImage(ballImage, x - 16, y - 16, this);
            return;
        }
        g.setColor(Color.WHITE);
        g.fillOval(x - 16, y - 16, 32, 32);
        g.setColor(Color.BLACK);
        g.drawOval(x - 16, y - 16, 32, 32);
    }

    /**
     * @return la altura de la cancha desde el suelo al techo
     */
    public int getFieldHeight(){
        return getHeight() - GROUND_MARGIN;
    }

    /**
     * sigue el partido despues de una pausa 
     */
    public void resume(){
        timer.start();
        requestFocusInWindow();
    }
}
