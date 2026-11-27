package domain;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
/**
 * pruebas de los  poder
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class PowerTest {
    private Player user;
    private Player rival;
    private Ball ball;
    private DOPOSoccer game;


    @Before
    public void setUp() {
        user = new Player("Uno", "Colombia", 3, 1, 3, 3, new BlueAura());
        rival = new Player("Dos", "Brasil", 3, 3, 3, 3, new Tornado());
        game = new DOPOSoccer(user, rival, 880, 480);
        rival.place(400);
        ball = game.getBall();
        ball.place(300, 50);
    }

    @Test
    public void shouldConfuseTheRival() {
        Power power = new Confusion();
        power.hit(user, rival, 1, game);
        assertTrue(rival.isConfused());
        assertEquals(rival.getMaxLife() - 30, rival.getLife());
    }

    @Test
    public void shouldShockTheRival() {
        Power power = new Lightning();
        power.hit(user, rival, 1, game);
        assertTrue(rival.isShocked());
        assertEquals(rival.getMaxLife() - 60, rival.getLife());
    }

    @Test
    public void shouldKnockOutTheRival() {
        Power power = new Tornado();
        power.hit(user, rival, 1, game);
        assertTrue(rival.isStunned());
    }

    @Test
    public void shouldPushTheRival() {
        Power power = new BlueAura();
        power.hit(user, rival, 1, game);
        rival.update(25, 855);
        assertTrue(rival.getX() > 400);
    }

    @Test
    public void shouldSplitTheBallInEight() {
        Power power = new Illusion();
        power.hit(user, rival, 1, game);
        assertEquals(7, ball.getFakes().size());
    }

    @Test
    public void shouldHaveNameAndDurationForEveryPower() {
        Power[] powers = {new BlueAura(), new Confusion(), new Illusion(), new Lightning(), new Tornado()};
        String[] names = {"Aura Azul", "Confusion", "Ilusion", "Rayo", "Tornado"};
        for (int i = 0; i < powers.length; i++) {
            assertEquals(names[i], powers[i].getName());
            assertTrue(powers[i].getFrames() > 0);
            assertTrue(powers[i].getSpeed() > 0);
        }
        assertTrue(new Tornado().isGroundShot());
        assertFalse(new Lightning().isGroundShot());
    }

    @Test
    public void shouldGoStraightOnTheGroundWithAGroundShot() {
        ball.powerShot(new Tornado(), user, rival, 1);
        assertEquals(16, ball.getHeight(), 0.01);
    }

    @Test
    public void shouldMoveTheOtherWayWhenConfused() {
        DOPOSoccer soccer = new DOPOSoccer(user, rival, 880, 480);
        rival.applyEffect(new ConfusionEffect(), 100);
        double start = rival.getX();
        soccer.moveLeft(2);
        assertTrue(rival.getX() > start);
        start = rival.getX();
        soccer.moveRight(2);
        assertTrue(rival.getX() < start);
    }
}