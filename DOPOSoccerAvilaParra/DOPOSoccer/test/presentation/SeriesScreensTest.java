package presentation;
import static org.junit.Assert.*;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import domain.BlueAura;
import domain.Confusion;
import domain.DOPOSoccer;
import domain.DOPOSoccerException;
import domain.GoalsMode;
import domain.Player;
import domain.Profile;
import domain.Progress;
import domain.Series;

/**
 * pruebas de las pantallas de la serie
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class SeriesScreensTest {
    private static final String JERO = "jeroDePrueba";
    private static final String LAURA = "lauraDePrueba";
    private static final String MATCH = "partidaDePrueba.dat";
    private MainWindow window;
    private Profile jero;
    private Profile laura;

    /**
     * crea la ventana, los dos perfiles y la configuracion antes de cada prueba
     */
    @Before
    public void setUp() {
        window = new MainWindow();
        jero = new Profile(JERO);
        laura = new Profile(LAURA);
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.saveProfile(1, jero);
        window.saveProfile(2, laura);
        window.saveConfig(true, 1, 900, 600, false);
    }

    /**
     * para el partido, cierra la ventana y borra los archivos que crean las pruebas
     */
    @After
    public void tearDown() {
        window.stopMatch();
        window.dispose();
        new File(Progress.fileName(JERO)).delete();
        new File(Progress.fileName(LAURA)).delete();
        new File(MATCH).delete();
    }

    /**
     * espera un momento a que el reloj de la cancha mueva el juego
     * @param screen es la pantalla que se espera
     * @throws InterruptedException si interrumpen la espera
     */
    private void waitFor(String screen) throws InterruptedException {
        for (int i = 0; i < 60 && !screen.equals(window.getCurrentScreen()); i++) {
            Thread.sleep(50);
        }
    }

    /**
     * despues de la configuracion debe mostrar la pantalla de serie, sin serie creada
     */
    @Test
    public void shouldGoToTheSeriesAfterTheConfiguration() {
        assertEquals(MainWindow.SERIES, window.getCurrentScreen());
        assertNull(window.getSeries());
    }

    /**
     * debe crear una serie al mejor de 5 con los nombres de los perfiles
     */
    @Test
    public void shouldStartABestOfFiveSeries() {
        SeriesPanel panel = new SeriesPanel(window);
        assertTrue(Finder.click(panel, "Mejor de 5"));
        assertTrue(Finder.click(panel, "Siguiente"));
        assertEquals(MainWindow.UPGRADE, window.getCurrentScreen());
        Series series = window.getSeries();
        assertFalse(series.isByPoints());
        assertEquals(5, series.getTarget());
        assertEquals(JERO, series.getName(1));
        assertEquals(LAURA, series.getName(2));
    }

    /**
     * debe crear una serie a puntos con la meta de 300
     */
    @Test
    public void shouldStartASeriesByPoints() {
        SeriesPanel panel = new SeriesPanel(window);
        assertTrue(Finder.click(panel, "A puntos"));
        assertTrue(Finder.click(panel, "Siguiente"));
        assertTrue(window.getSeries().isByPoints());
        assertEquals(300, window.getSeries().getTarget());
    }

    /**
     * si juega contra la maquina, el jugador 2 no tiene perfil y se llama como la maquina
     */
    @Test
    public void shouldCallTheMachineByItsName() {
        window.savePlayers(PlayerSelector.atRandom(), PlayerSelector.atRandom());
        window.saveProfile(1, jero);
        window.saveMachine("Ofensiva");
        SeriesPanel panel = new SeriesPanel(window);
        assertTrue(Finder.click(panel, "Mejor de 1"));
        assertTrue(Finder.click(panel, "Siguiente"));
        assertNull(window.getSeries().getProfile(2));
        assertEquals(Series.MACHINE_NAME, window.getSeries().getName(2));
    }

    /**
     * el boton volver debe regresar a la configuracion
     */
    @Test
    public void shouldGoBackToTheConfiguration() {
        window.showConfig(MainWindow.CONTROLS_TWO);
        SeriesPanel panel = new SeriesPanel(window);
        assertTrue(Finder.click(panel, "Volver"));
        assertEquals(MainWindow.CONFIG, window.getCurrentScreen());
    }

    /**
     * con puntos suficientes debe mejorar la habilidad y guardar el progreso
     * @throws DOPOSoccerException si falla abrir el progreso guardado
     */
    @Test
    public void shouldUpgradeWhenThereAreEnoughPoints() throws DOPOSoccerException {
        window.startSeries(false, 3);
        jero.addPoints(500);
        UpgradePanel panel = new UpgradePanel(window);
        panel.showSeries(window.getSeries());
        assertTrue(Finder.hasLabel(panel, "Siguiente mejora: 500"));
        assertTrue(Finder.click(panel, "Speed +"));
        assertEquals(2, jero.getLevel(Profile.SPEED));
        assertTrue(Finder.hasLabel(panel, JERO + " mejoro Speed"));
        assertTrue(Finder.hasLabel(panel, "Speed: 2 / 10"));
        assertEquals(2, Progress.open(Progress.fileName(JERO)).getLevel(Profile.SPEED));
    }

    /**
     * sin puntos suficientes debe avisar y no subir el nivel
     * @throws DOPOSoccerException si falla algo con el progreso
     */
    @Test
    public void shouldSayWhenThereAreNotEnoughPoints() throws DOPOSoccerException {
        window.startSeries(false, 3);
        UpgradePanel panel = new UpgradePanel(window);
        panel.showSeries(window.getSeries());
        assertTrue(Finder.click(panel, "Kick +"));
        assertTrue(Finder.hasLabel(panel, DOPOSoccerException.NOT_ENOUGH_POINTS));
        assertEquals(1, jero.getLevel(Profile.KICK));
    }

    /**
     * un jugador sin perfil no debe ver las mejoras
     * @throws DOPOSoccerException si falla algo con el progreso
     */
    @Test
    public void shouldNotShowUpgradesWithoutProfile() throws DOPOSoccerException {
        window.saveProfile(2, null);
        window.startSeries(false, 1);
        UpgradePanel panel = new UpgradePanel(window);
        panel.showSeries(window.getSeries());
        assertTrue(Finder.hasLabel(panel, "Sin perfil, no gana puntos ni mejora"));
        assertTrue(Finder.hasLabel(panel, "Jugador 2"));
    }

    /**
     * despues de mejorar, el boton jugar debe llevar a la cancha
     * @throws DOPOSoccerException si falla la mejora
     */
    @Test
    public void shouldPlayWithTheUpgradedStats() throws DOPOSoccerException {
        window.startSeries(false, 3);
        laura.addPoints(500);
        window.upgrade(2, Profile.SURVIVAL);
        UpgradePanel panel = new UpgradePanel(window);
        panel.showSeries(window.getSeries());
        assertTrue(Finder.click(panel, "Jugar"));
        assertEquals(MainWindow.GAME, window.getCurrentScreen());
    }

    /**
     * al terminar cada partido debe mostrar la tabla con el ganador y los puntos
     * @throws DOPOSoccerException si falla abrir el progreso guardado
     */
    @Test
    public void shouldShowTheTableAfterEachMatch() throws DOPOSoccerException {
        window.startSeries(false, 3);
        window.finishMatch(1, "Son (Corea Del Sur)   1 - 0", 100, 25);
        assertEquals(MainWindow.RESULTS, window.getCurrentScreen());
        assertEquals(100, Progress.open(Progress.fileName(JERO)).getPoints());
        ResultsPanel panel = new ResultsPanel(window);
        panel.showResults(window.getSeries(), 1, "Son (Corea Del Sur)   1 - 0");
        assertTrue(Finder.hasLabel(panel, "PARTIDO 1"));
        assertTrue(Finder.hasLabel(panel, "Ganador: " + JERO + " con Son (Corea Del Sur)   1 - 0"));
        assertTrue(Finder.hasLabel(panel, JERO + " ahora tiene 100 puntos y " + LAURA
                + " se queda con 25 puntos"));
        assertTrue(Finder.hasLabel(panel, "Serie al mejor de 3 partidos"));
        assertTrue(Finder.hasLabel(panel, "Puntos para mejoras"));
        assertTrue(Finder.hasLabel(panel, "100"));
        assertTrue(Finder.click(panel, "Siguiente partido"));
        assertEquals(MainWindow.UPGRADE, window.getCurrentScreen());
    }

    /**
     * al terminar la serie debe decir quien gano y permitir reiniciar, cambiar o volver al inicio
     * @throws DOPOSoccerException si falla algo con el progreso
     */
    @Test
    public void shouldSayWhoWonTheSeries() throws DOPOSoccerException {
        window.startSeries(true, 100);
        window.finishMatch(2, "Haaland (Noruega)   0 - 1", 100, 25);
        assertTrue(window.getSeries().isOver());
        ResultsPanel panel = new ResultsPanel(window);
        panel.showResults(window.getSeries(), 2, "Haaland (Noruega)   0 - 1");
        assertTrue(Finder.hasLabel(panel, LAURA.toUpperCase() + " GANA LA SERIE"));
        assertTrue(Finder.hasLabel(panel, "Serie a 100 puntos, la gano " + LAURA));
        assertTrue(Finder.click(panel, "Reiniciar serie"));
        assertEquals(MainWindow.UPGRADE, window.getCurrentScreen());
        assertEquals(0, window.getSeries().getMatches());
        assertEquals(100, laura.getPoints());
        assertTrue(Finder.click(panel, "Cambiar serie"));
        assertEquals(MainWindow.SERIES, window.getCurrentScreen());
        assertTrue(Finder.click(panel, "Inicio"));
        assertEquals(MainWindow.START, window.getCurrentScreen());
    }

    /**
     * un jugador sin perfil debe aparecer con un guion en la tabla
     * @throws DOPOSoccerException si falla algo con el progreso
     */
    @Test
    public void shouldShowADashWithoutProfile() throws DOPOSoccerException {
        window.saveProfile(2, null);
        window.startSeries(false, 3);
        window.finishMatch(2, "Haaland (Noruega)   0 - 1", 100, 25);
        ResultsPanel panel = new ResultsPanel(window);
        panel.showResults(window.getSeries(), 2, "Haaland (Noruega)   0 - 1");
        assertTrue(Finder.hasLabel(panel, "-"));
        assertTrue(Finder.hasLabel(panel, "Jugador 2"));
    }

    /**
     * si no habia serie, al terminar el partido debe crear una serie de un partido
     */
    @Test
    public void shouldMakeASeriesOfOneMatchWhenThereWasNone() {
        window.finishMatch(1, "Son (Corea Del Sur)   1 - 0", 100, 25);
        assertEquals(MainWindow.RESULTS, window.getCurrentScreen());
        assertTrue(window.getSeries().isOver());
        assertEquals(1, window.getSeries().getWinner());
    }

    /**
     * despues de terminar la serie debe permitir jugar una revancha
     * @throws DOPOSoccerException si falla algo con el progreso
     */
    @Test
    public void shouldPlayARematchAfterTheSeries() throws DOPOSoccerException {
        window.startSeries(false, 1);
        window.finishMatch(1, "Son (Corea Del Sur)   1 - 0", 100, 25);
        ResultsPanel panel = new ResultsPanel(window);
        panel.showResults(window.getSeries(), 1, "Son (Corea Del Sur)   1 - 0");
        assertTrue(Finder.click(panel, "Revancha"));
        assertEquals(MainWindow.UPGRADE, window.getCurrentScreen());
        window.finishMatch(2, "Haaland (Noruega)   0 - 2", 100, 25);
        assertEquals(1, window.getSeries().getRematches());
        assertEquals(1, window.getSeries().getWinner());
        assertEquals(125, laura.getPoints());
        panel.showResults(window.getSeries(), 2, "Haaland (Noruega)   0 - 2");
        assertTrue(Finder.hasLabel(panel, "REVANCHA 1"));
        assertTrue(Finder.hasLabel(panel, LAURA + " ahora tiene 125 puntos y " + JERO
                + " se queda con 125 puntos"));
    }

    /**
     * al abrir una partida guardada debe volver la serie y el progreso de los perfiles
     * @throws DOPOSoccerException si falla guardar o abrir
     */
    @Test
    public void shouldOpenASavedMatchWithItsSeries() throws DOPOSoccerException {
        window.startSeries(false, 3);
        window.finishMatch(1, "Son (Corea Del Sur)   1 - 0", 100, 25);
        Player one = new Player("Son", "Corea Del Sur", 1, 1, 1, 1, new BlueAura());
        Player two = new Player("Haaland", "Noruega", 1, 1, 1, 1, new Confusion());
        DOPOSoccer soccer = new DOPOSoccer(one, two, 880, 480, new GoalsMode(3));
        soccer.setSeries(window.getSeries());
        soccer.save(MATCH);
        jero.addPoints(500);
        Progress.save(Progress.fileName(JERO), jero);
        MainWindow other = new MainWindow();
        other.openMatch(MATCH);
        assertEquals(MainWindow.GAME, other.getCurrentScreen());
        assertEquals(1, other.getSeries().getWins(1));
        assertEquals(600, other.getProfile(1).getPoints());
        assertEquals(LAURA, other.getSeries().getName(2));
        other.dispose();
    }

    /**
     * al terminar el partido en la cancha debe mostrar la tabla y sumar los puntos
     * @throws DOPOSoccerException si falla algo con el progreso
     * @throws InterruptedException si interrumpen la espera
     */
    @Test
    public void shouldEndTheMatchAndShowTheTable() throws DOPOSoccerException, InterruptedException {
        window.startSeries(false, 3);
        GamePanel game = new GamePanel(window);
        game.setSize(900, 560);
        game.doLayout();
        Player one = new Player("Son", "Corea Del Sur", 1, 1, 1, 1, new BlueAura());
        Player two = new Player("Haaland", "Noruega", 1, 1, 1, 1, new Confusion());
        DOPOSoccer soccer = new DOPOSoccer(one, two, 880, 480, new GoalsMode(1));
        soccer.getBall().place(850, 50);
        game.loadMatch(soccer);
        waitFor(MainWindow.RESULTS);
        assertEquals(MainWindow.RESULTS, window.getCurrentScreen());
        assertEquals(1, window.getSeries().getWins(1));
        assertEquals(100, jero.getPoints());
    }
}