package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * menu de pausa del partido
 * muestra las opciones en vertical y devuelve la que se escogio
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class PauseMenu extends JDialog implements ActionListener {
    public static final int CONTINUE = 0;
    public static final int RESTART = 1;
    public static final int CONFIG = 2;
    public static final int CONTROLS = 3;
    public static final int MODE = 4;
    public static final int HOME = 5;
    private static final String[] OPTIONS = {"Continuar", "Reiniciar partido", "Configuracion",
        "Controles", "Modo de juego", "Inicio"};
    private static final Color GOLD = new Color(230, 190, 40);

    private JButton[] buttons;
    private int choice;

    /**
     * CONSTRUCTOR
     * crea el menu de pausa
     * @param owner es la ventana sobre la que se muestra
     */
    public PauseMenu(Window owner) {
        super(owner, "Pausa", Dialog.ModalityType.APPLICATION_MODAL);
        prepareElements();
        prepareActions();
    }

    /**
     * ubica el titulo y los botones, uno debajo del otro
     */
    private void prepareElements() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(40, 120, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 25, 40));
        JLabel title = new JLabel("PAUSA", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        title.setForeground(GOLD);
        panel.add(title, BorderLayout.NORTH);
        JPanel list = new JPanel(new GridLayout(OPTIONS.length, 1, 0, 10));
        list.setOpaque(false);
        buttons = new JButton[OPTIONS.length];
        for (int i = 0; i < OPTIONS.length; i++) {
            buttons[i] = new JButton(OPTIONS[i]);
            buttons[i].setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            buttons[i].setForeground(Color.BLACK);
            if (i == CONTINUE) {
                buttons[i].setBackground(GOLD);
            } else {
                buttons[i].setBackground(Color.WHITE);
            }
            buttons[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            buttons[i].setFocusPainted(false);
            buttons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            list.add(buttons[i]);
        }
        panel.add(list, BorderLayout.CENTER);
        setContentPane(panel);
        setSize(340, 480);
        setResizable(false);
    }

    /**
     * registra los botones para que respondan al click
     */
    private void prepareActions() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(this);
        }
    }

    /**
     * muestra el menu y espera a que se escoja una opcion
     * si se cierra con la X cuenta como continuar
     * @return es la opcion escogida, una de las constantes de esta clase
     */
    public int showMenu() {
        choice = CONTINUE;
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return choice;
    }

    /**
     * guarda la opcion escogida y cierra el menu
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            if (e.getSource() == buttons[i]) {
                choice = i;
                setVisible(false);
            }
        }
    }
}