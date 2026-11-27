package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de guardar y abrir partidas y perfiles, del log y de las excepciones
 */
public class PersistenceTest {
    private DOPOSoccer game;

    private File temp(String suffix) throws IOException {
        File file = File.createTempFile("doposoccer", suffix);
        file.deleteOnExit();
        return file;
    }

    @Before
    public void setUp() {
        Player one = new Player("A", "P", 2, 1, 1, 1, new BlueAura());
        Player two = new Player("B", "P", 1, 1, 1, 1, new Tornado());
        game = new DOPOSoccer(one, two, 900, 500, new GoalsMode(3));
    }

    private void expect(String message, DOPOSoccerException e) {
        assertEquals(message, e.getMessage());
    }

    @Test
    public void shouldSaveAndOpenTheSameMatch() throws Exception {
        game.getPlayerOne().addGoal();
        game.setMachine("Defensiva");
        File file = temp(".dat");
        game.save(file.getPath());
        DOPOSoccer opened = DOPOSoccer.open(file.getPath());
        assertEquals(1, opened.getPlayerOne().getGoals());
        assertEquals("Meta: 3 goles", opened.getModeText());
        assertTrue(opened.isByGoals());
        assertEquals("Defensiva", opened.getMachineName());
        assertEquals(900, opened.getWidth());
        assertEquals(500, opened.getHeight());
        assertEquals("Tornado", opened.getPlayerTwo().getPower().getName());
        assertEquals(2, opened.getPlayerOne().getSpeed());
    }

    @Test
    public void shouldKeepTheClockOfATimedMatch() throws Exception {
        DOPOSoccer timed = new DOPOSoccer(game.getPlayerOne(), game.getPlayerTwo(), 900, 500,
                new TimeMode(1));
        for (int i = 0; i < 5; i++) {
            timed.passSecond();
        }
        File file = temp(".dat");
        timed.save(file.getPath());
        assertEquals("Tiempo: 0:55", DOPOSoccer.open(file.getPath()).getModeText());
    }

    @Test
    public void shouldKeepTheSeries() throws Exception {
        game.setSeries(new Series("A", new Profile("Ana"), "B", null, false, 3));
        File file = temp(".dat");
        game.save(file.getPath());
        DOPOSoccer opened = DOPOSoccer.open(file.getPath());
        assertEquals(3, opened.getSeries().getTarget());
        assertEquals("Ana", opened.getSeries().getProfile(1).getName());
    }

    @Test
    public void shouldKeepPlayingAfterOpening() throws Exception {
        File file = temp(".dat");
        game.save(file.getPath());
        DOPOSoccer opened = DOPOSoccer.open(file.getPath());
        for (int i = 0; i < 100; i++) {
            opened.update();
        }
        opened.passSecond();
    }

    @Test
    public void shouldAcceptTheExtensionInCapitals() throws Exception {
        File file = temp(".DAT");
        game.save(file.getPath());
        assertNotNull(DOPOSoccer.open(file.getPath()));
    }

    @Test
    public void shouldRejectOtherExtensions() throws Exception {
        try {
            game.save("partida.txt");
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_EXTENSION, e);
        }
        try {
            DOPOSoccer.open("partida.txt");
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_EXTENSION, e);
        }
    }

    @Test
    public void shouldFailWhenTheFileDoesNotExist() {
        try {
            DOPOSoccer.open("no_existe_zzz.dat");
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.FILE_NOT_FOUND, e);
        }
    }

    @Test
    public void shouldFailWhenTheFileIsNotAMatch() throws Exception {
        File file = temp(".dat");
        FileWriter writer = new FileWriter(file);
        writer.write("esto no es una partida");
        writer.close();
        try {
            DOPOSoccer.open(file.getPath());
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_FILE, e);
        }
    }

    @Test
    public void shouldFailWhenTheFileIsEmpty() throws Exception {
        try {
            DOPOSoccer.open(temp(".dat").getPath());
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_FILE, e);
        }
    }

    @Test
    public void shouldFailWhenTheFileIsAProfile() throws Exception {
        File file = temp(".prf");
        Progress.save(file.getPath(), new Profile("Ana"));
        try {
            DOPOSoccer.open(file.getPath());
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_EXTENSION, e);
        }
    }

    @Test
    public void shouldFailWhenTheFolderDoesNotExist() {
        try {
            game.save("carpeta_que_no_existe_zzz/partida.dat");
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.CANNOT_SAVE, e);
        }
    }

    @Test
    public void shouldSaveAndOpenAProfile() throws Exception {
        Profile profile = new Profile("Ana");
        profile.addPoints(1500);
        profile.upgrade(Profile.SPEED);
        File file = temp(".prf");
        Progress.save(file.getPath(), profile);
        Profile opened = Progress.open(file.getPath());
        assertEquals("Ana", opened.getName());
        assertEquals(1000, opened.getPoints());
        assertEquals(2, opened.getLevel(Profile.SPEED));
        assertEquals(1000, opened.getUpgradeCost());
    }

    @Test
    public void shouldFailOpeningAMatchAsAProfile() throws Exception {
        File file = temp(".dat");
        game.save(file.getPath());
        try {
            Progress.open(file.getPath());
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.INVALID_PROFILE_EXTENSION, e);
        }
    }

    @Test
    public void shouldFailSavingAProfileInAMissingFolder() {
        try {
            Progress.save("carpeta_que_no_existe_zzz/perfil.prf", new Profile("Ana"));
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.CANNOT_SAVE, e);
        }
    }

    @Test
    public void shouldNotLetAProfileGoBeyondTheMaxLevel() throws Exception {
        Profile profile = new Profile("Ana");
        profile.addPoints(1000000);
        for (int i = Profile.MIN_LEVEL; i < Profile.MAX_LEVEL; i++) {
            profile.upgrade(Profile.SPEED);
        }
        assertEquals(Profile.MAX_LEVEL, profile.getLevel(Profile.SPEED));
        try {
            profile.upgrade(Profile.SPEED);
            fail();
        } catch (DOPOSoccerException e) {
            expect(DOPOSoccerException.MAX_LEVEL, e);
        }
    }

    @Test
    public void shouldKeepTheMessageOfTheException() {
        assertEquals("hola", new DOPOSoccerException("hola").getMessage());
    }

    @Test
    public void shouldWriteInTheLog() {
        File log = new File(Log.FILE_NAME);
        long before = log.length();
        Log.record(new DOPOSoccerException("prueba del log"));
        assertTrue(log.length() > before);
    }
}