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

    /**
     * borra los archivos que crean las pruebas
     */
    @After
    public void tearDown() {
        new File(FILE).delete();
    }

    /**
     * crea un partido con dos jugadores
     * @return el partido creado
     */
    private DOPOSoccer createMatch() {
        Player one = new Player("Son", "Corea del Sur", 5, 5, 5, 5, new BlueAura());
        Player two = new Player("Osimhen", "Nigeria", 5, 5, 5, 5, new Tornado());
        return new DOPOSoccer(one, two, 800, 400, true, 3);
    }

    /**
     * al abrir una partida guardada debe tener los mismos datos
     * @throws Exception si falla guardar o abrir
     */
    @Test
    public void shouldOpenWhatWasSaved() throws Exception {
        DOPOSoccer soccer = createMatch();
        soccer.save(FILE);
        DOPOSoccer opened = DOPOSoccer.open(FILE);
        assertEquals("Son", opened.getPlayerOne().getName());
        assertEquals("Nigeria", opened.getPlayerTwo().getCountry());
        assertEquals(soccer.getWidth(), opened.getWidth());
        assertTrue(opened.isByGoals());
        assertEquals(soccer.getGoalTarget(), opened.getGoalTarget());
    }

    /**
     * la maquina asignada debe seguir despues de abrir la partida
     * @throws Exception si falla guardar o abrir
     */
    @Test
    public void shouldKeepTheMachine() throws Exception {
        DOPOSoccer soccer = createMatch();
        soccer.setMachine("Experta");
        soccer.save(FILE);
        assertTrue(DOPOSoccer.open(FILE).isMachine(2));
    }

    /**
     * abrir un archivo que no existe debe lanzar la excepcion
     */
    @Test
    public void shouldNotOpenMissingFile() {
        try {
            DOPOSoccer.open("noexiste.dat");
            fail("debia lanzar excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.FILE_NOT_FOUND, e.getMessage());
        }
    }

    /**
     * abrir un archivo que no es una partida debe lanzar la excepcion
     * @throws Exception si no se puede crear el archivo
     */
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

    /**
     * guardar en una carpeta que no existe debe lanzar la excepcion
     */
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