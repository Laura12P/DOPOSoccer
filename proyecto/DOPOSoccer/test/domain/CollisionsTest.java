package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de los choques entre jugadores, pies y balon
 */
public class CollisionsTest {
    private Collisions collisions;
    private Player a;
    private Player b;
    private Ball ball;

    @Before
    public void setUp() {
        collisions = new Collisions();
        a = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        b = new Player("B", "P", 1, 1, 1, 1, new BlueAura());
        ball = new Ball();
        a.place(100);
        b.place(130);
    }

    private void lift(Player player) {
        player.launch(15);
        for (int i = 0; i < 20 && player.getHeight() < 90; i++) {
            player.update(0, 1000);
        }
    }

    @Test
    public void shouldTouchWhenCloseOnTheGround() {
        assertTrue(collisions.playersTouch(a, b));
    }

    @Test
    public void shouldNotTouchWhenFarApart() {
        b.place(160);
        assertFalse(collisions.playersTouch(a, b));
    }

    @Test
    public void shouldNotTouchWhenOneJumpsOverTheOther() {
        lift(a);
        assertTrue(a.getHeight() >= 90);
        assertFalse(collisions.playersTouch(a, b));
    }

    @Test
    public void shouldTouchTheBallWithTheFootToTheRight() {
        ball.place(135, 15);
        assertTrue(collisions.footTouchesBall(a, ball, 1));
        ball.place(300, 15);
        assertFalse(collisions.footTouchesBall(a, ball, 1));
    }

    @Test
    public void shouldTouchTheBallWithTheFootToTheLeft() {
        ball.place(65, 15);
        assertTrue(collisions.footTouchesBall(a, ball, -1));
        ball.place(135, 15);
        assertFalse(collisions.footTouchesBall(a, ball, -1));
    }

    @Test
    public void shouldNotTouchTheBallThatIsTooHigh() {
        ball.place(135, 200);
        assertFalse(collisions.footTouchesBall(a, ball, 1));
    }

    @Test
    public void shouldReachFurtherWithABiggerBall() {
        ball.place(135, 45);
        assertFalse(collisions.footTouchesBall(a, ball, 1));
        ball.changeBody(0.25, 0.8, 32, 100);
        assertTrue(collisions.footTouchesBall(a, ball, 1));
    }

    @Test
    public void shouldTouchTheRivalWithTheFoot() {
        b.place(140);
        assertTrue(collisions.footTouchesPlayer(a, b, 1));
        b.place(300);
        assertFalse(collisions.footTouchesPlayer(a, b, 1));
    }

    @Test
    public void shouldNotKickARivalThatIsBehind() {
        b.place(60);
        assertFalse(collisions.footTouchesPlayer(a, b, 1));
        assertTrue(collisions.footTouchesPlayer(a, b, -1));
    }

    @Test
    public void shouldNotKickARivalThatIsInTheAir() {
        b.place(140);
        lift(b);
        assertFalse(collisions.footTouchesPlayer(a, b, 1));
    }
}
