package presentation;
import static org.junit.Assert.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.DOPOSoccer;
import domain.DOPOSoccerException;
import domain.KnockOutEffect;
import domain.Machines;
import domain.MatchMode;
import domain.Player;
import domain.ShockEffect;

/**
 * pruebas de la pantalla del partido y de la cancha
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class GameTest {
    private static final int OFFENSIVE = 1;
    private static final int DEFENSIVE = 2;
    private MainWindow window;
    private GamePanel game;
    private FieldPanel field;
    private DOPOSoccer soccer;
    private Player one;
    private Player two;

    @Before
    public void setUp() {
        window = new MainWindow();
        game = new GamePanel(window);
        game.setSize(900, 560);
        game.doLayout();
        game.prepareMatch(PlayerSelector.atRandom(), PlayerSelector.atRandom(), true, 99, null);
        field = new FieldPanel(game);
        field.setSize(880, 480);
        one = PlayerSelector.atRandom().copy();
        two = PlayerSelector.atRandom().copy();
        soccer = new DOPOSoccer(one, two, 880, field.getFieldHeight(), MatchMode.create(true, 99));
        field.setSoccer(soccer);
        field.stop();
    }

    @After
    public void tearDown() {
        game.stopMatch();
        window.dispose();
    }

    /**
     * oprime una tecla en la cancha
     * @param code es el codigo de la tecla
     */
    private void press(int code) {
        field.keyPressed(new KeyEvent(field, KeyEvent.KEY_PRESSED, 0, 0, code, KeyEvent.CHAR_UNDEFINED));
    }

    /**
     * suelta una tecla en la cancha
     * @param code es el codigo de la tecla
     */
    private void release(int code) {
        field.keyReleased(new KeyEvent(field, KeyEvent.KEY_RELEASED, 0, 0, code, KeyEvent.CHAR_UNDEFINED));
    }

    /**
     * avanza la cancha los pasos que se indiquen
     * @param steps son los pasos del reloj
     */
    private void step(int steps) {
        for (int i = 0; i < steps; i++) {
            field.actionPerformed(null);
        }
    }

    /**
     * dibuja la cancha en una imagen
     */
    private void draw() {
        BufferedImage image = new BufferedImage(880, 480, BufferedImage.TYPE_INT_RGB);
        field.paint(image.getGraphics());
    }

    /**
     * electrocuta al jugador
     * @param player es el jugador afectado
     */
    private void shock(Player player) {
        player.applyEffect(new ShockEffect(), 100);
    }

    /**
     * deja al jugador inconsciente en el aire
     * @param player es el jugador afectado
     */
    private void knockOut(Player player) {
        player.applyEffect(new KnockOutEffect(), 100);
    }

    /**
     * @param index es la posicion de la maquina en el catalogo
     * @return el nombre de esa maquina
     */
    private String machineName(int index) {
        return Machines.getNames().get(index);
    }

    @Test
    public void shouldShowTheGoalTargetInAMatchByGoals() {
        assertTrue(Finder.hasLabel(game, soccer.getModeText()));
        assertTrue(Finder.hasLabel(game, "0-0"));
    }

    @Test
    public void shouldShowTheTimeInAMatchByTime() {
        game.prepareMatch(PlayerSelector.atRandom(), PlayerSelector.atRandom(), false, 10, null);
        assertTrue(Finder.hasLabel(game, "Tiempo: 10:00"));
    }

    @Test
    public void shouldShowWhoIsTheMachine() {
        game.prepareMatch(PlayerSelector.atRandom(), PlayerSelector.atRandom(), true, 99,
                machineName(OFFENSIVE));
        assertTrue(Finder.hasLabel(game, "Maquina " + machineName(OFFENSIVE)));
        game.begin();
    }

    @Test
    public void shouldMovePlayerOneWithTheKeys() {
        double start = one.getX();
        press(KeyEvent.VK_D);
        step(10);
        release(KeyEvent.VK_D);
        assertTrue(one.getX() > start);
        start = one.getX();
        press(KeyEvent.VK_A);
        step(10);
        release(KeyEvent.VK_A);
        assertTrue(one.getX() < start);
        press(KeyEvent.VK_W);
        step(1);
        release(KeyEvent.VK_W);
        assertTrue(one.getHeight() > 0);
    }

    @Test
    public void shouldMovePlayerTwoWithTheArrows() {
        double start = two.getX();
        press(KeyEvent.VK_LEFT);
        step(10);
        release(KeyEvent.VK_LEFT);
        assertTrue(two.getX() < start);
        start = two.getX();
        press(KeyEvent.VK_RIGHT);
        step(10);
        release(KeyEvent.VK_RIGHT);
        assertTrue(two.getX() > start);
        press(KeyEvent.VK_UP);
        step(1);
        release(KeyEvent.VK_UP);
        assertTrue(two.getHeight() > 0);
    }

    @Test
    public void shouldKickAndArmThePowerWithTheKeys() {
        one.chargePower(one.getMaxPower());
        two.chargePower(two.getMaxPower());
        press(KeyEvent.VK_S);
        press(KeyEvent.VK_DOWN);
        press(KeyEvent.VK_Q);
        press(KeyEvent.VK_SPACE);
        step(1);
        assertTrue(one.isKicking());
        assertTrue(two.isKicking());
        assertTrue(one.isPowerArmed());
        assertTrue(two.isPowerArmed());
        draw();
    }

    @Test
    public void shouldNotMoveTheMachineWithTheArrows() throws DOPOSoccerException {
        soccer.setMachine(machineName(DEFENSIVE));
        double start = two.getX();
        press(KeyEvent.VK_LEFT);
        press(KeyEvent.VK_DOWN);
        step(10);
        assertTrue(two.getX() >= start);
    }

    @Test
    public void shouldNotKickTheMachineWithTheKey() throws DOPOSoccerException {
        soccer.setMachine(machineName(DEFENSIVE));
        press(KeyEvent.VK_DOWN);
        step(1);
        assertFalse(two.isKicking());
    }

    @Test
    public void shouldDrawTheFieldWithTheEffects() {
        draw();
        shock(one);
        knockOut(two);
        soccer.getBall().split(100);
        draw();
        shock(two);
        one.receiveDamage(one.getMaxLife());
        draw();
        assertEquals(7, soccer.getBall().getFakes().size());
    }

    @Test
    public void shouldDrawTheAuraOfEveryPower() {
        for (int i = 0; i < 30; i++) {
            Player player = PlayerSelector.atRandom().copy();
            player.chargePower(player.getMaxPower());
            player.armPower();
            DOPOSoccer match = new DOPOSoccer(player, player.copy(), 880, field.getFieldHeight());
            field.setSoccer(match);
            field.stop();
            draw();
        }
        field.resume();
        field.stop();
    }

    @Test
    public void shouldIgnoreOtherKeys() {
        double start = one.getX();
        press(KeyEvent.VK_Z);
        field.keyTyped(new KeyEvent(field, KeyEvent.KEY_TYPED, 0, 0, KeyEvent.VK_UNDEFINED, 'z'));
        release(KeyEvent.VK_Z);
        step(1);
        assertEquals(start, one.getX(), 0.01);
    }
}