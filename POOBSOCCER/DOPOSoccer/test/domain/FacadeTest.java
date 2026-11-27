package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas extra de la fachada DOPOSoccer y de las maquinas jugando el partido
 */
public class FacadeTest {
    private Player one;
    private Player two;
    private DOPOSoccer game;

    @Before
    public void setUp() {
        one = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        two = new Player("B", "P", 1, 1, 1, 1, new Tornado());
        game = new DOPOSoccer(one, two, 900, 500);
    }

    @Test
    public void shouldStartEverythingInPlace() {
        assertEquals(225, one.getX(), 0.001);
        assertEquals(675, two.getX(), 0.001);
        assertEquals(450, game.getBall().getX(), 0.001);
        assertEquals(200, game.getBall().getHeight(), 0.001);
        assertEquals(900, game.getWidth());
        assertEquals(500, game.getHeight());
    }

    @Test
    public void shouldHaveNoMachineNorSeriesAtTheStart() {
        assertFalse(game.isMachine(1));
        assertFalse(game.isMachine(2));
        assertNull(game.getMachineName());
        assertNull(game.getSeries());
    }

    @Test
    public void shouldKeepTheSeries() throws DOPOSoccerException {
        Series series = new Series("A", null, "B", null, false, 3);
        game.setSeries(series);
        assertSame(series, game.getSeries());
    }

    @Test
    public void shouldStopBothPlayersAtTheWalls() {
        for (int i = 0; i < 500; i++) {
            game.moveLeft(1);
            game.moveRight(2);
        }
        assertEquals(25, one.getX(), 0.001);
        assertEquals(875, two.getX(), 0.001);
    }

    @Test
    public void shouldJumpTheIndicatedPlayer() {
        game.jump(2);
        two.update(25, 875);
        assertTrue(two.getHeight() > 0);
        assertEquals(0, one.getHeight(), 0.001);
    }

    @Test
    public void shouldKickTheIndicatedPlayer() {
        game.kick(1);
        assertTrue(one.isKicking());
        assertFalse(two.isKicking());
    }

    @Test
    public void shouldNameTheLoserAndTheWinner() {
        one.addGoal();
        assertSame(one, game.getWinner());
        assertSame(two, game.getLoser());
    }

    @Test
    public void shouldAssignEveryMachineByName() throws DOPOSoccerException {
        for (int i = 0; i < Machines.getNames().size(); i++) {
            String name = Machines.getNames().get(i);
            DOPOSoccer other = new DOPOSoccer(one, two, 900, 500);
            other.setMachine(name);
            assertTrue(other.isMachine(2));
            assertEquals(name, other.getMachineName());
        }
    }

    @Test
    public void shouldPlayAFullMatchWithEveryMachineWithoutBreaking() throws DOPOSoccerException {
        for (int i = 0; i < Machines.getNames().size(); i++) {
            Player a = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
            Player b = new Player("B", "P", 1, 1, 1, 1, new Lightning());
            DOPOSoccer other = new DOPOSoccer(a, b, 900, 500, new TimeMode(5));
            other.setMachine(Machines.getNames().get(i));
            for (int step = 0; step < 1200; step++) {
                other.update();
                if (step % 60 == 0) {
                    other.passSecond();
                }
                assertTrue(b.getX() >= 25 && b.getX() <= 875);
                assertTrue(a.getX() >= 25 && a.getX() <= 875);
                assertTrue(b.getHeight() >= 0);
            }
        }
    }
}