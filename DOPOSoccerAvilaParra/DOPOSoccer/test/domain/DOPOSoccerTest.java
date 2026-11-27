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

    private void passSecondsOn(DOPOSoccer game, int amount) {
        for (int i = 0; i < amount; i++) {
            game.passSecond();
        }
    }

    @Test
    public void shouldStartWithOneMinuteAndNoGoals() {
        assertEquals(1, soccer.getAmount());
        assertEquals(0, one.getGoals());
        assertEquals(0, two.getGoals());
        assertFalse(soccer.isOver());
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
    public void shouldNotEndBeforeTheTimeIsUp() {
        soccer.getBall().place(30, 50);
        soccer.update();
        passSeconds(59);
        assertFalse(soccer.isOver());
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
        assertSame(one, soccer.getWinner());
        assertSame(two, soccer.getLoser());
    }

    @Test
    public void shouldNotEndWhenTiedAtTheEndBecauseOfTheGoldenGoal() {
        passSeconds(60);
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
        one.chargePower(100);
        one.armPower();
        assertEquals(0, one.getPowerBar());
        soccer.passSecond();
        assertEquals(10, one.getPowerBar());
    }

    @Test
    public void shouldStayOverAfterTheEnd() {
        soccer.getBall().place(30, 50);
        soccer.update();
        passSeconds(60);
        assertTrue(soccer.isOver());
        passSeconds(10);
        assertTrue(soccer.isOver());
        assertSame(two, soccer.getWinner());
    }

    @Test
    public void shouldPlayTheMinutesChosen() {
        DOPOSoccer longmatch = new DOPOSoccer(one, two, WIDTH, HEIGHT, new TimeMode(3));
        assertEquals(3, longmatch.getAmount());
        assertFalse(longmatch.isByGoals());
        longmatch.getBall().place(30, 50);
        longmatch.update();
        passSecondsOn(longmatch, 60);
        assertFalse(longmatch.isOver());
        passSecondsOn(longmatch, 120);
        assertTrue(longmatch.isOver());
    }

    @Test
    public void shouldBeAMatchByGoals() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, new GoalsMode(3));
        assertTrue(goals.isByGoals());
        assertEquals(3, goals.getAmount());
        assertFalse(goals.isOver());
    }

    @Test
    public void shouldNotRunOutOfTimeWhenTheMatchIsByGoals() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, new GoalsMode(3));
        for (int i = 0; i < 500; i++) {
            goals.passSecond();
        }
        assertFalse(goals.isOver());
    }

    @Test
    public void shouldEndWhenSomeoneReachesTheGoalsToWin() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, new GoalsMode(2));
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
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, new GoalsMode(1));
        goals.getBall().place(30, 50);
        goals.update();
        assertEquals(100, two.getPoints());
        assertEquals(25, one.getPoints());
    }

    @Test
    public void shouldGivePointsOnlyOnce() {
        DOPOSoccer goals = new DOPOSoccer(one, two, WIDTH, HEIGHT, new GoalsMode(1));
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
        two.applyEffect(new ConfusionEffect(), 600);
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