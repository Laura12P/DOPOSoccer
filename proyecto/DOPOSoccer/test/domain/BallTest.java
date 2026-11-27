package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas del balon: gravedad, rebotes, patadas, tiros de poder y choques con jugadores
 */
public class BallTest {
    private Ball ball;
    private DOPOSoccer game;
    private Player one;
    private Player two;

    @Before
    public void setUp() {
        one = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        two = new Player("B", "P", 1, 1, 1, 1, new Lightning());
        game = new DOPOSoccer(one, two, 900, 500);
        ball = new Ball();
    }

    @Test
    public void shouldFallWithGravity() {
        ball.place(450, 200);
        ball.update(0, 900, 500);
        assertEquals(199.75, ball.getHeight(), 0.001);
        assertEquals(450, ball.getX(), 0.001);
    }

    @Test
    public void shouldNeverGoBelowTheFloorAndShouldRest() {
        ball.place(450, 100);
        for (int i = 0; i < 1000; i++) {
            ball.update(0, 900, 500);
            assertTrue(ball.getHeight() >= 16);
        }
        assertEquals(16, ball.getHeight(), 0.001);
    }

    @Test
    public void shouldBounceAtTheCeiling() {
        ball.place(450, 480);
        ball.kick(0, 10);
        ball.update(0, 900, 500);
        assertEquals(484, ball.getHeight(), 0.001);
    }

    @Test
    public void shouldBounceAtTheLeftWall() {
        ball.place(20, 100);
        ball.kick(-10, 0);
        ball.update(0, 900, 500);
        assertEquals(16, ball.getX(), 0.001);
        ball.update(0, 900, 500);
        assertTrue(ball.getX() > 16);
    }

    @Test
    public void shouldBounceAtTheRightWall() {
        ball.place(880, 100);
        ball.kick(10, 0);
        ball.update(0, 900, 500);
        assertEquals(884, ball.getX(), 0.001);
        ball.update(0, 900, 500);
        assertTrue(ball.getX() < 884);
    }

    @Test
    public void shouldMoveWithAKick() {
        ball.place(450, 300);
        ball.kick(5, 0);
        ball.update(0, 900, 500);
        assertEquals(455, ball.getX(), 0.001);
    }

    @Test
    public void shouldStopMovingWhenPlaced() {
        ball.place(450, 300);
        ball.kick(5, 5);
        ball.place(100, 200);
        ball.update(0, 900, 500);
        assertEquals(100, ball.getX(), 0.001);
    }

    @Test
    public void shouldFlyStraightWithoutGravityInAPowerShot() {
        ball.place(450, 200);
        ball.powerShot(new BlueAura(), one, two, 1);
        for (int i = 0; i < 5; i++) {
            ball.update(0, 900, 500);
        }
        assertEquals(530, ball.getX(), 0.001);
        assertEquals(200, ball.getHeight(), 0.001);
    }

    @Test
    public void shouldShootToTheLeft() {
        ball.place(450, 200);
        ball.powerShot(new BlueAura(), two, one, -1);
        ball.update(0, 900, 500);
        assertEquals(434, ball.getX(), 0.001);
    }

    @Test
    public void shouldGoAtGroundLevelWithAGroundShot() {
        ball.place(450, 200);
        ball.powerShot(new Tornado(), one, two, 1);
        assertEquals(16, ball.getHeight(), 0.001);
    }

    @Test
    public void shouldGetGravityBackAfterAKick() {
        ball.place(450, 200);
        ball.powerShot(new BlueAura(), one, two, 1);
        ball.kick(0, 0);
        ball.update(0, 900, 500);
        assertTrue(ball.getHeight() < 200);
    }

    @Test
    public void shouldEndThePowerShotAgainstAWall() {
        ball.place(30, 100);
        ball.powerShot(new BlueAura(), two, one, -1);
        ball.update(0, 900, 500);
        assertEquals(100, ball.getHeight(), 0.001);
        ball.update(0, 900, 500);
        assertTrue(ball.getHeight() < 100);
    }

    @Test
    public void shouldNotTouchAPlayerThatIsFarAway() {
        ball.place(500, 20);
        assertFalse(ball.collideWithPlayer(one, game));
        assertEquals(500, ball.getX(), 0.001);
    }

    @Test
    public void shouldPushTheBallOutOfTheBodyToTheRight() {
        ball.place(235, 20);
        assertTrue(ball.collideWithPlayer(one, game));
        assertEquals(261, ball.getX(), 0.001);
        ball.update(0, 900, 500);
        assertEquals(264, ball.getX(), 0.001);
    }

    @Test
    public void shouldPushTheBallOutOfTheBodyToTheLeft() {
        ball.place(215, 20);
        assertTrue(ball.collideWithPlayer(one, game));
        assertEquals(189, ball.getX(), 0.001);
        ball.update(0, 900, 500);
        assertEquals(186, ball.getX(), 0.001);
    }

    @Test
    public void shouldBounceUpFromTheHead() {
        ball.place(225, 100);
        assertTrue(ball.collideWithPlayer(one, game));
        assertEquals(106, ball.getHeight(), 0.001);
    }

    @Test
    public void shouldLimitTheSpeedAfterTheHit() {
        ball.place(235, 20);
        ball.kick(-100, 0);
        ball.collideWithPlayer(one, game);
        ball.update(0, 900, 500);
        assertEquals(269, ball.getX(), 0.001);
    }

    @Test
    public void shouldHitWiderWhenTheBallIsBigger() {
        ball.place(270, 20);
        assertFalse(ball.collideWithPlayer(one, game));
        ball.place(270, 20);
        ball.changeBody(0.25, 0.8, 32, 100);
        assertTrue(ball.collideWithPlayer(one, game));
    }

    @Test
    public void shouldHurtTheTargetWithAPowerShot() {
        two.place(500);
        ball.place(490, 20);
        ball.powerShot(new Lightning(), one, two, 1);
        assertTrue(ball.collideWithPlayer(two, game));
        assertTrue(two.isShocked());
        assertEquals(two.getMaxLife() - 60, two.getLife());
    }

    @Test
    public void shouldHurtTheTargetOnlyOnce() {
        two.place(500);
        ball.place(490, 20);
        ball.powerShot(new Lightning(), one, two, 1);
        ball.collideWithPlayer(two, game);
        ball.place(490, 20);
        ball.collideWithPlayer(two, game);
        assertEquals(two.getMaxLife() - 60, two.getLife());
    }

    @Test
    public void shouldNotHurtThePlayerWhoShot() {
        one.place(500);
        ball.place(490, 20);
        ball.powerShot(new Lightning(), one, two, 1);
        ball.collideWithPlayer(one, game);
        assertEquals(one.getMaxLife(), one.getLife());
        assertFalse(one.isShocked());
    }

    @Test
    public void shouldKeepSevenFakesAfterSplitAndClearThemOnPlace() {
        ball.place(450, 200);
        ball.split(100);
        assertEquals(7, ball.getFakes().size());
        ball.place(450, 200);
        assertEquals(0, ball.getFakes().size());
    }

    @Test
    public void shouldMoveTheFakesWithTheClock() {
        ball.place(450, 200);
        ball.split(100);
        ball.update(0, 900, 500);
        boolean moved = false;
        for (int i = 0; i < ball.getFakes().size(); i++) {
            if (ball.getFakes().get(i).getX() != 450) {
                moved = true;
            }
        }
        assertTrue(moved);
    }
}