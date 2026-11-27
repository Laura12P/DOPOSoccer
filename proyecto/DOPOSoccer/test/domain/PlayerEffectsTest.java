package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * pruebas de los efectos sobre el jugador
 */
public class PlayerEffectsTest {

    /**
     * @return un jugador con stats en 1, parado en x = 400
     */
    private Player create() {
        Player player = new Player("Prueba", "Pais", 1, 1, 1, 1, new BlueAura());
        player.place(400);
        return player;
    }

    @Test
    public void normalSpeedIsFive() {
        Player player = create();
        player.moveRight(1000);
        assertEquals(405, player.getX(), 0.001);
    }

    @Test
    public void shockHalvesSpeed() {
        Player player = create();
        player.applyEffect(new ShockEffect(), 60);
        player.moveRight(1000);
        assertEquals(402.5, player.getX(), 0.001);
        assertTrue(player.isShocked());
    }

    @Test
    public void shockBlocksJump() {
        Player player = create();
        player.applyEffect(new ShockEffect(), 60);
        player.jump();
        player.update(0, 1000);
        assertEquals(0, player.getHeight(), 0.001);
    }

    @Test
    public void jumpWorksWithoutEffect() {
        Player player = create();
        player.jump();
        player.update(0, 1000);
        assertTrue(player.getHeight() > 0);
    }

    @Test
    public void confusionInvertsControls() {
        Player player = create();
        assertFalse(player.isConfused());
        player.applyEffect(new ConfusionEffect(), 60);
        assertTrue(player.isConfused());
    }

    @Test
    public void pushMovesPlayerEachStep() {
        Player player = create();
        player.applyEffect(new PushEffect(1), 5);
        player.update(0, 1000);
        assertEquals(410, player.getX(), 0.001);
    }

    @Test
    public void pushStopsAtTheWall() {
        Player player = create();
        player.applyEffect(new PushEffect(-1), 100);
        for (int i = 0; i < 100; i++) {
            player.update(25, 875);
        }
        assertEquals(25, player.getX(), 0.001);
    }

    @Test
    public void knockOutStunsAndLaunches() {
        Player player = create();
        player.applyEffect(new KnockOutEffect(), 60);
        assertTrue(player.isStunned());
        player.update(0, 1000);
        assertTrue(player.getHeight() > 0);
    }

    @Test
    public void effectEndsWhenTimeEnds() {
        Player player = create();
        player.applyEffect(new ConfusionEffect(), 2);
        player.update(0, 1000);
        assertTrue(player.isConfused());
        player.update(0, 1000);
        assertFalse(player.isConfused());
    }

    @Test
    public void newEffectReplacesTheOldOne() {
        Player player = create();
        player.applyEffect(new ShockEffect(), 60);
        player.applyEffect(new ConfusionEffect(), 60);
        assertFalse(player.isShocked());
        assertTrue(player.isConfused());
    }

    @Test
    public void removeGoalNeverGoesBelowZero() {
        Player player = create();
        player.removeGoal();
        assertEquals(0, player.getGoals());
        player.addGoal();
        player.removeGoal();
        assertEquals(0, player.getGoals());
    }

    @Test
    public void losePointsReducesPoints() {
        Player player = create();
        player.addPoints(100);
        player.losePoints(25);
        assertEquals(75, player.getPoints());
        player.losePoints(500);
        assertEquals(0, player.getPoints());
    }

    @Test
    public void armPowerNeedsFullBar() {
        Player player = create();
        player.armPower();
        assertFalse(player.isPowerArmed());
        player.chargePower(player.getMaxPower());
        player.armPower();
        assertTrue(player.isPowerArmed());
        assertEquals(0, player.getPowerBar());
    }
}