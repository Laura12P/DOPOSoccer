package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import domain.DOPOSoccer;
import domain.Player;
import domain.DOPOSoccerException;
import domain.Log;
/**
 * es la pantalla del juego
 * el tiempo
 * el marcador
 * y el boton de menu
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class GamePanel extends JPanel implements ActionListener {
    private MainWindow window;
    private JLabel time;
    private JLabel scoreboard;
    private JButton menuButton;
    private FieldPanel field;
        private static final String CONTROLS_TEXT = "Jugador 1\nMover: A / D\nSaltar: W\nPatear: S\nPoder: Q\n\n"
            + "Jugador 2\nMover: ← /→\nSaltar: ↑\n"
            + "Patear: ↓ \nPoder: Barra espaciadora";

    private JLabel infoOne;
    private JLabel infoTwo;
    private DOPOSoccer soccer;
    private Timer timer;
    /**
     * CONSTRUCTOR
     * crea la pantalla del partido
     * @param window es la ventana principal
     */
    public GamePanel(MainWindow window) {
        this.window = window;
        prepareElements();
        prepareActions();
    }
    /**
     * es la barra del tiempo
     * con el marcador
     * y el boton de menu
     * y prepara el resto de la cancha
     */
    private void prepareElements() {
        setLayout(new BorderLayout());
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.BLACK);
        bar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        time = new JLabel("Tiempo: 1:00");
        time.setFont(new Font("Arial", Font.BOLD, 18));
        time.setForeground(Color.WHITE);
        bar.add(time, BorderLayout.WEST);
        scoreboard = new JLabel("0 - 0", SwingConstants.CENTER);
        scoreboard.setFont(new Font("Arial", Font.BOLD, 28));
        scoreboard.setForeground(Color.WHITE);
        bar.add(scoreboard, BorderLayout.CENTER);
        menuButton = new JButton("Menu");
        menuButton.setFocusable(false);
        bar.add(menuButton, BorderLayout.EAST);
        add(bar, BorderLayout.NORTH);
        field = new FieldPanel(this);
        add(field, BorderLayout.CENTER);
        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(Color.BLACK);
        info.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        infoOne = new JLabel(" ");
        infoOne.setFont(new Font("Arial", Font.BOLD, 16));
        infoOne.setForeground(Color.WHITE);
        infoTwo = new JLabel(" ");
        infoTwo.setFont(new Font("Arial", Font.BOLD, 16));
        infoTwo.setForeground(Color.WHITE);
        info.add(infoOne, BorderLayout.WEST);
        info.add(infoTwo, BorderLayout.EAST);
        add(info, BorderLayout.SOUTH);
        timer = new Timer(1000, this);
    }

    /**
     * es el que registra el panel cuando se le da click
     * 
     */
    private void prepareActions() {
        menuButton.addActionListener(this);
    }

    /**
     * abre el menu de pausa y cada segundo avanza el tiempo del partido
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuButton) {
            pause();
        } else if (e.getSource() == timer) {
            soccer.passSecond();
            showTime();
            if (soccer.isOver()) {
                endMatch();
            }
        }
    }
    
    /**
     * recibe los jugadores del partido y los muestra
     * @param one es el jugador del lado izquierdo
     * @param two es el jugador del lado derecho
     * @param byGoals es true si el partido es por goles o false por tiempo
     * @param amount son los goles para ganar o minutos del partido
     * @param machine es el tipo de maquina del jugador de der
     */
    public void prepareMatch(Player one, Player two, boolean byGoals, int amount, String machine) {
        soccer = new DOPOSoccer(one.copy(), two.copy(), field.getWidth(), field.getFieldHeight(), byGoals, amount);
       if (machine != null) {
            try {
                soccer.setMachine(machine);
            } catch (DOPOSoccerException e) {
                Log.record(e);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
        showTime();
        timer.restart();
        field.setSoccer(soccer);
        updateScoreboard();
    }
       
    

    /**
     * muestra los goles de cada jugador en el marcador y en la barra de abajo
     */
    public void updateScoreboard() {
        Player playerOne = soccer.getPlayerOne();
        Player playerTwo = soccer.getPlayerTwo();
        scoreboard.setText(playerOne.getGoals() + "-" + playerTwo.getGoals());
        infoOne.setText(playerOne.getName() + "-" + playerOne.getCountry() + 
        " Goles " + playerOne.getGoals());
        String machineText = "";
        if (soccer.isMachine(2)) {
            machineText = " (Maquina " + soccer.getMachineName() + ")";
        }
        infoTwo.setText(playerTwo.getName() + "-" + playerTwo.getCountry() + machineText
                + " Goles " + playerTwo.getGoals());
        if (soccer.isOver()) {
            endMatch();
        }
    }

    /**
     * pregunta donde guardar y guarda la partida 
     * 
     */
    private void saveMatch() {
        soccer.setSeries(window.getSeries());
        JFileChooser chooser = new JFileChooser(MainWindow.matchesFolder());
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getPath();
            if (!path.endsWith(".dat")) {
                path = path + ".dat";
            }
            try {
                soccer.save(path);
                JOptionPane.showMessageDialog(this, "Partida guardada");
            } catch (DOPOSoccerException e) {
                Log.record(e);
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    /**
     * sigue una partida que se abrio de un archivo
     * @param loaded es la partida que estaba guardada
     */
    public void loadMatch(DOPOSoccer loaded) {
        soccer = loaded;
        showTime();
        timer.restart();
        field.setSoccer(soccer);
        updateScoreboard();
    }

    /**
     * @return el ancho de la cancha
     */
    public int getFieldWidth() {
        return field.getWidth();
    }

    /**
     * @return la altura de la cancha desde el suelo al techo
     */
    public int getFieldHeight() {
        return field.getFieldHeight();
    }
    /**
     * para los relojes del partido
     */
    public void stopMatch() {
        timer.stop();
        field.stop();
    }
    /**
     * le da el teclado a la cancha para que los jugadores respondan
     */
    public void begin() {
        field.requestFocusInWindow();
    }

    /**
     * termina el partido: para los relojes, dice quien gano y los puntos de cada uno
     * y vuelve al inicio
     */
    private void endMatch() {
        timer.stop();
        field.stop();
        Player winner = soccer.getWinner();
         String result = winner.getName() + " (" + winner.getCountry() + ")   "
                + soccer.getPlayerOne().getGoals() + " - " + soccer.getPlayerTwo().getGoals();
        int number = 2;
        if (winner == soccer.getPlayerOne()) {
            number = 1;
        }
        window.finishMatch(number, result);
    }


    /**
     * muestra en la barra de arriba la meta de goles, el tiempo que queda o el gol de oro
     */
    private void showTime() {
        if (soccer.isByGoals()) {
            time.setText("Meta: " + soccer.getGoalTarget() + " goles");
        } else if (soccer.isGoldenGoal()) {
            time.setText("GOL DE ORO");
        } else {
            int seconds = soccer.getSeconds();
            int rest = seconds % 60;
            if (rest < 10) {
                time.setText("Tiempo: " + seconds / 60 + ":0" + rest);
            } else {
                time.setText("Tiempo: " + seconds / 60 + ":" + rest);
            }
        }
    }


    /**
     * pausa el partido y muestra el menu hasta que se continua o se sale del partido
     * antes de salir pide confirmacion porque se pierde el partido
     */
    private void pause() {
        timer.stop();
        field.stop();
        PauseMenu menu = new PauseMenu(SwingUtilities.getWindowAncestor(this));
        boolean finished = false;
        while (!finished) {
            int answer = menu.showMenu();
            if (answer == PauseMenu.CONTINUE) {
                if (!soccer.isOver()) {
                    timer.start();
                }
                field.resume();
                finished = true;
            } else if (answer == PauseMenu.SAVE) {
                saveMatch();
            } else if (answer == PauseMenu.CONTROLS) {
                JOptionPane.showMessageDialog(this, CONTROLS_TEXT, "Controles",
                        JOptionPane.PLAIN_MESSAGE);
            } else if (JOptionPane.showConfirmDialog(this,
                    "Se perdera el partido actual. Seguro?", "Confirmar",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                finished = true;
                if (answer == PauseMenu.RESTART) {
                    window.restartMatch();
                } else if (answer == PauseMenu.CONFIG) {
                    window.changeConfig();
                } else if (answer == PauseMenu.MODE) {
                    window.showScreen(MainWindow.MODE);
                } else {
                    window.showScreen(MainWindow.START);
                }
            }
        }
    }
}