package domain;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import org.junit.After;
import org.junit.Test;

/**
 * pruebas de guardar y abrir el progreso de una persona: sus puntos y estadisticas
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ProgressTest {
    private static final String FILE = "pruebaprogreso.prf";


    @After
    public void tearDown() {
        new File(FILE).delete();
    }

    @Test
    public void shouldOpenTheSavedPointsAndStats() throws Exception {
        Profile laura = new Profile("Laura");
        laura.addPoints(DOPOSoccer.WIN_POINTS * 6);
        laura.upgrade(Profile.JUMP);
        Progress.save(FILE, laura);
        Profile opened = Progress.open(FILE);
        assertEquals("Laura", opened.getName());
        assertEquals(DOPOSoccer.WIN_POINTS, opened.getPoints());
        assertEquals(2, opened.getLevel(Profile.JUMP));
        assertEquals(1, opened.getLevel(Profile.SPEED));
        assertEquals(1000, opened.getUpgradeCost());
    }

    @Test
    public void shouldNotOpenASavedMatchAsAProfile() throws Exception {
        Player one = new Player("Son", "Corea del Sur", 1, 1, 1, 1, new BlueAura());
        Player two = new Player("Osimhen", "Nigeria", 1, 1, 1, 1, new Tornado());
        new DOPOSoccer(one, two, 880, 480).save("pruebapartida.dat");
        try {
            Progress.open("pruebapartida.dat");
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.INVALID_PROFILE_EXTENSION, e.getMessage());
        }
        new File("pruebapartida.dat").delete();
    }


    @Test
    public void shouldBuildTheFileNameOfAProfile() {
        assertEquals("perfiles/perfil_laura.prf", Progress.fileName("Laura"));
        assertEquals("perfiles/perfil_laura2.prf", Progress.fileName(" LAURA 2! "));
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
            Progress.open("noexisteprogreso.prf");
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


    @Test
    public void shouldNotSaveInMissingFolder() {
        try {
            Progress.save("carpetaquenoexiste/progreso.prf", new Profile("Laura"));
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.CANNOT_SAVE, e.getMessage());
        }
    }
}