package domain;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

/**
 * pruebas de los catalogos y de los poderes
 */
public class CatalogTest {

    @Test
    public void rosterHasFiveCountries() {
        assertEquals(5, Roster.getCountries().size());
    }

    @Test
    public void everyCountryHasTwoPlayers() {
        ArrayList<String> countries = Roster.getCountries();
        for (int i = 0; i < countries.size(); i++) {
            assertEquals(2, Roster.getPlayers(countries.get(i)).size());
        }
    }

    @Test
    public void unknownCountryHasNoPlayers() {
        assertEquals(0, Roster.getPlayers("Narnia").size());
    }

    @Test
    public void everyPlayerHasPowerWithAura() {
        ArrayList<String> countries = Roster.getCountries();
        for (int i = 0; i < countries.size(); i++) {
            ArrayList<Player> players = Roster.getPlayers(countries.get(i));
            for (int j = 0; j < players.size(); j++) {
                int[] aura = players.get(j).getPower().getAura();
                assertNotNull(aura);
                assertEquals(4, aura.length);
            }
        }
    }

    @Test
    public void atRandomReturnsAPlayer() {
        assertNotNull(Roster.atRandom());
    }

    @Test
    public void powerFramesAreSecondsTimesSixty() {
        assertEquals(180, new Lightning().getFrames());
        assertEquals(90, new BlueAura().getFrames());
    }

    @Test
    public void tornadoIsGroundShot() {
        assertTrue(new Tornado().isGroundShot());
        assertFalse(new Lightning().isGroundShot());
    }

    @Test
    public void lightningHitShocksRival() {
        Player user = new Player("A", "P", 1, 1, 1, 1, new Lightning());
        Player rival = new Player("B", "P", 1, 1, 1, 1, new Lightning());
        DOPOSoccer game = new DOPOSoccer(user, rival, 900, 500);
        int life = rival.getLife();
        user.getPower().hit(user, rival, 1, game);
        assertTrue(rival.isShocked());
        assertTrue(rival.getLife() < life);
    }

    @Test
    public void illusionHitSplitsBall() {
        Player user = new Player("A", "P", 1, 1, 1, 1, new Illusion());
        Player rival = new Player("B", "P", 1, 1, 1, 1, new Illusion());
        DOPOSoccer game = new DOPOSoccer(user, rival, 900, 500);
        user.getPower().hit(user, rival, 1, game);
        assertEquals(7, game.getBall().getFakes().size());
    }

    @Test
    public void machinesHasFourWithDescription() {
        assertEquals(4, Machines.getNames().size());
        assertEquals(4, Machines.getDescriptions().size());
    }

    @Test
    public void machinesCreateByName() throws DOPOSoccerException {
        assertEquals("Experta", Machines.create("Experta").getName());
        assertEquals("Torpe", Machines.create("Torpe").getName());
    }

    @Test
    public void machinesCreateGivesNewObjectEachTime() throws DOPOSoccerException {
        assertNotSame(Machines.create("Ofensiva"), Machines.create("Ofensiva"));
    }

    @Test(expected = DOPOSoccerException.class)
    public void machinesUnknownNameThrows() throws DOPOSoccerException {
        Machines.create("NoExiste");
    }
}