package presentation;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de las pantallas de seleccion
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ScreensTest {
    private MainWindow window;


    @Before
    public void setUp() {
        window = new MainWindow();
    }

    @After
    public void tearDown() {
        window.dispose();
    }

    @Test
    public void shouldGoToModeFromStart() {
        StartPanel panel = new StartPanel(window);
        assertTrue(Finder.click(panel, "Jugar"));
        assertEquals(MainWindow.MODE, window.getCurrentScreen());
    }

    @Test
    public void shouldGoToTheRightScreenFromMode() {
        ModePanel panel = new ModePanel(window);
        assertTrue(Finder.click(panel, "Player vs Player"));
        assertEquals(MainWindow.COUNTRY_TWO, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Machine vs Player"));
        assertEquals(MainWindow.COUNTRY_ONE, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.START, window.getCurrentScreen());
    }

    @Test
    public void shouldChooseACountryForOnePlayer() {
        CountryOnePanel panel = new CountryOnePanel(window);
        assertTrue(Finder.click(panel, "Nigeria"));
        assertTrue(Finder.click(panel, "listo"));
        assertEquals(MainWindow.PLAYER_ONE, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "volver"));
        assertEquals(MainWindow.MODE, window.getCurrentScreen());
    }

    @Test
    public void shouldChooseAPlayerAndGoToTheMachine() {
        PlayerOnePanel panel = new PlayerOnePanel(window);
        panel.loadCountry("Nigeria");
        Finder.clickOption(panel, 0);
        assertTrue(Finder.click(panel, "listo"));
        assertEquals(MainWindow.MACHINE, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "volver"));
        assertEquals(MainWindow.COUNTRY_ONE, window.getCurrentScreen());
    }

    @Test
    public void shouldChooseTheTypeOfMachine() {
        MachinePanel panel = new MachinePanel(window);
        assertEquals("Torpe", panel.getSelected());
        assertTrue(Finder.click(panel, "Experta"));
        assertEquals("Experta", panel.getSelected());
        assertTrue(Finder.click(panel, "Listo"));
        assertEquals("Experta", window.getMachine());
        assertEquals(MainWindow.CONTROLS_ONE, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.PLAYER_ONE, window.getCurrentScreen());
    }

    @Test
    public void shouldChooseCountriesForTwoPlayers() {
        CountryTwoPanel panel = new CountryTwoPanel(window, false);
        int half = Finder.countOptions(panel) / 2;
        Finder.clickOption(panel, 0);
        Finder.clickOption(panel, half + 1);
        assertTrue(Finder.click(panel, "Listo"));
        assertEquals(MainWindow.PLAYER_TWO, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.MODE, window.getCurrentScreen());
    }

    @Test
    public void shouldChooseTwoPlayers() {
        PlayerTwoPanel panel = new PlayerTwoPanel(window, false);
        panel.loadCountries("Nigeria", "Camerun");
        int half = Finder.countOptions(panel) / 2;
        Finder.clickOption(panel, 0);
        Finder.clickOption(panel, half);
        assertTrue(Finder.click(panel, "Listo"));
        assertEquals(MainWindow.CONTROLS_TWO, window.getCurrentScreen());
        assertNull(window.getMachine());
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.COUNTRY_TWO, window.getCurrentScreen());
    }

    @Test
    public void shouldShowTheControlsForTwoPlayers() {
        ControlsPanel panel = new ControlsPanel(window, true);
        assertTrue(Finder.hasLabel(panel, "Jugador 2"));
        assertTrue(Finder.click(panel, "jugar"));
        assertEquals(MainWindow.CONFIG, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "volver"));
        assertEquals(MainWindow.PLAYER_TWO, window.getCurrentScreen());
    }

    @Test
    public void shouldShowTheControlsForOnePlayer() {
        ControlsPanel panel = new ControlsPanel(window, false);
        assertFalse(Finder.hasLabel(panel, "Jugador 2"));
        assertTrue(Finder.click(panel, "jugar"));
        assertEquals(MainWindow.CONFIG, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "volver"));
        assertEquals(MainWindow.MACHINE, window.getCurrentScreen());
    }

    @Test
    public void shouldConfigureAndStartTheMatch() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        ConfigPanel panel = new ConfigPanel(window);
        panel.setBackScreen(MainWindow.CONTROLS_ONE);
        assertTrue(Finder.click(panel, "Por goles"));
        assertTrue(Finder.hasLabel(panel, "Goles para ganar"));
        assertTrue(Finder.click(panel, "Por tiempo"));
        assertTrue(Finder.hasLabel(panel, "Minutos del partido"));
        assertTrue(Finder.click(panel, "Grande"));
        assertTrue(Finder.click(panel, "Por goles"));
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.CONTROLS_ONE, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Jugar"));
        assertEquals(MainWindow.GAME, window.getCurrentScreen());
        assertEquals(1100, window.getWidth());
    }

    @Test
    public void shouldStartTheMatchInFullScreen() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        ConfigPanel panel = new ConfigPanel(window);
        assertTrue(Finder.click(panel, "Por goles"));
        assertTrue(Finder.click(panel, "Pantalla completa"));
        assertTrue(Finder.click(panel, "Jugar"));
        assertTrue(window.isFullScreen());
    }

    @Test
    public void shouldSelectACountry() {
        CountrySelector selector = new CountrySelector("jugador 1");
        assertFalse(selector.hasSelected());
        assertTrue(Finder.click(selector, "Camerun"));
        assertTrue(selector.hasSelected());
        assertEquals("Camerun", selector.getSelected());
        assertTrue(Finder.click(selector, "Noruega"));
        assertEquals("Noruega", selector.getSelected());
    }

    @Test
    public void shouldSelectAPlayerOfTheCountry() {
        PlayerSelector selector = new PlayerSelector("jugador 1");
        selector.loadCountry("Corea Del Sur");
        assertFalse(selector.hasSelected());
        assertTrue(Finder.countOptions(selector) > 0);
        Finder.clickOption(selector, 0);
        assertTrue(selector.hasSelected());
        assertEquals("Corea Del Sur", selector.getSelected().getCountry());
        assertNotNull(PlayerSelector.atRandom());
    }

    @Test
    public void shouldShowTheKeysOfAPlayer() {
        String[] keys = {"A", "D", "W", "S", "Q"};
        ControlsTable table = new ControlsTable("Jugador 1", keys);
        assertTrue(Finder.hasLabel(table, "Jugador 1"));
        assertTrue(Finder.hasLabel(table, "W"));
    }

    @Test
    public void shouldCloseThePauseMenuWhenAnOptionIsChosen() {
        PauseMenu menu = new PauseMenu(window);
        assertTrue(Finder.click(menu.getContentPane(), "Continuar"));
        assertFalse(menu.isVisible());
        menu.dispose();
    }
}