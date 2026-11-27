package presentation;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de la ventana principal
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class MainWindowTest {
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
    public void shouldStartOnTheStartScreen() {
        assertEquals(MainWindow.START, window.getCurrentScreen());
        assertFalse(window.isFullScreen());
    }

    @Test
    public void shouldChangeTheScreen() {
        window.showScreen(MainWindow.MODE);
        assertEquals(MainWindow.MODE, window.getCurrentScreen());
        window.showScreen(MainWindow.MACHINE);
        assertEquals(MainWindow.MACHINE, window.getCurrentScreen());
    }

    @Test
    public void shouldGoToChooseThePlayers() {
        window.choosePlayerOne("Nigeria");
        assertEquals(MainWindow.PLAYER_ONE, window.getCurrentScreen());
        window.choosePlayerTwo("Nigeria", "Camerun");
        assertEquals(MainWindow.PLAYER_TWO, window.getCurrentScreen());
    }

    @Test
    public void shouldForgetTheMachineWhenTwoPeoplePlay() {
        window.saveMachine("Experta");
        assertEquals("Experta", window.getMachine());
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        assertNull(window.getMachine());
    }

    @Test
    public void shouldGoToTheConfiguration() {
        window.showConfig(MainWindow.CONTROLS_TWO);
        assertEquals(MainWindow.CONFIG, window.getCurrentScreen());
        window.showScreen(MainWindow.START);
        window.changeConfig();
        assertEquals(MainWindow.CONFIG, window.getCurrentScreen());
    }

    @Test
    public void shouldStartAMatch() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.startMatch(true, 99, 800, 550, false);
        assertEquals(MainWindow.GAME, window.getCurrentScreen());
        assertEquals(800, window.getWidth());
        assertFalse(window.isFullScreen());
    }

    @Test
    public void shouldStartAMatchAgainstTheMachine() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.saveMachine("Defensiva");
        window.startMatch(true, 99, 900, 600, false);
        window.restartMatch();
        assertEquals(MainWindow.GAME, window.getCurrentScreen());
        assertEquals("Defensiva", window.getMachine());
    }

    @Test
    public void shouldGoInAndOutOfFullScreen() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.startMatch(true, 99, 900, 600, true);
        assertTrue(window.isFullScreen());
        window.showScreen(MainWindow.START);
        assertFalse(window.isFullScreen());
        assertEquals(900, window.getWidth());
        assertEquals(600, window.getHeight());
    }
}