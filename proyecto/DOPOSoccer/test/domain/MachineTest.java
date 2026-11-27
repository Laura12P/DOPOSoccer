package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de las maquinas, juegan siempre con el jugador 2 (x = 675, ataca a la izq)
 */
public class MachineTest {
    private Player one;
    private Player two;
    private DOPOSoccer game;

    @Before
    public void setUp() {
        one = new Player("A", "P", 1, 1, 1, 1, new BlueAura());
        two = new Player("B", "P", 1, 1, 1, 1, new BlueAura());
        game = new DOPOSoccer(one, two, 900, 500);
    }

    @Test
    public void shouldHaveNameAndDescription() {
        Machine[] all = {new ClumsyMachine(), new OffensiveMachine(),
            new DefensiveMachine(), new ExpertMachine()};
        for (int i = 0; i < all.length; i++) {
            assertNotNull(all[i].getName());
            assertFalse(all[i].getDescription().equals(""));
        }
    }

    @Test
    public void shouldCopyIntoANewMachineOfTheSameType() {
        Machine[] all = {new ClumsyMachine(), new OffensiveMachine(),
            new DefensiveMachine(), new ExpertMachine()};
        for (int i = 0; i < all.length; i++) {
            Machine copy = all[i].copy();
            assertNotSame(all[i], copy);
            assertSame(all[i].getClass(), copy.getClass());
            assertEquals(all[i].getName(), copy.getName());
        }
    }

    @Test
    public void shouldKnowWhoIsWho() {
        Machine machine = new OffensiveMachine();
        assertSame(one, machine.getMe(game, 1));
        assertSame(two, machine.getMe(game, 2));
        assertSame(two, machine.getRival(game, 1));
        assertSame(one, machine.getRival(game, 2));
        assertEquals(1, machine.getDirection(1));
        assertEquals(-1, machine.getDirection(2));
        assertEquals(0, machine.getOwnGoal(game, 1));
        assertEquals(900, machine.getOwnGoal(game, 2));
    }

    @Test
    public void shouldGoBehindTheBallWhenOffensive() {
        game.getBall().place(500, 200);
        new OffensiveMachine().play(game, 2);
        assertTrue(two.getX() < 675);
    }

    @Test
    public void shouldKickWhenTheBallIsAtItsFeet() {
        game.getBall().place(655, 20);
        new OffensiveMachine().play(game, 2);
        assertTrue(two.isKicking());
    }

    @Test
    public void shouldJumpWhenTheBallIsInTheAir() {
        game.getBall().place(675, 100);
        new OffensiveMachine().play(game, 2);
        two.update(25, 875);
        assertTrue(two.getHeight() > 0);
    }

    @Test
    public void shouldArmThePowerWhenItsReady() {
        two.chargePower(100);
        new OffensiveMachine().play(game, 2);
        assertTrue(two.isPowerArmed());
    }

    @Test
    public void shouldNotArmThePowerWithAnIncompleteBar() {
        two.chargePower(50);
        new OffensiveMachine().play(game, 2);
        assertFalse(two.isPowerArmed());
    }

    @Test
    public void shouldKnowIfTheBallIsInItsHalf() {
        DefensiveMachine machine = new DefensiveMachine();
        game.getBall().place(800, 100);
        assertTrue(machine.isBallInMyHalf(game, 2));
        assertFalse(machine.isBallInMyHalf(game, 1));
        game.getBall().place(100, 100);
        assertFalse(machine.isBallInMyHalf(game, 2));
        assertTrue(machine.isBallInMyHalf(game, 1));
    }

    @Test
    public void shouldGoBackToItsGoalWhenTheBallIsFar() {
        game.getBall().place(100, 200);
        new DefensiveMachine().play(game, 2);
        assertTrue(two.getX() > 675);
    }

    @Test
    public void shouldGoForTheBallWhenItIsInItsHalf() {
        game.getBall().place(500, 200);
        new DefensiveMachine().play(game, 2);
        assertTrue(two.getX() < 675);
    }

    @Test
    public void shouldAttackWhenTied() {
        ExpertMachine machine = new ExpertMachine();
        machine.play(game, 2);
        assertTrue(machine.isAttacking());
    }

    @Test
    public void shouldDefendWhenWinning() {
        ExpertMachine machine = new ExpertMachine();
        two.addGoal();
        machine.play(game, 2);
        assertFalse(machine.isAttacking());
    }

    @Test
    public void shouldAttackWhenLosing() {
        ExpertMachine machine = new ExpertMachine();
        one.addGoal();
        machine.play(game, 2);
        assertTrue(machine.isAttacking());
    }

    @Test
    public void shouldAttackWhenTheRivalIsStunned() {
        ExpertMachine machine = new ExpertMachine();
        two.addGoal();
        one.receiveDamage(1000);
        machine.play(game, 2);
        assertTrue(machine.isAttacking());
    }

    @Test
    public void shouldPickAnActionAndKeepItForAWhile() {
        ClumsyMachine machine = new ClumsyMachine();
        machine.play(game, 2);
        int action = machine.getAction();
        assertTrue(action >= 0 && action < 5);
        for (int i = 0; i < 9; i++) {
            machine.play(game, 2);
            assertEquals(action, machine.getAction());
        }
    }

    @Test
    public void shouldAlwaysPickAValidAction() {
        ClumsyMachine machine = new ClumsyMachine();
        for (int i = 0; i < 500; i++) {
            machine.play(game, 2);
            assertTrue(machine.getAction() >= 0 && machine.getAction() < 5);
        }
    }
}