package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de cada poder (datos, daño, efecto) y de cada efecto
 */
public class PowerEffectTest {
    private Player user;
    private Player rival;
    private DOPOSoccer game;

    @Before
    public void setUp() {
        user = new Player("U", "P", 1, 1, 1, 1, new BlueAura());
        rival = new Player("R", "P", 1, 1, 1, 1, new BlueAura());
        game = new DOPOSoccer(new Player("A", "P", 1, 1, 1, 1, new BlueAura()),
                new Player("B", "P", 1, 1, 1, 1, new BlueAura()), 900, 500);
        rival.place(400);
    }

    @Test
    public void shouldHaveTheRightDataInEachPower() {
        assertEquals("Aura Azul", new BlueAura().getName());
        assertEquals("Rayo", new Lightning().getName());
        assertEquals("Tornado", new Tornado().getName());
        assertEquals("Confusion", new Confusion().getName());
        assertEquals("Ilusion", new Illusion().getName());
        assertEquals(16, new BlueAura().getSpeed(), 0.001);
        assertEquals(14, new Lightning().getSpeed(), 0.001);
        assertEquals(12, new Tornado().getSpeed(), 0.001);
        assertEquals(90, new BlueAura().getFrames());
        assertEquals(180, new Tornado().getFrames());
        assertEquals(480, new Confusion().getFrames());
        assertEquals(150, new Illusion().getFrames());
    }

    @Test
    public void shouldHaveAnAuraColorInEachPower() {
        assertArrayEquals(new int[] {0, 120, 255, 100}, new BlueAura().getAura());
        assertArrayEquals(new int[] {255, 220, 0, 110}, new Lightning().getAura());
        assertArrayEquals(new int[] {150, 150, 150, 120}, new Tornado().getAura());
        assertArrayEquals(new int[] {170, 60, 220, 100}, new Illusion().getAura());
        assertArrayEquals(new int[] {255, 80, 160, 110}, new Confusion().getAura());
    }

    @Test
    public void shouldDoTheBaseDamageOfEachPower() {
        new BlueAura().hit(user, rival, 1, game);
        assertEquals(rival.getMaxLife() - 40, rival.getLife());
    }

    @Test
    public void shouldMultiplyTheDamageByTheKickOfTheUser() {
        Player strong = new Player("S", "P", 1, 3, 1, 1, new BlueAura());
        new BlueAura().hit(strong, rival, 1, game);
        assertEquals(rival.getMaxLife() - 48, rival.getLife());
    }

    @Test
    public void shouldPushWithTheBlueAura() {
        new BlueAura().hit(user, rival, 1, game);
        rival.update(0, 1000);
        assertEquals(410, rival.getX(), 0.001);
    }

    @Test
    public void shouldPushToTheLeftWhenTheShotGoesLeft() {
        new BlueAura().hit(user, rival, -1, game);
        rival.update(0, 1000);
        assertEquals(390, rival.getX(), 0.001);
    }

    @Test
    public void shouldShockWithTheLightning() {
        new Lightning().hit(user, rival, 1, game);
        assertTrue(rival.isShocked());
        assertEquals(rival.getMaxLife() - 60, rival.getLife());
    }

    @Test
    public void shouldKnockOutWithTheTornado() {
        new Tornado().hit(user, rival, 1, game);
        assertTrue(rival.isStunned());
        rival.update(0, 1000);
        assertTrue(rival.getHeight() > 0);
    }

    @Test
    public void shouldConfuseWithTheConfusion() {
        new Confusion().hit(user, rival, 1, game);
        assertTrue(rival.isConfused());
    }

    @Test
    public void shouldSplitTheBallWithTheIllusion() {
        new Illusion().hit(user, rival, 1, game);
        assertEquals(7, game.getBall().getFakes().size());
    }

    @Test
    public void shouldDoNothingByDefaultInAnEffect() {
        Effect effect = new Effect() { };
        assertEquals(1, effect.getSpeedFactor(), 0.001);
        assertTrue(effect.canJump());
        assertFalse(effect.isStunned());
        assertFalse(effect.invertsControls());
        effect.start(rival);
        effect.step(rival, 0, 1000);
        assertEquals(400, rival.getX(), 0.001);
    }

    @Test
    public void shouldHaveTheRightValuesInEachEffect() {
        assertEquals(0.5, new ShockEffect().getSpeedFactor(), 0.001);
        assertFalse(new ShockEffect().canJump());
        assertTrue(new ConfusionEffect().invertsControls());
        assertTrue(new KnockOutEffect().isStunned());
        assertFalse(new PushEffect(1).isStunned());
        assertTrue(new PushEffect(1).canJump());
    }

    @Test
    public void shouldPushOneStepAtATime() {
        new PushEffect(-1).step(rival, 0, 1000);
        assertEquals(390, rival.getX(), 0.001);
        new PushEffect(1).step(rival, 0, 1000);
        assertEquals(400, rival.getX(), 0.001);
    }

    @Test
    public void shouldLaunchThePlayerWhenTheKnockOutStarts() {
        new KnockOutEffect().start(rival);
        rival.update(0, 1000);
        assertEquals(15, rival.getHeight(), 0.001);
    }
}