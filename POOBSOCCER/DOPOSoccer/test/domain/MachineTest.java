package domain;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
/**
 * pruebas de las maquinas
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class MachineTest {
    private static final int WIDTH = 880;
    private static final int HEIGHT = 480;
    private Player one;
    private Player two;
    private DOPOSoccer soccer;
 
    @Before
    public void setUp() {
        one = new Player("Uno", "Colombia", 3, 3, 3, 3, new BlueAura());
        two = new Player("Dos", "Brasil", 3, 3, 3, 3, new Tornado());
        soccer = new DOPOSoccer(one, two, WIDTH, HEIGHT, MatchMode.create(true, 99));
    }

    private void play(int steps) {
        for (int i = 0; i < steps; i++) {
            soccer.update();
        }
    }

    @Test
    public void shouldStartWithoutMachine() {
        assertFalse(soccer.isMachine(1));
        assertFalse(soccer.isMachine(2));
        assertNull(soccer.getMachineName());
    }

    @Test
    public void shouldCreateEveryTypeOfMachine() throws DOPOSoccerException {
        for (int i = 0; i < Machines.getNames().size(); i++) {
            soccer.setMachine(Machines.getNames().get(i));
            assertTrue(soccer.isMachine(2));
            assertFalse(soccer.isMachine(1));
            assertEquals(Machines.getNames().get(i), soccer.getMachineName());
        }
    }

    @Test
    public void shouldNotAcceptAMachineThatDoesNotExist() {
        try {
            soccer.setMachine("Loca");
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_MACHINE, e.getMessage());
        }
        assertFalse(soccer.isMachine(2));
    }

    @Test
    public void shouldNotAcceptAnEmptyMachine() {
        try {
            soccer.setMachine(null);
            fail("deberia lanzar la excepcion");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.UNKNOWN_MACHINE, e.getMessage());
        }
    }

    @Test
    public void shouldGoForTheBallWhenOffensive() throws DOPOSoccerException {
        soccer.setMachine("Ofensiva");
        double start = Math.abs(two.getX() - soccer.getBall().getX());
        play(30);
        assertTrue(Math.abs(two.getX() - soccer.getBall().getX()) < start);
    }

    @Test
    public void shouldScoreWhenOffensiveAgainstAQuietRival() throws DOPOSoccerException {
        soccer.setMachine("Ofensiva");
        play(60 * 60);
        assertTrue(two.getGoals() > 0);
    }

    @Test
    public void shouldUseThePowerWhenOffensiveAndTheBarIsFull() throws DOPOSoccerException {
        soccer.setMachine("Ofensiva");
        two.chargePower(two.getMaxPower());
        soccer.update();
        assertTrue(two.isPowerArmed());
    }

    @Test
    public void shouldStayNearItsGoalWhenDefensive() throws DOPOSoccerException {
        soccer.setMachine("Defensiva");
        play(200);
        assertTrue(two.getX() > WIDTH * 3 / 4);
    }

    @Test
    public void shouldGoForTheBallWhenDefensiveAndTheBallIsInItsHalf() throws DOPOSoccerException {
        soccer.setMachine("Defensiva");
        play(200);
        soccer.getBall().place(WIDTH * 0.6, 12);
        double start = two.getX();
        play(20);
        assertTrue(two.getX() < start);
    }

    @Test
    public void shouldKnowWhereTheBallIsWhenDefensive() {
        DefensiveMachine machine = new DefensiveMachine();
        soccer.getBall().place(WIDTH - 100, 12);
        assertTrue(machine.isBallInMyHalf(soccer, 2));
        assertFalse(machine.isBallInMyHalf(soccer, 1));
    }

    @Test
    public void shouldAttackWhenExpertAndNotWinning() throws DOPOSoccerException {
        ExpertMachine machine = new ExpertMachine();
        machine.play(soccer, 2);
        assertTrue(machine.isAttacking());
    }

    @Test
    public void shouldDefendWhenExpertAndWinning() {
        ExpertMachine machine = new ExpertMachine();
        two.addGoal();
        machine.play(soccer, 2);
        assertFalse(machine.isAttacking());
    }

    @Test
    public void shouldAttackWhenExpertAndTheRivalIsStunned() {
        ExpertMachine machine = new ExpertMachine();
        two.addGoal();
        one.receiveDamage(one.getMaxLife());
        machine.play(soccer, 2);
        assertTrue(machine.isAttacking());
    }

    @Test
    public void shouldPlayAMatchWhenExpert() throws DOPOSoccerException {
        soccer.setMachine("Experta");
        play(60 * 60);
        assertTrue(two.getGoals() > 0);
    }

    @Test
    public void shouldDoRandomThingsWhenClumsy() {
        ClumsyMachine machine = new ClumsyMachine();
        boolean[] seen = new boolean[5];
        for (int i = 0; i < 3000; i++) {
            machine.play(soccer, 2);
            soccer.update();
            seen[machine.getAction()] = true;
        }
        for (int i = 0; i < seen.length; i++) {
            assertTrue(seen[i]);
        }
    }

    @Test
    public void shouldStayInsideTheFieldWhenClumsy() throws DOPOSoccerException {
        soccer.setMachine("Torpe");
        for (int i = 0; i < 3000; i++) {
            soccer.update();
            assertTrue(two.getX() >= 0);
            assertTrue(two.getX() <= WIDTH);
        }
    }

    @Test
    public void shouldPlayWithTheLeftPlayerToo() {
        OffensiveMachine machine = new OffensiveMachine();
        double start = Math.abs(one.getX() - soccer.getBall().getX());
        for (int i = 0; i < 30; i++) {
            machine.play(soccer, 1);
            soccer.update();
        }
        assertTrue(Math.abs(one.getX() - soccer.getBall().getX()) < start);
    }

    @Test
    public void shouldJumpWhenTheBallIsHighAndClose() {
        OffensiveMachine machine = new OffensiveMachine();
        soccer.getBall().place(two.getX(), 100);
        machine.play(soccer, 2);
        soccer.update();
        assertTrue(two.getHeight() > 0);
    }

    @Test
    public void shouldKickWhenTheBallIsLowAndInFront() {
        OffensiveMachine machine = new OffensiveMachine();
        soccer.getBall().place(two.getX() - 30, 12);
        machine.play(soccer, 2);
        assertTrue(two.isKicking());
    }

    @Test
    public void shouldNotBeMovedByTheKeysOfPlayerTwo() throws DOPOSoccerException {
        soccer.setMachine("Defensiva");
        assertTrue(soccer.isMachine(2));
        assertEquals(WIDTH, soccer.getWidth());
    }
}