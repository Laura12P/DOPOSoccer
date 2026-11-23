package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import org.junit.After;
import org.junit.Test;

/**
 * pruebas de guardar y abrir el progreso de los jugadores
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ProgressTest {
    private static final String FILE = "pruebaprogreso.dat";

    @After
    public void tearDown() {
        new File(FILE).delete();
    }

    @Test
    public void shouldOpenTheSavedPoints() throws Exception {
        Player one = new Player("Son", "Corea del Sur", 1, 1, 1, 1, new BlueAura());
        Player two = new Player("Osimhen", "Nigeria", 2, 3, 4, 5, new Tornado());
        one.addPoints(DOPOSoccer.WIN_POINTS);
        two.addPoints(DOPOSoccer.LOSE_POINTS);
        Progress.save(FILE, new Player[] {one, two});
        Player[] opened = Progress.open(FILE);
        assertEquals(2, opened.length);
        assertEquals("Son", opened[0].getName());
        assertEquals(DOPOSoccer.WIN_POINTS, opened[0].getPoints());
        assertEquals("Nigeria", opened[1].getCountry());
        assertEquals(DOPOSoccer.LOSE_POINTS, opened[1].getPoints());
        assertEquals(4, opened[1].getJump());
    }

    @Test
    public void shouldBuildTheFileNameOfAProfile() {
        assertEquals("perfil_laura.dat", Progress.fileName("Laura"));
        assertEquals("perfil_laura2.dat", Progress.fileName(" LAURA 2! "));
        assertEquals(Progress.fileName("Laura"), Progress.fileName("laura"));
    }

    @Test
    public void shouldNotAcceptAProfileWithoutLetters() {
        assertNull(Progress.fileName("   "));
        assertNull(Progress.fileName("!!"));
    }

    @Test
    public void shouldNotOpenMissingFile() {
        try {
            Progress.open("noexisteprogreso.dat");
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.FILE_NOT_FOUND, e.getMessage());
        }
    }

    @Test
    public void shouldNotOpenInvalidFile() throws Exception {
        FileWriter writer = new FileWriter(FILE);
        writer.write("esto no es un progreso");
        writer.close();
        try {
            Progress.open(FILE);
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_FILE, e.getMessage());
        }
    }

    /**
     * guardar en una carpeta que no existe debe lanzar la excepcion
     */
    @Test
    public void shouldNotSaveInMissingFolder() {
        try {
            Progress.save("carpetaquenoexiste/progreso.dat", new Player[0]);
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CANNOT_SAVE, e.getMessage());
        }
    }
}
