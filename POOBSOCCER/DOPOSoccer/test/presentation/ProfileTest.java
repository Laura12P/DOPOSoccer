package presentation;
import static org.junit.Assert.*;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.DOPOSoccer;
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
        window.dispose();
        new File(Progress.fileName(NAME)).delete();
        new File(Progress.fileName(OTHER)).delete();
    }

    /**
     * escoge el primer jugador de Nigeria con ese perfil
     * @param name es el nombre del perfil, o vacio si no tiene
     * @return el selector con el jugador elegido
     */
    private PlayerSelector choose(String name) {
        PlayerSelector selector = new PlayerSelector("jugador 1");
        selector.writeProfile(name);
        selector.loadCountry("Nigeria");
        Finder.clickOption(selector, 0);
        return selector;
    }

    @Test
    public void shouldPlayWithoutProfile() {
        PlayerSelector selector = choose("");
        assertNull(selector.getProfile());
        assertTrue(selector.hasSelected());
    }

    @Test
    public void shouldKeepThePointsOfEachPerson() throws Exception {
        PlayerSelector one = choose(NAME);
        assertEquals(NAME, one.getProfile());
        assertEquals(0, one.getSelected().getPoints());
        one.getSelected().addPoints(125);
        Progress.save(Progress.fileName(NAME), one.getRoster());
        assertEquals(125, choose(NAME).getSelected().getPoints());
        assertEquals(125, choose(NAME.toUpperCase()).getSelected().getPoints());
        assertEquals(0, choose(OTHER).getSelected().getPoints());
    }

    @Test
    public void shouldGiveThePointsToThePersonWhoPlayed() throws Exception {
        PlayerSelector one = choose(NAME);
        window.savePlayers(one.getSelected(), PlayerSelector.atRandom());
        window.saveProfile(1, one.getProfile(), one.getRoster());
        window.addMatchPoints(true);
        assertEquals(NAME, window.getProfile(1));
        assertNull(window.getProfile(2));
        assertEquals(DOPOSoccer.WIN_POINTS, window.getPoints(1));
        assertEquals(DOPOSoccer.WIN_POINTS, choose(NAME).getSelected().getPoints());
        window.addMatchPoints(false);
        assertEquals(DOPOSoccer.WIN_POINTS + DOPOSoccer.LOSE_POINTS, choose(NAME).getSelected().getPoints());
    }

    @Test
    public void shouldNotSaveAnythingWithoutProfile() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.addMatchPoints(true);
        assertNull(window.getProfile(1));
        assertNull(window.getProfile(2));
    }

    @Test
    public void shouldKeepTwoProfilesApart() throws Exception {
        PlayerSelector one = choose(NAME);
        PlayerSelector two = choose(OTHER);
        window.savePlayers(one.getSelected(), two.getSelected());
        window.saveProfile(1, one.getProfile(), one.getRoster());
        window.saveProfile(2, two.getProfile(), two.getRoster());
        window.addMatchPoints(false);
        assertEquals(DOPOSoccer.LOSE_POINTS, choose(NAME).getSelected().getPoints());
        assertEquals(DOPOSoccer.WIN_POINTS, choose(OTHER).getSelected().getPoints());
    }
}