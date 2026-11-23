package domain;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * pruebas de la fachada DOPOSoccer
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class DOPOSoccerTest {
    private static final int WIDTH = 880;
    private static final int HEIGHT = 480;
    private static final double DELTA = 0.01;
    private Player one;
    private Player two;
    private DOPOSoccer soccer;

    @Before
    public void setUp() {
        one = new Player("Uno", "Colombia", 3, 3, 3, 3, new BlueAura());
        two = new Player("Dos", "Brasil", 3, 3, 3, 3, new Tornado());
        soccer = new DOPOSoccer(one, two, WIDTH, HEIGHT);
    }


    private void passSeconds(int amount) {
        for (int i = 0; i < amount; i++) {
            soccer.passSecond();
        }
    }

    @Test
    public void shouldStartWithSixtySecondsAndNoGoals() {
        assertEquals(60, soccer.getSeconds());
        assertEquals(0, one.getGoals());
        assertEquals(0, two.getGoals());
        assertFalse(soccer.isOver());
        assertFalse(soccer.isGoldenGoal());
        assertFalse(soccer.isByGoals());
    }

    @Test
    public void shouldStartWithEmptyPowerBarsAndFullLife() {
        assertEquals(0, one.getPowerBar());
        assertEquals(0, two.getPowerBar());
        assertEquals(one.getMaxLife(), one.getLife());
        assertEquals(two.getMaxLife(), two.getLife());
    }

    @Test
    public void shouldGivePlayerTwoAGoalWhenBallEntersLeftGoal() {
        soccer.getBall().place(30, 50);
        assertTrue(soccer.update());
        assertEquals(1, two.getGoals());
        assertEquals(0, one.getGoals());
    }

    @Test
    public void shouldGivePlayerOneAGoalWhenBallEntersRightGoal() {
        soccer.getBall().place(WIDTH - 30, 50);
        assertTrue(soccer.update());
        assertEquals(1, one.getGoals());
        assertEquals(0, two.getGoals());
    }

    @Test
    public void shouldNotScoreWhenBallIsInTheMiddle() {
        assertFalse(soccer.update());
        assertEquals(0, one.getGoals());
        assertEquals(0, two.getGoals());
    }

    @Test
    public void shouldNotScoreWhenBallPassesAboveTheGoal() {
        soccer.getBall().place(30, 300);
        assertFalse(soccer.update());
    }

    @Test
    public void shouldPutEverythingBackAfterAGoal() {
        for (int i = 0; i < 20; i++) {
            soccer.moveRight(1);
        }
        soccer.getBall().place(30, 50);
        soccer.update();
        assertEquals(220, one.getX(), DELTA);
        assertEquals(660, two.getX(), DELTA);
        assertEquals(440, soccer.getBall().getX(), DELTA);
    }

    @Test
    public void shouldPutThePlayersInTheMiddleOfEachHalfInAnyFieldSize() {
        DOPOSoccer big = new DOPOSoccer(one, two, 1000, 600);
        assertEquals(250, one.getX(), DELTA);
        assertEquals(750, two.getX(), DELTA);
        assertEquals(500, big.getBall().getX(), DELTA);
    }

    @Test
    public void shouldGiveAQuarterOfTheBarToWhoReceivesAGoal() {
        soccer.getBall().place(30, 50);
        soccer.update();
        assertEquals(25, one.getPowerBar());
        assertEquals(0, two.getPowerBar());
    }

    @Test
    public void shouldGiveAQuarterOfTheBarToPlayerTwoWhenHeReceivesAGoal() {
        soccer.getBall().place(WIDTH - 30, 50);
        soccer.update();
        assertEquals(25, two.getPowerBar());
        assertEquals(0, one.getPowerBar());
    }

    @Test
    public void shouldLoseOneSecondEachTime() {
        soccer.passSecond();
        soccer.passSecond();
        assertEquals(58, soccer.getSeconds());
    }

    @Test
    public void shouldChargeThePowerBarsEachSecond() {
        passSeconds(3);
        assertEquals(30, one.getPowerBar());
        assertEquals(30, two.getPowerBar());
    }

    @Test
    public void shouldEndWithAWinnerWhenTimeRunsOut() {
        soccer.getBall().place(WIDTH - 30, 50);
        soccer.update();
        passSeconds(60);
        assertTrue(soccer.isOver());
        assertFalse(soccer.isGoldenGoal());
        assertSame(one, soccer.getWinner());
        assertSame(two, soccer.getLoser());
    }

    @Test
    public void shouldStartGoldenGoalWhenTiedAtTheEnd() {
        passSeconds(60);
        assertTrue(soccer.isGoldenGoal());
        assertFalse(soccer.isOver());
    }

    @Test
    public void shouldEndWithTheGoldenGoal() {
        passSeconds(60);
        soccer.getBall().place(30, 50);
        soccer.update();
        assertTrue(soccer.isOver());
        assertSame(two, soccer.getWinner());
    }

    @Test
    public void shouldKeepChargingTheBarsInGoldenGoal() {
        passSeconds(60);
        int before = one.getPowerBar();
        soccer.passSecond();
        assertTrue(one.getPowerBar() > before || one.getPowerBar() == one.getMaxPower());
    }

    @Test
    public void shouldNotLoseTimeAfterTheEnd() {
        soccer.getBall().place(30, 50);
        soccer.update();
        passSeconds(60);
        soccer.passSecond();
        assertEquals(0, soccer.getSeconds());
    }

    @Test
    public void shouldPlayTheMinutesChosen() {
        DOPOSoccer long_match = new DOPOSoccer(one, two, WIDTH, HEIGHT, false, 3);
        assertEquals(180, long_match.getSeconds());
        assertFalse(long_match.isByGoals());
    }

    @Test
    public void shouldBeAMatchByGoals() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, true, 3);
        assertTrue(goals.isByGoals());
        assertEquals(3, goals.getGoalTarget());
        assertFalse(goals.isOver());
    }

    @Test
    public void shouldNotRunOutOfTimeWhenTheMatchIsByGoals() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, true, 3);
        for (int i = 0; i < 500; i++) {
            goals.passSecond();
        }
        assertFalse(goals.isOver());
        assertFalse(goals.isGoldenGoal());
    }

    @Test
    public void shouldEndWhenSomeoneReachesTheGoalsToWin() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, true, 2);
        goals.getBall().place(WIDTH - 30, 50);
        goals.update();
        assertFalse(goals.isOver());
        goals.getBall().place(WIDTH - 30, 50);
        goals.update();
        assertTrue(goals.isOver());
        assertSame(one, goals.getWinner());
    }

    @Test
    public void shouldGiveHundredPointsToTheWinnerAndTwentyFiveToTheLoser() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, true, 1);
        goals.getBall().place(30, 50);
        goals.update();
        assertEquals(100, two.getPoints());
        assertEquals(25, one.getPoints());
    }

    @Test
    public void shouldGivePointsOnlyOnce() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, true, 1);
        goals.getBall().place(30, 50);
        goals.update();
        goals.getBall().place(30, 50);
        goals.update();
        assertEquals(100, two.getPoints());
        assertEquals(25, one.getPoints());
    }

    @Test
    public void shouldNotGivePointsWhileTheMatchIsNotOver() {
        soccer.getBall().place(30, 50);
        soccer.update();
        assertEquals(0, one.getPoints());
        assertEquals(0, two.getPoints());
    }

    @Test
    public void shouldNotLetPlayersGoThroughEachOther() {
        soccer.getBall().place(440, 400);
        for (int i = 0; i < 150; i++) {
            soccer.moveRight(1);
            soccer.moveLeft(2);
            soccer.update();
            assertTrue(two.getX() - one.getX() >= 50);
        }
    }

    @Test
    public void shouldPushRivalWhenWalkingIntoHim() {
        soccer.getBall().place(440, 400);
        double start = one.getX();
        for (int i = 0; i < 150; i++) {
            soccer.moveLeft(2);
            soccer.update();
        }
        assertTrue(one.getX() < start);
    }

    @Test
    public void shouldKickTheBallTowardsTheRivalGoal() {
        soccer.getBall().place(260, 12);
        soccer.kick(1);
        soccer.update();
        soccer.update();
        assertTrue(soccer.getBall().getX() > 260);
        assertEquals(two.getMaxLife(), two.getLife());
    }

    @Test
    public void shouldDamageTheRivalWhenTheKickMissesTheBall() {
        soccer.getBall().place(800, 400);
        for (int i = 0; i < 200; i++) {
            soccer.moveLeft(2);
            soccer.update();
        }
        soccer.kick(2);
        soccer.update();
        assertEquals(one.getMaxLife() - two.getKickDamage(), one.getLife());
    }

    @Test
    public void shouldNotDamageTheRivalWhenFarAway() {
        soccer.getBall().place(800, 400);
        soccer.kick(1);
        soccer.update();
        assertEquals(two.getMaxLife(), two.getLife());
    }

    @Test
    public void shouldInvertTheMovementWhenConfused() {
        two.applyConfusion(600);
        double start = two.getX();
        soccer.moveLeft(2);
        assertTrue(two.getX() > start);
        start = two.getX();
        soccer.moveRight(2);
        assertTrue(two.getX() < start);
    }

    @Test
    public void shouldMoveNormallyWithoutConfusion() {
        double start = two.getX();
        soccer.moveLeft(2);
        assertTrue(two.getX() < start);
    }

    @Test
    public void shouldNotActivateThePowerWithAnIncompleteBar() {
        soccer.activatePower(1);
        assertFalse(one.isPowerArmed());
    }

    @Test
    public void shouldActivateThePowerWithAFullBar() {
        one.chargePower(100);
        soccer.activatePower(1);
        assertTrue(one.isPowerArmed());
        assertEquals(0, one.getPowerBar());
    }

    @Test
    public void shouldLaunchThePowerShotAndHurtTheRival() {
        one.chargePower(100);
        soccer.activatePower(1);
        soccer.getBall().place(255, 15);
        soccer.kick(1);
        for (int i = 0; i < 60; i++) {
            soccer.update();
        }
        assertFalse(one.isPowerArmed());
        assertTrue(two.getLife() < two.getMaxLife());
    }

    @Test
    public void shouldPushTheRivalTowardsHisOwnGoalWithTheBlueAura() {
        one.chargePower(100);
        soccer.activatePower(1);
        soccer.getBall().place(255, 15);
        soccer.kick(1);
        for (int i = 0; i < 80; i++) {
            soccer.update();
        }
        assertTrue(two.getX() > 660);
    }

    @Test
    public void shouldNotHurtTheRivalWhenThePowerShotEntersTheGoal() {
        one.chargePower(100);
        soccer.activatePower(1);
        soccer.getBall().place(255, 15);
        soccer.kick(1);
        two.push(-600, 25, 855);
        for (int i = 0; i < 100; i++) {
            soccer.update();
        }
        assertEquals(1, one.getGoals());
        assertEquals(two.getMaxLife(), two.getLife());
    }
}