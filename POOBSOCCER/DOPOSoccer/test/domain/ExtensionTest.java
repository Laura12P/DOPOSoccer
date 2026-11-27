package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de que se pueden agregar poderes, efectos y modos nuevos sin cambiar lo que ya existe
 */
public class ExtensionTest {
    private Player user;
    private Player rival;
    private DOPOSoccer game;

    private static class FreezePower extends Power {
        FreezePower() {
            super("Tiempo", 10, 14, 3, false, new int[] {0, 220, 220, 100});
        }
        protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
            game.freezeTime(5);
        }
    }

    private static class RemoveGoalPower extends Power {
        RemoveGoalPower() {
            super("Quitagol", 10, 14, 3, false, new int[] {255, 0, 0, 100});
        }
        protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
            game.removeGoal(rival);
        }
    }

    private static class PenaltyPower extends Power {
        PenaltyPower() {
            super("Multa", 10, 14, 3, false, new int[] {255, 0, 0, 100});
        }
        protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
            rival.losePoints(25);
        }
    }

    private static class HeavyBallPower extends Power {
        HeavyBallPower() {
            super("Pesado", 10, 14, 3, false, new int[] {0, 0, 0, 100});
        }
        protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
            game.getBall().changeBody(0.6, 0.2, 32, getFrames());
        }
    }

    private static class SelfChargePower extends Power {
        SelfChargePower() {
            super("Recarga", 10, 14, 3, false, new int[] {0, 255, 0, 100});
        }
        protected void applyEffect(Player user, Player rival, int direction, DOPOSoccer game) {
            user.chargePower(50);
        }
    }

    private static class FastEffect extends Effect {
        public double getSpeedFactor() {
            return 2;
        }
    }

    private static class DiffTwoMode extends MatchMode {
        private int target;
        DiffTwoMode(int target) {
            this.target = target;
        }
        public void goalScored(DOPOSoccer game) {
            int one = game.getPlayerOne().getGoals();
            int two = game.getPlayerTwo().getGoals();
            if (Math.max(one, two) >= target && Math.abs(one - two) >= 2) {
                game.finish();
            }
        }
        public String getText(DOPOSoccer game) {
            return "Diferencia de 2";
        }
        public boolean isByGoals() {
            return true;
        }
        public int getAmount() {
            return target;
        }
    }

    @Before
    public void setUp() {
        user = new Player("U", "P", 1, 1, 1, 1, new BlueAura());
        rival = new Player("R", "P", 1, 1, 1, 1, new BlueAura());
        game = new DOPOSoccer(new Player("A", "P", 1, 1, 1, 1, new BlueAura()),
                new Player("B", "P", 1, 1, 1, 1, new BlueAura()), 900, 500, new TimeMode(1));
        rival.place(400);
    }

    private void scoreForOne() {
        game.getBall().place(870, 50);
        game.update();
    }

    private void scoreForTwo() {
        game.getBall().place(30, 50);
        game.update();
    }

    @Test
    public void shouldFreezeTheTimeWithANewPower() {
        new FreezePower().hit(user, rival, 1, game);
        assertTrue(game.isTimeFrozen());
        for (int i = 0; i < 5; i++) {
            game.passSecond();
        }
        assertEquals("Tiempo: 1:00", game.getModeText());
        game.passSecond();
        assertEquals("Tiempo: 0:59", game.getModeText());
    }

    @Test
    public void shouldRemoveAGoalWithANewPower() {
        rival.addGoal();
        new RemoveGoalPower().hit(user, rival, 1, game);
        assertEquals(0, rival.getGoals());
        assertTrue(game.update());
    }

    @Test
    public void shouldRemovePointsWithANewPower() {
        rival.addPoints(100);
        new PenaltyPower().hit(user, rival, 1, game);
        assertEquals(75, rival.getPoints());
    }

    @Test
    public void shouldChangeTheBallWithANewPower() {
        new HeavyBallPower().hit(user, rival, 1, game);
        assertEquals(32, game.getBall().getRadius());
        for (int i = 0; i < 180; i++) {
            game.getBall().update(0, 900, 500);
        }
        assertEquals(16, game.getBall().getRadius());
    }

    @Test
    public void shouldAffectTheUserWithANewPower() {
        new SelfChargePower().hit(user, rival, 1, game);
        assertEquals(50, user.getPowerBar());
    }

    @Test
    public void shouldUseANewEffect() {
        rival.applyEffect(new FastEffect(), 60);
        rival.moveRight(1000);
        assertEquals(410, rival.getX(), 0.001);
    }

    @Test
    public void shouldPlayWithANewMode() {
        DOPOSoccer other = new DOPOSoccer(new Player("A", "P", 1, 1, 1, 1, new BlueAura()),
                new Player("B", "P", 1, 1, 1, 1, new BlueAura()), 900, 500, new DiffTwoMode(2));
        game = other;
        assertEquals("Diferencia de 2", game.getModeText());
        scoreForOne();
        assertFalse(game.isOver());
        scoreForTwo();
        scoreForOne();
        scoreForOne();
        assertEquals(3, game.getPlayerOne().getGoals());
        assertEquals(1, game.getPlayerTwo().getGoals());
        assertTrue(game.isOver());
    }

    @Test
    public void shouldNotEndWithOnlyOneGoalOfDifference() {
        game = new DOPOSoccer(new Player("A", "P", 1, 1, 1, 1, new BlueAura()),
                new Player("B", "P", 1, 1, 1, 1, new BlueAura()), 900, 500, new DiffTwoMode(2));
        scoreForOne();
        scoreForOne();
        assertTrue(game.isOver());
        game = new DOPOSoccer(new Player("A", "P", 1, 1, 1, 1, new BlueAura()),
                new Player("B", "P", 1, 1, 1, 1, new BlueAura()), 900, 500, new DiffTwoMode(2));
        scoreForOne();
        scoreForTwo();
        scoreForOne();
        assertFalse(game.isOver());
    }
}