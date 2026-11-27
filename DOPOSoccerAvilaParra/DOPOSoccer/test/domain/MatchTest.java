package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * pruebas del partido, los modos de juego y el balon
 */
public class MatchTest {

    /**
     * @param mode es el modo de juego
     * @return un partido nuevo de 900 x 500
     */
    private DOPOSoccer create(MatchMode mode) {
        Player one = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        Player two = new Player("B", "P", 1, 1, 1, 1, new BlueAura());
        return new DOPOSoccer(one, two, 900, 500, mode);
    }

    @Test
    public void createBuildsTheRightMode() {
        assertTrue(MatchMode.create(true, 3) instanceof GoalsMode);
        assertTrue(MatchMode.create(false, 2) instanceof TimeMode);
    }

    @Test
    public void goalsModeText() {
        DOPOSoccer game = create(new GoalsMode(3));
        assertEquals("Meta: 3 goles", game.getModeText());
        assertTrue(game.isByGoals());
        assertEquals(3, game.getAmount());
    }

    @Test
    public void timeModeStartsInOneMinute() {
        DOPOSoccer game = create(new TimeMode(1));
        assertEquals("Tiempo: 1:00", game.getModeText());
        assertFalse(game.isByGoals());
    }

    @Test
    public void passSecondLowersTheClock() {
        DOPOSoccer game = create(new TimeMode(1));
        game.passSecond();
        assertEquals("Tiempo: 0:59", game.getModeText());
    }

    @Test
    public void timeEndsAndGivesPointsToWinner() {
        DOPOSoccer game = create(new TimeMode(1));
        game.getPlayerOne().addGoal();
        for (int i = 0; i < 60; i++) {
            game.passSecond();
        }
        assertTrue(game.isOver());
        assertSame(game.getPlayerOne(), game.getWinner());
        assertEquals(DOPOSoccer.WIN_POINTS, game.getPlayerOne().getPoints());
        assertEquals(DOPOSoccer.LOSE_POINTS, game.getPlayerTwo().getPoints());
    }

    @Test
    public void tieStartsGoldenGoal() {
        DOPOSoccer game = create(new TimeMode(1));
        for (int i = 0; i < 60; i++) {
            game.passSecond();
        }
        assertFalse(game.isOver());
        assertEquals("GOL DE ORO", game.getModeText());
    }

    @Test
    public void freezeStopsTheClock() {
        DOPOSoccer game = create(new TimeMode(1));
        game.freezeTime(2);
        assertTrue(game.isTimeFrozen());
        game.passSecond();
        game.passSecond();
        assertFalse(game.isTimeFrozen());
        assertEquals("Tiempo: 1:00", game.getModeText());
        game.passSecond();
        assertEquals("Tiempo: 0:59", game.getModeText());
    }

    @Test
    public void frozenTextShowsFrozen() {
        DOPOSoccer game = create(new TimeMode(1));
        game.freezeTime(5);
        assertTrue(game.getModeText().contains("congelado"));
    }

    @Test
    public void removeGoalChangesScoreboard() {
        DOPOSoccer game = create(new GoalsMode(3));
        game.getPlayerOne().addGoal();
        game.removeGoal(game.getPlayerOne());
        assertEquals(0, game.getPlayerOne().getGoals());
        assertTrue(game.update());
        assertFalse(game.update());
    }

    @Test
    public void setMachineWorksAndUnknownThrows() throws DOPOSoccerException {
        DOPOSoccer game = create(new TimeMode(1));
        assertFalse(game.isMachine(2));
        game.setMachine("Defensiva");
        assertTrue(game.isMachine(2));
        assertEquals("Defensiva", game.getMachineName());
    }

    @Test(expected = DOPOSoccerException.class)
    public void setMachineUnknownThrows() throws DOPOSoccerException {
        create(new TimeMode(1)).setMachine("NoExiste");
    }

    @Test(expected = DOPOSoccerException.class)
    public void saveNeedsDatExtension() throws DOPOSoccerException {
        create(new TimeMode(1)).save("partida.txt");
    }

    @Test(expected = DOPOSoccerException.class)
    public void openNeedsDatExtension() throws DOPOSoccerException {
        DOPOSoccer.open("partida.txt");
    }

    @Test
    public void ballStartsNormal() {
        Ball ball = new Ball();
        assertEquals(16, ball.getRadius());
    }

    @Test
    public void changeBodyLastsAndThenRestores() {
        Ball ball = new Ball();
        ball.place(450, 200);
        ball.changeBody(0.25, 0.8, 32, 2);
        assertEquals(32, ball.getRadius());
        ball.update(0, 900, 500);
        assertEquals(32, ball.getRadius());
        ball.update(0, 900, 500);
        assertEquals(16, ball.getRadius());
    }

    @Test
    public void placeRestoresTheBall() {
        Ball ball = new Ball();
        ball.changeBody(0.25, 0.8, 32, 100);
        ball.place(450, 200);
        assertEquals(16, ball.getRadius());
    }

    @Test
    public void lowGravityFallsSlower() {
        Ball normal = new Ball();
        Ball light = new Ball();
        normal.place(450, 300);
        light.place(450, 300);
        light.changeBody(0.05, 0.8, 16, 100);
        for (int i = 0; i < 10; i++) {
            normal.update(0, 900, 500);
            light.update(0, 900, 500);
        }
        assertTrue(light.getHeight() > normal.getHeight());
    }

    @Test
    public void splitCreatesSevenFakesThatDisappear() {
        Ball ball = new Ball();
        ball.place(450, 200);
        ball.split(2);
        assertEquals(7, ball.getFakes().size());
        ball.update(0, 900, 500);
        ball.update(0, 900, 500);
        assertEquals(0, ball.getFakes().size());
    }
}