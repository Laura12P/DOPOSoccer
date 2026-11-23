package domain;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de la serie de partidos
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class SeriesTest {
    private Profile jero;
    private Profile laura;

    @Before
    public void setUp() {
        jero = new Profile("Jero");
        laura = new Profile("Laura");
    }

    @Test
    public void shouldStartWithoutMatches() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 3);
        assertEquals(0, series.getMatches());
        assertEquals(0, series.getWins(1));
        assertEquals(0, series.getPoints(2));
        assertFalse(series.isOver());
        assertEquals(0, series.getWinner());
        assertFalse(series.isByPoints());
        assertEquals(3, series.getTarget());
    }

    @Test
    public void shouldEndAfterOneMatchInBestOfOne() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 1);
        series.recordMatch(2);
        assertTrue(series.isOver());
        assertEquals(2, series.getWinner());
    }

    @Test
    public void shouldNeedTwoWinsInBestOfThree() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 3);
        series.recordMatch(1);
        assertFalse(series.isOver());
        series.recordMatch(2);
        assertFalse(series.isOver());
        series.recordMatch(1);
        assertTrue(series.isOver());
        assertEquals(1, series.getWinner());
        assertEquals(2, series.getWins(1));
        assertEquals(1, series.getWins(2));
        assertEquals(3, series.getMatches());
    }

    @Test
    public void shouldNeedThreeWinsInBestOfFive() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 5);
        series.recordMatch(2);
        series.recordMatch(2);
        assertFalse(series.isOver());
        series.recordMatch(2);
        assertTrue(series.isOver());
        assertEquals(2, series.getWinner());
    }

    @Test
    public void shouldGiveOneHundredToTheWinnerAndTwentyFiveToTheLoser() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 5);
        series.recordMatch(1);
        series.recordMatch(2);
        assertEquals(125, series.getPoints(1));
        assertEquals(125, series.getPoints(2));
        series.recordMatch(1);
        assertEquals(225, series.getPoints(1));
        assertEquals(225, jero.getPoints());
        assertEquals(150, laura.getPoints());
    }

    @Test
    public void shouldBeWonByTheFirstToReachThePoints() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, true, 200);
        series.recordMatch(1);
        assertFalse(series.isOver());
        series.recordMatch(1);
        assertTrue(series.isOver());
        assertEquals(1, series.getWinner());
        assertEquals(200, series.getPoints(1));
        assertTrue(series.isByPoints());
    }

    @Test
    public void shouldBeWonByTheLoserOfTheMatchIfOnlyHeReachesThePoints() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, true, 325);
        series.recordMatch(2);
        series.recordMatch(2);
        series.recordMatch(2);
        assertFalse(series.isOver());
        assertEquals(300, series.getPoints(2));
        series.recordMatch(1);
        assertEquals(175, series.getPoints(1));
        assertEquals(325, series.getPoints(2));
        assertTrue(series.isOver());
        assertEquals(2, series.getWinner());
    }

    @Test
    public void shouldGiveTheSeriesToTheMatchWinnerIfBothReachThePoints() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, true, 25);
        series.recordMatch(1);
        assertEquals(100, series.getPoints(1));
        assertEquals(25, series.getPoints(2));
        assertEquals(1, series.getWinner());
    }

    @Test
    public void shouldNotCountMatchesAfterTheEnd() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 1);
        series.recordMatch(1);
        series.recordMatch(2);
        assertEquals(1, series.getMatches());
        assertEquals(0, series.getWins(2));
        assertEquals(1, series.getWinner());
    }

    @Test
    public void shouldPlayAgainstTheMachineWithoutProfile() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, Series.MACHINE_NAME, null, false, 3);
        series.recordMatch(2);
        assertNull(series.getProfile(2));
        assertEquals(Series.MACHINE_NAME, series.getName(2));
        assertEquals("Jero", series.getName(1));
        assertEquals(100, series.getPoints(2));
        assertEquals(25, jero.getPoints());
        assertSame(jero, series.getProfile(1));
    }

    @Test
    public void shouldOnlyAcceptBestOfOneThreeOrFive() {
        int[] wrong = {0, 2, 4, 7};
        for (int i = 0; i < wrong.length; i++) {
            try {
                new Series("Jero", jero, "Laura", laura, false, wrong[i]);
                fail("deberia lanzar la excepcion");
            } catch (DOPOSoccerException e) {
                assertEquals(DOPOSoccerException.INVALID_SERIES, e.getMessage());
            }
        }
    }

    @Test
    public void shouldNeedMoreThanZeroPoints() {
        try {
            new Series("Jero", jero, "Laura", laura, true, 0);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_SERIES, e.getMessage());
        }
    }
        @Test
    public void shouldGivePointsInARematchWithoutChangingTheWinner() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 1);
        series.recordMatch(1);
        series.recordRematch(2);
        assertEquals(1, series.getWinner());
        assertEquals(1, series.getRematches());
        assertEquals(2, series.getMatches());
        assertEquals(1, series.getWins(2));
        assertEquals(125, series.getPoints(2));
        assertEquals(125, laura.getPoints());
    }

    @Test
    public void shouldUseTheProfileThatWasOpened() throws DOPOSoccerException {
        Series series = new Series("Jero", jero, "Laura", laura, false, 3);
        Profile opened = new Profile("Jero");
        series.updateProfile(1, opened);
        series.recordMatch(1);
        assertEquals(100, opened.getPoints());
        assertEquals(0, jero.getPoints());
    }
    
}