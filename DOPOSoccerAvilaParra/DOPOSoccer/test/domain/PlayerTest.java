package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas del jugador: stats, movimiento, salto, patada, vida y poder
 */
public class PlayerTest {
    private Player player;

    private Player create(int speed, int kick, int jump, int survival) {
        return new Player("Uno", "Pais", speed, kick, jump, survival, new BlueAura());
    }

    @Before
    public void setUp() {
        player = create(1, 1, 1, 1);
        player.place(400);
    }

    @Test
    public void shouldKeepTheConstructorData() {
        assertEquals("Uno", player.getName());
        assertEquals("Pais", player.getCountry());
        assertEquals(1, player.getSpeed());
        assertEquals(1, player.getKick());
        assertEquals(1, player.getJump());
        assertEquals(1, player.getSurvival());
        assertEquals("Aura Azul", player.getPower().getName());
    }

    @Test
    public void shouldGiveTenPercentMoreLifePerSurvivalPoint() {
        assertEquals(100, create(1, 1, 1, 1).getMaxLife());
        assertEquals(120, create(1, 1, 1, 3).getMaxLife());
        assertEquals(190, create(1, 1, 1, 10).getMaxLife());
        assertEquals(190, create(1, 1, 1, 10).getLife());
    }

    @Test
    public void shouldGiveTenPercentMoreKickDamagePerKickPoint() {
        assertEquals(10, create(1, 1, 1, 1).getKickDamage());
        assertEquals(12, create(1, 3, 1, 1).getKickDamage());
        assertEquals(19, create(1, 10, 1, 1).getKickDamage());
        assertEquals(1.2, create(1, 3, 1, 1).getKickMultiplier(), 0.001);
    }

    @Test
    public void shouldMoveFasterWithMoreSpeed() {
        Player fast = create(3, 1, 1, 1);
        fast.place(400);
        fast.moveRight(1000);
        assertEquals(406, fast.getX(), 0.001);
        fast.moveLeft(0);
        fast.moveLeft(0);
        assertEquals(394, fast.getX(), 0.001);
    }

    @Test
    public void shouldStopAtTheLimits() {
        for (int i = 0; i < 500; i++) {
            player.moveLeft(25);
        }
        assertEquals(25, player.getX(), 0.001);
        for (int i = 0; i < 500; i++) {
            player.moveRight(855);
        }
        assertEquals(855, player.getX(), 0.001);
    }

    @Test
    public void shouldPushAndStayInsideTheField() {
        player.push(30, 25, 855);
        assertEquals(430, player.getX(), 0.001);
        player.push(5000, 25, 855);
        assertEquals(855, player.getX(), 0.001);
        player.push(-5000, 25, 855);
        assertEquals(25, player.getX(), 0.001);
    }

    @Test
    public void shouldJumpAndComeBackDown() {
        player.jump();
        player.update(25, 875);
        assertEquals(12, player.getHeight(), 0.001);
        for (int i = 0; i < 100; i++) {
            player.update(25, 875);
        }
        assertEquals(0, player.getHeight(), 0.001);
    }

    @Test
    public void shouldNotJumpTwiceInTheAir() {
        player.jump();
        player.update(25, 875);
        player.jump();
        player.update(25, 875);
        assertEquals(23, player.getHeight(), 0.001);
    }

    @Test
    public void shouldJumpHigherWithMoreJump() {
        Player high = create(1, 1, 10, 1);
        high.place(400);
        high.jump();
        high.update(25, 875);
        player.jump();
        player.update(25, 875);
        assertTrue(high.getHeight() > player.getHeight());
    }

    @Test
    public void shouldKickOnlyOnceAndForALimitedTime() {
        player.startKick();
        assertTrue(player.isKicking());
        assertEquals(Player.KICK_TIME, player.getKickTime());
        assertTrue(player.canHit());
        player.useKick();
        assertFalse(player.canHit());
        assertTrue(player.isKicking());
        for (int i = 0; i < Player.KICK_TIME; i++) {
            player.update(25, 875);
        }
        assertFalse(player.isKicking());
    }

    @Test
    public void shouldNotRestartAKickInProgress() {
        player.startKick();
        player.update(25, 875);
        player.startKick();
        assertEquals(Player.KICK_TIME - 1, player.getKickTime());
    }

