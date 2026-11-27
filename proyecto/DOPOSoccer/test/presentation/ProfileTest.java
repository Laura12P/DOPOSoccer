package presentation;
import static org.junit.Assert.*;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.DOPOSoccer;
import domain.Profile;
import domain.Progress;

/**
 * pruebas de los perfiles de las personas y sus puntos
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ProfileTest {
    private static final String NAME = "perfilDePrueba";
    private static final String OTHER = "otroPerfilDePrueba";
    private MainWindow window;

    @Before
    public void setUp() {
        window = new MainWindow();
    }

    @After
    public void tearDown() {
        window.stopMatch();
        window.dispose();
        new File(Progress.fileName(NAME)).delete();
        new File(Progress.fileName(OTHER)).delete();
    }
    private PlayerSelector choose(String name, String country) {
        PlayerSelector selector = new PlayerSelector("jugador 1");
        selector.writeProfile(name);
        selector.loadCountry(country);
        Finder.clickOption(selector, 0);
        return selector;
    }

    private void playOneMatch(Profile one, Profile two, int winner) throws Exception {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.saveProfile(1, one);
        window.saveProfile(2, two);
        window.startSeries(false, 1);
        window.finishMatch(winner, "Son (Corea Del Sur)   1 - 0", 100, 25);
    }

    @Test
    public void shouldPlayWithoutProfile() {
        PlayerSelector selector = choose("", "Nigeria");
        assertNull(selector.getProfile());
        assertTrue(selector.hasSelected());
    }

    @Test
    public void shouldCreateANewProfileWithoutPoints() {
        Profile profile = choose(NAME, "Nigeria").getProfile();
        assertEquals(NAME, profile.getName());
        assertEquals(0, profile.getPoints());
        assertEquals(1, profile.getLevel(Profile.SPEED));
    }

    @Test
    public void shouldKeepThePointsOfEachPerson() throws Exception {
        Profile profile = choose(NAME, "Nigeria").getProfile();
        profile.addPoints(125);
        Progress.save(Progress.fileName(NAME), profile);
        assertEquals(125, choose(NAME, "Nigeria").getProfile().getPoints());
        assertEquals(125, choose(NAME.toUpperCase(), "Nigeria").getProfile().getPoints());
        assertEquals(0, choose(OTHER, "Nigeria").getProfile().getPoints());
    }

    @Test
    public void shouldKeepThePointsWithAnyCharacter() throws Exception {
        Profile profile = choose(NAME, "Nigeria").getProfile();
        profile.addPoints(600);
        profile.upgrade(Profile.KICK);
        Progress.save(Progress.fileName(NAME), profile);
        Profile again = choose(NAME, "Noruega").getProfile();
        assertEquals(100, again.getPoints());
        assertEquals(2, again.getLevel(Profile.KICK));
    }

    @Test
    public void shouldGiveThePointsToThePersonWhoPlayed() throws Exception {
        Profile one = choose(NAME, "Nigeria").getProfile();
        playOneMatch(one, null, 1);
        assertSame(one, window.getProfile(1));
        assertNull(window.getProfile(2));
        assertEquals(DOPOSoccer.WIN_POINTS, choose(NAME, "Nigeria").getProfile().getPoints());
        playOneMatch(choose(NAME, "Nigeria").getProfile(), null, 2);
        assertEquals(DOPOSoccer.WIN_POINTS + DOPOSoccer.LOSE_POINTS,
                choose(NAME, "Nigeria").getProfile().getPoints());
    }

    @Test
    public void shouldNotSaveAnythingWithoutProfile() throws Exception {
        playOneMatch(null, null, 1);
        assertNull(window.getProfile(1));
        assertNull(window.getProfile(2));
        assertEquals("Jugador 1", window.getSeries().getName(1));
        assertEquals(DOPOSoccer.WIN_POINTS, window.getSeries().getPoints(1));
    }

    @Test
    public void shouldKeepTwoProfilesApart() throws Exception {
        playOneMatch(choose(NAME, "Nigeria").getProfile(), choose(OTHER, "Camerun").getProfile(), 2);
        assertEquals(DOPOSoccer.LOSE_POINTS, choose(NAME, "Nigeria").getProfile().getPoints());
        assertEquals(DOPOSoccer.WIN_POINTS, choose(OTHER, "Nigeria").getProfile().getPoints());
    }

    @Test
    public void shouldPlayWithTheStatsOfTheProfile() throws Exception {
        Profile profile = choose(NAME, "Nigeria").getProfile();
        profile.addPoints(500);
        profile.upgrade(Profile.SPEED);
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.saveProfile(1, profile);
        window.startMatch(true, 99, 900, 600, false);
        assertEquals(MainWindow.GAME, window.getCurrentScreen());
        assertEquals(1, PlayerSelector.atRandom().getSpeed());
    }
}