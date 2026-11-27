package domain;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas del perfil de un jugador
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ProfileTest {
    private Profile profile;
    @Before
    public void setUp() {
        profile = new Profile("Jero");
    }

    @Test
    public void shouldStartWithoutPointsAndEverythingInOne() {
        assertEquals("Jero", profile.getName());
        assertEquals(0, profile.getPoints());
        for (int i = 0; i < Profile.STATS.length; i++) {
            assertEquals(Profile.MIN_LEVEL, profile.getLevel(i));
        }
    }

    @Test
    public void shouldAddPoints() {
        profile.addPoints(100);
        profile.addPoints(25);
        assertEquals(125, profile.getPoints());
    }

    @Test
    public void shouldDoubleThePriceAfterEachUpgrade() throws DOPOSoccerException {
        profile.addPoints(3500);
        assertEquals(500, profile.getUpgradeCost());
        profile.upgrade(Profile.SPEED);
        assertEquals(1000, profile.getUpgradeCost());
        profile.upgrade(Profile.KICK);
        assertEquals(2000, profile.getUpgradeCost());
        profile.upgrade(Profile.SPEED);
        assertEquals(4000, profile.getUpgradeCost());
        assertEquals(0, profile.getPoints());
        assertEquals(3, profile.getLevel(Profile.SPEED));
        assertEquals(2, profile.getLevel(Profile.KICK));
    }

    @Test
    public void shouldNotUpgradeWithoutEnoughPoints() {
        profile.addPoints(499);
        try {
            profile.upgrade(Profile.JUMP);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
        assertEquals(1, profile.getLevel(Profile.JUMP));
        assertEquals(499, profile.getPoints());
    }

    @Test
    public void shouldNotGoPastTheMaximumLevel() throws DOPOSoccerException {
        profile.addPoints(1000000);
        for (int i = 1; i < Profile.MAX_LEVEL; i++) {
            profile.upgrade(Profile.SURVIVAL);
        }
        assertEquals(Profile.MAX_LEVEL, profile.getLevel(Profile.SURVIVAL));
        try {
            profile.upgrade(Profile.SURVIVAL);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.MAX_LEVEL, e.getMessage());
        }
    }

    @Test
    public void shouldNotUpgradeAStatThatDoesNotExist() {
        profile.addPoints(500);
        try {
            profile.upgrade(7);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_STAT, e.getMessage());
        }
        try {
            profile.upgrade(-1);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_STAT, e.getMessage());
        }
    }

    @Test
    public void shouldGiveItsStatsToThePlayer() throws DOPOSoccerException {
        profile.addPoints(1500);
        profile.upgrade(Profile.SURVIVAL);
        profile.upgrade(Profile.KICK);
        Player player = new Player("Son", "Corea Del Sur", 1, 1, 1, 1, new BlueAura());
        profile.applyTo(player);
        assertEquals(1, player.getSpeed());
        assertEquals(2, player.getKick());
        assertEquals(1, player.getJump());
        assertEquals(2, player.getSurvival());
        assertEquals(110, player.getMaxLife());
        assertEquals(110, player.getLife());
        assertEquals(11, player.getKickDamage());
    }
}