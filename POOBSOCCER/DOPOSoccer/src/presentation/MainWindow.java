package presentation;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import domain.Player;

/**
 * Ventana principal de POOBSoccer.
 * Guarda todas las pantallas del juego y muestra una a la vez.
 *
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 * @version 1.0
 */
public class MainWindow extends JFrame {

    public static final String START = "inicio";
    public static final String OPTIONS = "opciones";
    public static final String MODE = "modo";
    public static final String COUNTRY_ONE = "paisUno";
    public static final String COUNTRY_TWO = "paisDos";
    public static final String CONTROLS_ONE = "controlesUno";
    public static final String CONTROLS_TWO = "controlesDos";
    public static final String GAME = "juego";
    public static final String PLAYER_ONE = "jugadorUno";
    public static final String PLAYER_TWO = "jugadorDos";
    public static final String CONFIG = "configuracion";
    public static final String MACHINE = "maquina";
    
    // dimensiones de la ventana
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private CardLayout cards;
    private JPanel container;
    private StartPanel startPanel;
    private ModePanel modePanel;
    private CountryOnePanel countryOnePanel;
    private CountryTwoPanel countryTwoPanel;
    private ControlsPanel controlsPanelOne;
    private ControlsPanel controlsPanelTwo;
    private GamePanel gamePanel;
    private PlayerOnePanel playerOnePanel;
    private PlayerTwoPanel playerTwoPanel;
    private Player playerOne;
    private Player playerTwo;
    private ConfigPanel configPanel;
    private boolean lastByGoals;
    private int lastAmount;
    private int lastWidth;
    private int lastHeight;
    private String lastControls;
    private boolean lastFullScreen;
    private MachinePanel machinePanel;
    private String machine;
    private String currentScreen;

    /**
     * Crea la ventana con las pantallas.
     */
    public MainWindow() {
        super("POOBSoccer");
        prepareElements();
        prepareActions();
    }

    /**
     * Define el tamano de la ventana y le agrega las pantallas.
     */
    private void prepareElements() {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        cards = new CardLayout();
        container = new JPanel(cards);
        startPanel = new StartPanel(this);
        container.add(startPanel, START);
        modePanel = new ModePanel(this);
        container.add(modePanel, MODE);
        countryOnePanel = new CountryOnePanel(this);
        container.add(countryOnePanel, COUNTRY_ONE);
        countryTwoPanel = new CountryTwoPanel(this, false);
        container.add(countryTwoPanel, COUNTRY_TWO);
        playerTwoPanel = new PlayerTwoPanel(this, false);
        container.add(playerTwoPanel, PLAYER_TWO);
        controlsPanelOne = new ControlsPanel(this, false);
        container.add(controlsPanelOne, CONTROLS_ONE);
        controlsPanelTwo = new ControlsPanel(this, true);
        container.add(controlsPanelTwo, CONTROLS_TWO);
        gamePanel = new GamePanel(this);
        container.add(gamePanel, GAME);
        configPanel = new ConfigPanel(this);
        container.add(configPanel, CONFIG);
        add(container);
        cards.show(container, START);
        currentScreen = START;
        playerOnePanel = new PlayerOnePanel(this);
        container.add(playerOnePanel, PLAYER_ONE);
        machinePanel = new MachinePanel(this);
        container.add(machinePanel, MACHINE);
    }

    /**
     * Hace que al dar clic en la X se cierre el juego.
     */
    private void prepareActions() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Cambia la pantalla que se esta mostrando.
     *
     * @param screen nombre de la pantalla
     */
    public void showScreen(String screen) {
        leaveFullScreen();
        if (getWidth() != WIDTH || getHeight() != HEIGHT) {
            setSize(WIDTH, HEIGHT);
            setLocationRelativeTo(null);
        }
        cards.show(container, screen);
        currentScreen = screen;
    }

