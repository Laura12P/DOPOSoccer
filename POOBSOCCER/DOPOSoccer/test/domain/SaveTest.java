package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import org.junit.After;
import org.junit.Test;

/**
 * pruebas de guardar y abrir partidas
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class SaveTest {
    private static final String FILE = "prueba.dat";

    @After
    public void tearDown() {
        new File(FILE).delete();
    }
    private DOPOSoccer createMatch() {
        Player one = new Player("Son", "Corea del Sur", 5, 5, 5, 5, new BlueAura());
        Player two = new Player("Osimhen", "Nigeria", 5, 5, 5, 5, new Tornado());
        return new DOPOSoccer(one, two, 800, 400, MatchMode.create(true, 3));
    }

    @Test
    public void shouldOpenWhatWasSaved() throws Exception {
        DOPOSoccer soccer = createMatch();
        soccer.save(FILE);
        DOPOSoccer opened = DOPOSoccer.open(FILE);
        assertEquals("Son", opened.getPlayerOne().getName());
        assertEquals("Nigeria", opened.getPlayerTwo().getCountry());
        assertEquals(soccer.getWidth(), opened.getWidth());
        assertTrue(opened.isByGoals());
        assertEquals(soccer.getAmount(), opened.getAmount());
    }


    @Test
    public void shouldKeepTheMachine() throws Exception {
        DOPOSoccer soccer = createMatch();
        soccer.setMachine("Experta");
        soccer.save(FILE);
        assertTrue(DOPOSoccer.open(FILE).isMachine(2));
    }


    @Test
    public void shouldNotOpenMissingFile() {
        try {
            DOPOSoccer.open("noexiste.dat");
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.FILE_NOT_FOUND, e.getMessage());
        }
    }


    @Test
    public void shouldNotOpenInvalidFile() throws Exception {
        FileWriter writer = new FileWriter(FILE);
        writer.write("esto no es una partida");
        writer.close();
        try {
            DOPOSoccer.open(FILE);
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_FILE, e.getMessage());
        }
    }

    @Test
    public void shouldNotSaveInMissingFolder() {
        try {
            createMatch().save("carpetaquenoexiste/prueba.dat");
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CANNOT_SAVE, e.getMessage());
        }
    }
}