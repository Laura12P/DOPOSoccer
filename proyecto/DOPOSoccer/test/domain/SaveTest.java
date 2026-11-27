package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de guardar y abrir partidas, que cada parte del estado se conserve
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class SaveTest {
    private Player one;
    private Player two;
    private DOPOSoccer game;
    private ArrayList<File> files;

    @Before
    public void setUp() {
        one = new Player("Uno", "Colombia", 3, 3, 3, 3, new BlueAura());
        two = new Player("Dos", "Brasil", 2, 2, 2, 2, new Tornado());
        game = new DOPOSoccer(one, two, 900, 500, new TimeMode(2));
        files = new ArrayList<File>();
    }

    @After
    public void tearDown() {
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
    }

    private String path() throws IOException {
        File file = File.createTempFile("saveTest", ".dat");
        files.add(file);
        return file.getPath();
    }

    private String profilePath() throws IOException {
        File file = File.createTempFile("saveTest", ".prf");
        files.add(file);
        return file.getPath();
    }

    private DOPOSoccer saveAndOpen() throws Exception {
        String path = path();
        game.save(path);
        return DOPOSoccer.open(path);
    }

    @Test
    public void shouldKeepThePositionsOfThePlayersAndTheBall() throws Exception {
        game.moveRight(1);
        game.getBall().place(300, 150);
        DOPOSoccer opened = saveAndOpen();
        assertEquals(one.getX(), opened.getPlayerOne().getX(), 0.001);
        assertEquals(two.getX(), opened.getPlayerTwo().getX(), 0.001);
        assertEquals(300, opened.getBall().getX(), 0.001);
        assertEquals(150, opened.getBall().getHeight(), 0.001);
    }

    @Test
    public void shouldKeepTheGoalsLifeAndPoints() throws Exception {
        one.addGoal();
        one.addGoal();
        two.receiveDamage(30);
        one.addPoints(100);
        one.losePoints(25);
        DOPOSoccer opened = saveAndOpen();
        assertEquals(2, opened.getPlayerOne().getGoals());
        assertEquals(two.getLife(), opened.getPlayerTwo().getLife());
        assertEquals(75, opened.getPlayerOne().getPoints());
    }

    @Test
    public void shouldKeepTheStatsAndThePowerOfThePlayers() throws Exception {
        DOPOSoccer opened = saveAndOpen();
        assertEquals("Uno", opened.getPlayerOne().getName());
        assertEquals("Colombia", opened.getPlayerOne().getCountry());
        assertEquals(3, opened.getPlayerOne().getSpeed());
        assertEquals(2, opened.getPlayerTwo().getJump());
        assertEquals("Aura Azul", opened.getPlayerOne().getPower().getName());
        assertEquals("Tornado", opened.getPlayerTwo().getPower().getName());
        assertEquals(4, opened.getPlayerOne().getPower().getAura().length);
    }

    @Test
    public void shouldKeepThePowerBarAndTheArmedPower() throws Exception {
        one.chargePower(100);
        one.armPower();
        two.chargePower(40);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.getPlayerOne().isPowerArmed());
        assertEquals(0, opened.getPlayerOne().getPowerBar());
        assertEquals(40, opened.getPlayerTwo().getPowerBar());
    }

    @Test
    public void shouldKeepAnActiveShockEffect() throws Exception {
        two.applyEffect(new ShockEffect(), 60);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.getPlayerTwo().isShocked());
    }

    @Test
    public void shouldKeepAnActivePushEffectAndItsDirection() throws Exception {
        one.applyEffect(new PushEffect(1), 60);
        DOPOSoccer opened = saveAndOpen();
        double start = opened.getPlayerOne().getX();
        opened.getPlayerOne().update(0, 1000);
        assertEquals(start + 10, opened.getPlayerOne().getX(), 0.001);
    }

    @Test
    public void shouldKeepAnActiveConfusionEffect() throws Exception {
        two.applyEffect(new ConfusionEffect(), 60);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.getPlayerTwo().isConfused());
    }

    @Test
    public void shouldKeepAnActiveKnockOutEffect() throws Exception {
        two.applyEffect(new KnockOutEffect(), 60);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.getPlayerTwo().isStunned());
    }

    @Test
    public void shouldKeepTheClockOfATimedMatch() throws Exception {
        for (int i = 0; i < 10; i++) {
            game.passSecond();
        }
        DOPOSoccer opened = saveAndOpen();
        assertEquals("Tiempo: 1:50", opened.getModeText());
        assertFalse(opened.isByGoals());
        assertEquals(2, opened.getAmount());
    }

    @Test
    public void shouldKeepTheGoldenGoal() throws Exception {
        for (int i = 0; i < 120; i++) {
            game.passSecond();
        }
        DOPOSoccer opened = saveAndOpen();
        assertEquals("GOL DE ORO", opened.getModeText());
        assertFalse(opened.isOver());
    }

    @Test
    public void shouldKeepAMatchByGoals() throws Exception {
        game = new DOPOSoccer(one, two, 900, 500, new GoalsMode(4));
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.isByGoals());
        assertEquals(4, opened.getAmount());
        assertEquals("Meta: 4 goles", opened.getModeText());
    }

    @Test
    public void shouldKeepTheFrozenTime() throws Exception {
        game.freezeTime(5);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.isTimeFrozen());
        assertTrue(opened.getModeText().contains("congelado"));
    }

    @Test
    public void shouldKeepAMatchThatIsOver() throws Exception {
        game = new DOPOSoccer(one, two, 900, 500, new GoalsMode(1));
        game.getBall().place(870, 50);
        game.update();
        assertTrue(game.isOver());
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.isOver());
        assertEquals(1, opened.getWinner().getGoals());
        assertEquals(100, opened.getWinner().getPoints());
        assertEquals(25, opened.getLoser().getPoints());
    }

    @Test
    public void shouldKeepTheChangedBodyOfTheBall() throws Exception {
        game.getBall().changeBody(0.25, 0.8, 32, 100);
        DOPOSoccer opened = saveAndOpen();
        assertEquals(32, opened.getBall().getRadius());
    }

    @Test
    public void shouldKeepTheFakeBalls() throws Exception {
        game.getBall().split(100);
        DOPOSoccer opened = saveAndOpen();
        assertEquals(7, opened.getBall().getFakes().size());
    }

    @Test
    public void shouldKeepEveryMachine() throws Exception {
        for (int i = 0; i < Machines.getNames().size(); i++) {
            String name = Machines.getNames().get(i);
            game.setMachine(name);
            DOPOSoccer opened = saveAndOpen();
            assertTrue(opened.isMachine(2));
            assertEquals(name, opened.getMachineName());
            for (int step = 0; step < 100; step++) {
                opened.update();
            }
        }
    }

    @Test
    public void shouldKeepAMatchWithoutMachine() throws Exception {
        DOPOSoccer opened = saveAndOpen();
        assertFalse(opened.isMachine(2));
        assertNull(opened.getMachineName());
    }

    @Test
    public void shouldKeepTheSeriesWithItsProfiles() throws Exception {
        Profile ana = new Profile("Ana");
        ana.addPoints(1500);
        ana.upgrade(Profile.SPEED);
        Series series = new Series("Ana", ana, Series.MACHINE_NAME, null, true, 300);
        series.recordMatch(1, 100, 25);
        game.setSeries(series);
        DOPOSoccer opened = saveAndOpen();
        Series other = opened.getSeries();
        assertTrue(other.isByPoints());
        assertEquals(300, other.getTarget());
        assertEquals(1, other.getWins(1));
        assertEquals(100, other.getPoints(1));
        assertEquals(Series.MACHINE_NAME, other.getName(2));
        assertNull(other.getProfile(2));
        assertEquals(2, other.getProfile(1).getLevel(Profile.SPEED));
    }

    @Test
    public void shouldKeepAMatchWithoutSeries() throws Exception {
        assertNull(saveAndOpen().getSeries());
    }

    @Test
    public void shouldKeepTheSizeOfTheField() throws Exception {
        DOPOSoccer opened = saveAndOpen();
        assertEquals(900, opened.getWidth());
        assertEquals(500, opened.getHeight());
    }

    @Test
    public void shouldKeepPlayingAfterOpening() throws Exception {
        game.getBall().place(30, 50);
        DOPOSoccer opened = saveAndOpen();
        assertTrue(opened.update());
        assertEquals(1, opened.getPlayerTwo().getGoals());
        opened.passSecond();
        assertEquals("Tiempo: 1:59", opened.getModeText());
    }

    @Test
    public void shouldNotChangeTheOriginalWhenSaving() throws Exception {
        one.addGoal();
        saveAndOpen();
        assertEquals(1, one.getGoals());
        assertEquals("Tiempo: 2:00", game.getModeText());
    }

    @Test
    public void shouldBeIndependentFromTheOriginalAfterOpening() throws Exception {
        DOPOSoccer opened = saveAndOpen();
        opened.getPlayerOne().addGoal();
        assertEquals(0, one.getGoals());
        assertNotSame(one, opened.getPlayerOne());
    }

    @Test
    public void shouldOverwriteAnExistingFile() throws Exception {
        String path = path();
        game.save(path);
        one.addGoal();
        game.save(path);
        assertEquals(1, DOPOSoccer.open(path).getPlayerOne().getGoals());
    }

    @Test
    public void shouldSaveTwiceTheSameMatch() throws Exception {
        String path = path();
        game.save(path);
        game.save(path);
        assertNotNull(DOPOSoccer.open(path));
    }

    @Test
    public void shouldAcceptTheExtensionInAnyCase() throws Exception {
        File file = File.createTempFile("saveTest", ".DaT");
        files.add(file);
        game.save(file.getPath());
        assertNotNull(DOPOSoccer.open(file.getPath()));
    }

    @Test
    public void shouldNotCreateTheFileWhenTheExtensionIsWrong() {
        File file = new File("saveTestNoDebeExistir.txt");
        try {
            game.save(file.getPath());
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_EXTENSION, e.getMessage());
        }
        assertFalse(file.exists());
    }

    @Test
    public void shouldRejectAFileWithoutExtension() {
        try {
            game.save("saveTestSinExtension");
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_EXTENSION, e.getMessage());
        }
    }

    @Test
    public void shouldRejectANameThatOnlyContainsDatInTheMiddle() {
        try {
            game.save("partida.dat.txt");
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_EXTENSION, e.getMessage());
        }
    }

    @Test
    public void shouldFailSavingInAFolderThatDoesNotExist() {
        try {
            game.save("carpetaQueNoExisteZzz/partida.dat");
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CANNOT_SAVE, e.getMessage());
        }
    }

    @Test
    public void shouldFailOpeningAFileThatDoesNotExist() {
        try {
            DOPOSoccer.open("noExisteZzz.dat");
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.FILE_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void shouldNotOpenAProfileAsAMatchNorTheReverse() throws Exception {
        String profilePath = profilePath();
        Progress.save(profilePath, new Profile("Ana"));
        try {
            DOPOSoccer.open(profilePath);
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_EXTENSION, e.getMessage());
        }
        String matchPath = path();
        game.save(matchPath);
        try {
            Progress.open(matchPath);
            fail();
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_PROFILE_EXTENSION, e.getMessage());
        }
    }

    @Test
    public void shouldKeepTheLevelsAndPointsOfAProfile() throws Exception {
        Profile profile = new Profile("Ana");
        profile.addPoints(1500);
        profile.upgrade(Profile.KICK);
        String path = profilePath();
        Progress.save(path, profile);
        Profile opened = Progress.open(path);
        assertEquals("Ana", opened.getName());
        assertEquals(1000, opened.getPoints());
        assertEquals(2, opened.getLevel(Profile.KICK));
        assertEquals(1000, opened.getUpgradeCost());
    }
}