    @Test
    public void shouldLoseLifeAndGetStunnedAtZero() {
        player.receiveDamage(30);
        assertEquals(70, player.getLife());
        assertFalse(player.isStunned());
        player.receiveDamage(500);
        assertEquals(0, player.getLife());
        assertTrue(player.isStunned());
    }

    @Test
    public void shouldNotTakeDamageWhileStunned() {
        player.receiveDamage(100);
        player.receiveDamage(10);
        assertEquals(0, player.getLife());
    }

    @Test
    public void shouldNotMoveJumpKickOrArmPowerWhileStunned() {
        player.receiveDamage(100);
        player.moveRight(1000);
        player.moveLeft(0);
        assertEquals(400, player.getX(), 0.001);
        player.jump();
        player.update(25, 875);
        assertEquals(0, player.getHeight(), 0.001);
        player.startKick();
        assertFalse(player.isKicking());
        player.chargePower(100);
        player.armPower();
        assertFalse(player.isPowerArmed());
        assertEquals(100, player.getPowerBar());
    }

    @Test
    public void shouldRecoverFullLifeWhenTheStunEnds() {
        player.receiveDamage(100);
        for (int i = 0; i < 179; i++) {
            player.update(25, 875);
        }
        assertTrue(player.isStunned());
        player.update(25, 875);
        assertFalse(player.isStunned());
        assertEquals(player.getMaxLife(), player.getLife());
    }

    @Test
    public void shouldHaveNoEffectAtTheStart() {
        assertFalse(player.isShocked());
        assertFalse(player.isConfused());
        assertFalse(player.isStunned());
    }

    @Test
    public void shouldResetWhenPlaced() {
        player.jump();
        player.update(25, 875);
        player.startKick();
        player.place(100);
        assertEquals(100, player.getX(), 0.001);
        assertEquals(0, player.getHeight(), 0.001);
        assertFalse(player.isKicking());
    }

    @Test
    public void shouldChangeStatsAndRefillLife() {
        player.receiveDamage(50);
        player.setStats(5, 5, 5, 5);
        assertEquals(5, player.getSpeed());
        assertEquals(5, player.getKick());
        assertEquals(5, player.getJump());
        assertEquals(5, player.getSurvival());
        assertEquals(140, player.getLife());
    }

    @Test
    public void shouldCopyWithoutGoalsOrPoints() {
        player.addGoal();
        player.addPoints(100);
        Player copy = player.copy();
        assertNotSame(player, copy);
        assertEquals(player.getName(), copy.getName());
        assertEquals(player.getCountry(), copy.getCountry());
        assertSame(player.getPower(), copy.getPower());
        assertEquals(0, copy.getGoals());
        assertEquals(0, copy.getPoints());
    }

    @Test
    public void shouldCountGoalsAndPoints() {
        player.addGoal();
        player.addGoal();
        player.addPoints(100);
        player.addPoints(25);
        assertEquals(2, player.getGoals());
        assertEquals(125, player.getPoints());
    }

    @Test
    public void shouldChargeThePowerBarWithoutPassingTheMaximum() {
        player.passSecond();
        assertEquals(10, player.getPowerBar());
        player.chargePower(500);
        assertEquals(player.getMaxPower(), player.getPowerBar());
    }

    @Test
    public void shouldFillTheBarInTenSeconds() {
        for (int i = 0; i < 10; i++) {
            player.passSecond();
        }
        assertEquals(100, player.getPowerBar());
    }

    @Test
    public void shouldNotArmTheSamePowerTwice() {
        player.chargePower(100);
        player.armPower();
        player.chargePower(100);
        player.armPower();
        assertEquals(100, player.getPowerBar());
    }

    @Test
    public void shouldShootOnlyWhenArmed() {
        Player rival = create(1, 1, 1, 1);
        Ball ball = new Ball();
        ball.place(450, 200);
        player.usePower(rival, ball, 1);
        ball.update(0, 900, 500);
        assertEquals(450, ball.getX(), 0.001);
        player.chargePower(100);
        player.armPower();
        player.usePower(rival, ball, 1);
        assertFalse(player.isPowerArmed());
        ball.update(0, 900, 500);
        assertEquals(466, ball.getX(), 0.001);
    }
}