package domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de la clase Player
 * revisa la vida, la patada, el movimiento, el aturdimiento,
 * la barra de poder y los efectos de los tiros de poder
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class PlayerTest {
    private static final double DELTA = 0.01;
    private Player player;

    /**
     * antes de cada prueba crea un jugador nuevo con todas las estadisticas en 3 parado en el 200
     */
    @Before
    public void setUp() {
        player = new Player("Prueba", "Colombia", 3, 3, 3, 3, new BlueAura());
        player.place(200);
    }

    /**
     * deja pasar los pasos del reloj que se indiquen
     * @param frames son los pasos que pasan
     */
    private void pass(int frames) {
        for (int i = 0; i < frames; i++) {
            player.update(25, 855);
        }
    }

    @Test
    public void shouldStartWithFullLife() {
        assertEquals(120, player.getMaxLife());
        assertEquals(player.getMaxLife(), player.getLife());
    }

    @Test
    public void shouldHaveMoreLifeWithMoreSurvival() {
        Player low = new Player("Bajo", "Colombia", 1, 1, 1, 1, new BlueAura());
        Player high = new Player("Alto", "Colombia", 1, 1, 1, 10, new BlueAura());
        assertEquals(100, low.getMaxLife());
        assertEquals(190, high.getMaxLife());
    }

    @Test
    public void shouldLoseLifeWhenDamaged() {
        player.receiveDamage(30);
        assertEquals(90, player.getLife());
    }

    @Test
    public void shouldNotHaveNegativeLife() {
        player.receiveDamage(500);
        assertEquals(0, player.getLife());
    }

    @Test
    public void shouldDoMoreDamageWithMoreKick() {
        Player weak = new Player("Debil", "Colombia", 3, 1, 3, 3, new BlueAura());
        Player strong = new Player("Fuerte", "Colombia", 3, 5, 3, 3, new BlueAura());
        assertEquals(10, weak.getKickDamage());
        assertEquals(12, player.getKickDamage());
        assertEquals(14, strong.getKickDamage());
    }

    @Test
    public void shouldBeStunnedWhenLifeReachesZero() {
        assertFalse(player.isStunned());
        player.receiveDamage(500);
        assertTrue(player.isStunned());
    }

    @Test
    public void shouldNotMoveJumpOrKickWhenStunned() {
        player.receiveDamage(500);
        player.moveLeft(25);
        player.moveRight(855);
        player.jump();
        player.startKick();
        pass(1);
        assertEquals(200, player.getX(), DELTA);
        assertEquals(0, player.getHeight(), DELTA);
        assertFalse(player.isKicking());
    }

    @Test
    public void shouldNotReceiveDamageWhenStunned() {
        player.receiveDamage(500);
        player.receiveDamage(10);
        assertEquals(0, player.getLife());
    }

    @Test
    public void shouldRecoverFullLifeAfterThreeSeconds() {
        player.receiveDamage(500);
        pass(179);
        assertTrue(player.isStunned());
        pass(1);
        assertFalse(player.isStunned());
        assertEquals(player.getMaxLife(), player.getLife());
    }

    @Test
    public void shouldHitOnlyOncePerKick() {
        player.startKick();
        assertTrue(player.canHit());
        player.useKick();
        assertFalse(player.canHit());
        player.startKick();
        assertFalse(player.canHit());
    }

    @Test
    public void shouldStopKickingAfterSomeTime() {
        player.startKick();
        pass(20);
        assertFalse(player.isKicking());
    }

    @Test
    public void shouldNotMovePastTheLeftLimit() {
        for (int i = 0; i < 100; i++) {
            player.moveLeft(25);
        }
        assertEquals(25, player.getX(), DELTA);
    }

    @Test
    public void shouldNotMovePastTheRightLimit() {
        for (int i = 0; i < 200; i++) {
            player.moveRight(855);
        }
        assertEquals(855, player.getX(), DELTA);
    }

    @Test
    public void shouldMoveFasterWithMoreSpeed() {
        Player slow = new Player("Lento", "Colombia", 1, 3, 3, 3, new BlueAura());
        Player fast = new Player("Rapido", "Colombia", 10, 3, 3, 3, new BlueAura());
        slow.place(400);
        fast.place(400);
        slow.moveRight(855);
        fast.moveRight(855);
        assertTrue(fast.getX() > slow.getX());
    }

    @Test
    public void shouldStayInsideTheFieldWhenPushed() {
        player.push(-500, 25, 855);
        assertEquals(25, player.getX(), DELTA);
        player.push(1000, 25, 855);
        assertEquals(855, player.getX(), DELTA);
    }

    @Test
    public void shouldGoUpAndComeBackDownWhenJumping() {
        player.jump();
        pass(1);
        assertTrue(player.getHeight() > 0);
        pass(100);
        assertEquals(0, player.getHeight(), DELTA);
    }

    @Test
    public void shouldJumpHigherWithMoreJump() {
        Player low = new Player("Bajo", "Colombia", 3, 3, 1, 3, new BlueAura());
        Player high = new Player("Alto", "Colombia", 3, 3, 10, 3, new BlueAura());
        low.place(200);
        high.place(200);
        low.jump();
        high.jump();
        low.update(25, 855);
        high.update(25, 855);
        assertTrue(high.getHeight() > low.getHeight());
    }

    @Test
    public void shouldCopyAsAnIndependentPlayer() {
        Player copy = player.copy();
        copy.addGoal();
        copy.receiveDamage(50);
        assertEquals(player.getName(), copy.getName());
        assertEquals(0, player.getGoals());
        assertEquals(player.getMaxLife(), player.getLife());
    }

    @Test
    public void shouldAddPoints() {
        assertEquals(0, player.getPoints());
        player.addPoints(100);
        player.addPoints(25);
        assertEquals(125, player.getPoints());
    }

    @Test
    public void shouldStartWithAnEmptyPowerBar() {
        assertEquals(0, player.getPowerBar());
        assertFalse(player.isPowerArmed());
    }

    @Test
    public void shouldFillThePowerBarInTenSeconds() {
        for (int i = 0; i < 9; i++) {
            player.passSecond();
        }
        assertTrue(player.getPowerBar() < player.getMaxPower());
        player.passSecond();
        assertEquals(player.getMaxPower(), player.getPowerBar());
    }

    @Test
    public void shouldNotArmThePowerWithAnIncompleteBar() {
        player.chargePower(50);
        player.armPower();
        assertFalse(player.isPowerArmed());
        assertEquals(50, player.getPowerBar());
    }

    @Test
    public void shouldEmptyTheBarWhenThePowerIsArmed() {
        player.chargePower(100);
        player.armPower();
        assertTrue(player.isPowerArmed());
        assertEquals(0, player.getPowerBar());
    }

    @Test
    public void shouldKeepChargingWhileThePowerIsArmed() {
        player.chargePower(100);
        player.armPower();
        player.passSecond();
        assertEquals(10, player.getPowerBar());
    }

    @Test
    public void shouldNotArmAnotherPowerWhileHavingOne() {
        player.chargePower(100);
        player.armPower();
        player.chargePower(100);
        player.armPower();
        assertTrue(player.isPowerArmed());
        assertEquals(100, player.getPowerBar());
    }

    @Test
    public void shouldLoseTheAuraWhenUsingThePower() {
        Player rival = new Player("Rival", "Brasil", 3, 3, 3, 3, new Tornado());
        Ball ball = new Ball();
        player.chargePower(100);
        player.armPower();
        player.usePower(rival, ball, 1);
        assertFalse(player.isPowerArmed());
    }

    @Test
    public void shouldNotUseThePowerWithoutTheAura() {
        Player rival = new Player("Rival", "Brasil", 3, 3, 3, 3, new Tornado());
        Ball ball = new Ball();
        ball.place(300, 15);
        player.usePower(rival, ball, 1);
        assertEquals(0, ball.getX() - 300, DELTA);
    }

    @Test
    public void shouldBeConfusedOnlyForTheEffectTime() {
        player.applyConfusion(60);
        assertTrue(player.isConfused());
        pass(59);
        assertTrue(player.isConfused());
        pass(1);
        assertFalse(player.isConfused());
    }

    @Test
    public void shouldBeShockedOnlyForTheEffectTime() {
        player.applyShock(60);
        assertTrue(player.isShocked());
        pass(60);
        assertFalse(player.isShocked());
    }

    @Test
    public void shouldMoveAtHalfSpeedWhenShocked() {
        Player normal = new Player("Normal", "Colombia", 3, 3, 3, 3, new BlueAura());
        normal.place(400);
        player.place(400);
        player.applyShock(60);
        normal.moveRight(855);
        player.moveRight(855);
        assertEquals((normal.getX() - 400) / 2, player.getX() - 400, DELTA);
    }

    @Test
    public void shouldNotJumpWhenShocked() {
        player.applyShock(60);
        player.jump();
        pass(1);
        assertEquals(0, player.getHeight(), DELTA);
    }

    @Test
    public void shouldBeKnockedOutAndNotMove() {
        player.applyKnockOut(180);
        assertTrue(player.isStunned());
        player.moveLeft(25);
        assertEquals(200, player.getX(), DELTA);
    }

    @Test
    public void shouldBePushedTowardsTheDirection() {
        player.applyPush(1, 30);
        pass(10);
        assertTrue(player.getX() > 200);
    }

    @Test
    public void shouldReplaceTheEffectInsteadOfAddingThem() {
        player.applyShock(60);
        player.applyConfusion(60);
        assertFalse(player.isShocked());
        assertTrue(player.isConfused());
        player.applyShock(60);
        assertTrue(player.isShocked());
        assertFalse(player.isConfused());
    }
}