    /**
     * Pregunta al usuario si quiere salir y, si acepta, cierra el juego.
     */
    public void exit() {
        int answer = JOptionPane.showConfirmDialog(this,
                "seguro que quieres salir de POOBSoccer?", "Salir",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Punto de entrada del programa.
     *
     * @param args no se estan usando
     */
    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setVisible(true);
    }

    /**
     * Muestra la pantalla de jugadores con los del pais elegido.
     *
     * @param country pais que escogio el jugador
     */
    public void choosePlayerOne(String country) {
        playerOnePanel.loadCountry(country);
        cards.show(container, PLAYER_ONE);
        currentScreen = PLAYER_ONE;
    }

    /**
     * Muestra la pantalla de jugadores con los de los paises elegidos.
     *
     * @param countryOne pais del primero
     * @param countryTwo pais del segundo
     * @param machines true si son maquina contra maquina
     */
    public void choosePlayerTwo(String countryOne, String countryTwo) {
        playerTwoPanel.loadCountries(countryOne, countryTwo);
        cards.show(container, PLAYER_TWO);
        currentScreen = PLAYER_TWO;
    }

    /**
     * Guarda los jugadores que se escogieron para el partido.
     *
     * @param one jugador del lado izquierdo
     * @param two jugador del lado derecho
     */
    public void savePlayers(Player one, Player two) {
        playerOne = one;
        playerTwo = two;
        machine = null;
    /**
     * guarda el tipo de maquina que va a manejar el jugador de la der
     * @param profile es el tipo de maquina que se escogio
     */
    }
    public void saveMachine(String profile) {
        machine = profile;
    }
    /**
     * retorna el tipo de maquina que se escogio
     */
    public String getMachine() {
        return machine;
    }
    /**
     * Muestra la pantalla de configuracion del partido.
     *
     * @param backScreen pantalla de controles a la que vuelve el boton volver
     */
    public void showConfig(String backScreen) {
        lastControls = backScreen;
        configPanel.setBackScreen(backScreen);
        showScreen(CONFIG);
    }

    /**
     * Vuelve a la configuracion desde el partido.
     */
    public void changeConfig() {
        showConfig(lastControls);
    }

    /**
     * Prepara el partido con copias de los jugadores y muestra la cancha.
     *
     * @param byGoals true si el partido es por goles, false si es por tiempo
     * @param amount goles para ganar, o minutos del partido
     * @param width es el anchp de la ventana
     * @param height es el alto de la ventana
     * @param fullScreen s true si el partido se juega con pantalla completa
     */
    public void startMatch(boolean byGoals, int amount, int width, int height, boolean fullScreen) {
        lastByGoals = byGoals;
        lastAmount = amount;
        lastWidth = width;
        lastHeight = height;
        lastFullScreen = fullScreen;
        if (fullScreen) {
            enterFullScreen();
        } else {
            leaveFullScreen();
            setSize(width, height);
            setLocationRelativeTo(null);
        }
        cards.show(container, GAME);
        currentScreen = GAME;
        validate();
        gamePanel.prepareMatch(playerOne.copy(), playerTwo.copy(), byGoals, amount, machine);
        gamePanel.begin();
    }

    /**
     * Empieza de nuevo el partido con los mismos jugadores y la misma configuracion.
     */
    public void restartMatch() {
        startMatch(lastByGoals, lastAmount, lastWidth, lastHeight, lastFullScreen);
    }
    /**
     * pone la ventana sin bordes y fullscreen
     */
    private void enterFullScreen() {
        if (!isUndecorated()) {
            boolean visible = isVisible();
            dispose();
            setUndecorated(true);
            setVisible(visible);
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screen.width, screen.height);
    }
    /**
     * si la ventana esta en fullscreen vuelve a ponerla normal
     */
    private void leaveFullScreen() {
        if (isUndecorated()){
            boolean visible = isVisible();
            dispose();
            setUndecorated(false);
            setSize(WIDTH,HEIGHT);
            setLocationRelativeTo(null);
            setVisible(visible);
        }
    }
    /**
     * retorna el nombre de la pantalla que se esta mostrando
     */
    public String getCurrentScreen(){
        return currentScreen;
    }
    /**
     * retorna true si la ventana esta en pantalla completa
     */
    public boolean isFullScreen() {
        return isUndecorated();
    }
}