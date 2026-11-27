package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * pruebas de la serie, el perfil y el nombre de los archivos
 */
public class SeriesProfileTest {

    @Test(expected = DOPOSoccerException.class)
    public void bestOfMustBeOneThreeOrFive() throws DOPOSoccerException {
        new Series("A", null, "B", null, false, 2);
    }

    @Test(expected = DOPOSoccerException.class)
    public void byPointsNeedsPositiveTarget() throws DOPOSoccerException {
        new Series("A", null, "B", null, true, 0);
    }

    @Test
    public void bestOfThreeEndsWithTwoWins() throws DOPOSoccerException {
        Series series = new Series("A", null, "B", null, false, 3);
        series.recordMatch(1, 100, 25);
        assertFalse(series.isOver());
        series.recordMatch(1, 100, 25);
        assertTrue(series.isOver());
        assertEquals(1, series.getWinner());
        assertEquals(200, series.getPoints(1));
        assertEquals(50, series.getPoints(2));
        assertEquals(2, series.getWins(1));
    }

    @Test
    public void recordMatchUsesGivenPoints() throws DOPOSoccerException {
        Series series = new Series("A", null, "B", null, false, 5);
        series.recordMatch(2, 10, 5);
        assertEquals(10, series.getPoints(2));
        assertEquals(5, series.getPoints(1));
    }

    @Test
    public void byPointsEndsWhenTargetIsReached() throws DOPOSoccerException {
        Series series = new Series("A", null, "B", null, true, 150);
        series.recordMatch(2, 100, 25);
        assertFalse(series.isOver());
        series.recordMatch(2, 100, 25);
        assertTrue(series.isOver());
        assertEquals(2, series.getWinner());
    }

    @Test
    public void rematchCountsAfterTheSeriesEnds() throws DOPOSoccerException {
        Series series = new Series("A", null, "B", null, false, 1);
        series.recordMatch(1, 100, 25);
        series.recordRematch(2, 100, 25);
        assertEquals(1, series.getRematches());
        assertEquals(2, series.getMatches());
        assertEquals(1, series.getWinner());
    }

    @Test
    public void seriesGivesPointsToProfiles() throws DOPOSoccerException {
        Profile one = new Profile("Ana");
        Series series = new Series("Ana", one, "B", null, false, 3);
        series.recordMatch(1, 100, 25);
        assertEquals(100, one.getPoints());
    }

    @Test
    public void newProfileStartsInLevelOne() {
        Profile profile = new Profile("Ana");
        assertEquals(0, profile.getPoints());
        for (int i = 0; i < Profile.STATS.length; i++) {
            assertEquals(Profile.MIN_LEVEL, profile.getLevel(i));
        }
    }

    @Test
    public void upgradeCostsAndDoubles() throws DOPOSoccerException {
        Profile profile = new Profile("Ana");
        profile.addPoints(1500);
        assertEquals(500, profile.getUpgradeCost());
        profile.upgrade(Profile.SPEED);
        assertEquals(2, profile.getLevel(Profile.SPEED));
        assertEquals(1000, profile.getPoints());
        assertEquals(1000, profile.getUpgradeCost());
        profile.upgrade(Profile.KICK);
        assertEquals(0, profile.getPoints());
    }

    @Test(expected = DOPOSoccerException.class)
    public void upgradeWithoutPointsThrows() throws DOPOSoccerException {
        new Profile("Ana").upgrade(Profile.SPEED);
    }

    @Test(expected = DOPOSoccerException.class)
    public void upgradeUnknownStatThrows() throws DOPOSoccerException {
        Profile profile = new Profile("Ana");
        profile.addPoints(500);
        profile.upgrade(9);
    }

    @Test
    public void applyToChangesPlayerStats() throws DOPOSoccerException {
        Profile profile = new Profile("Ana");
        profile.addPoints(500);
        profile.upgrade(Profile.JUMP);
        Player player = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        profile.applyTo(player);
        assertEquals(2, player.getJump());
        assertEquals(1, player.getSpeed());
    }

    @Test
    public void fileNameKeepsOnlyLettersAndDigits() {
        assertEquals("perfil_anamaria1.dat", Progress.fileName("Ana Maria!1"));
        assertEquals(Progress.fileName("ANA"), Progress.fileName("ana"));
    }

    @Test
    public void fileNameWithoutLettersIsNull() {
        assertNull(Progress.fileName("!!! "));
    }

    @Test(expected = DOPOSoccerException.class)
    public void openMissingProfileThrows() throws DOPOSoccerException {
        Progress.open("perfil_noexiste_xyz.dat");
    }
}