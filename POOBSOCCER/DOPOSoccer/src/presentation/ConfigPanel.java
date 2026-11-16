package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/**
 * pantalla donde se configura el partido
 * el tipo, por tiempo con los minutos o por goles con los goles para ganar
 * y la resolucion de la cancha
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ConfigPanel extends JPanel implements ActionListener {
    private static final Color GOLD = new Color(230, 190, 40);
    private static final String[] SIZE_NAMES = {"Pequena", "Mediana", "Grande"};
    private static final int[] WIDTHS = {800, 900, 1100};
    private static final int[] HEIGHTS = {550, 600, 700};
    private static final int MEDIUM = 1;

    private MainWindow window;
    private String backScreen;
    private JLabel title;
    private JToggleButton timeButton;
    private JToggleButton goalsButton;
    private JToggleButton[] sizeButtons;
    private JLabel amountLabel;
    private JSpinner amount;
    private JButton playButton;
    private JButton backButton;

    /**
     * CONSTRUCTOR
     * crea la pantalla de configuracion del partido
     * @param window es la ventana principal
     */
    public ConfigPanel(MainWindow window) {
        this.window = window;
        prepareElements();
        prepareActions();
    }

    /**
     * ubica el titulo, el tipo de partido, la cantidad, la resolucion y los botones
     */
    private void prepareElements() {
        setLayout(new BorderLayout());
        final Image background = new ImageIcon("imagenes/modo.png").getImage();
        JPanel panel = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        title = new JLabel("CONFIGURACION", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 56));
        title.setForeground(GOLD);
        panel.add(title, BorderLayout.NORTH);

        timeButton = new JToggleButton("Por tiempo", true);
        goalsButton = new JToggleButton("Por goles");
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(timeButton);
        typeGroup.add(goalsButton);
        JPanel types = new JPanel(new GridLayout(1, 2, 40, 0));
        types.setOpaque(false);
        types.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        types.add(timeButton);
        types.add(goalsButton);

        amountLabel = new JLabel("Minutos del partido:");
        amountLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        amountLabel.setForeground(Color.WHITE);
        amount = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        amount.setFont(new Font("Arial", Font.BOLD, 26));
        amount.setPreferredSize(new Dimension(90, 45));
        ((JSpinner.DefaultEditor) amount.getEditor()).getTextField()
                .setHorizontalAlignment(SwingConstants.CENTER);
        JPanel quantity = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        quantity.setOpaque(false);
        quantity.add(amountLabel);
        quantity.add(amount);

        sizeButtons = new JToggleButton[SIZE_NAMES.length];
        ButtonGroup sizeGroup = new ButtonGroup();
        JPanel sizes = new JPanel(new GridLayout(1, SIZE_NAMES.length, 20, 0));
        sizes.setOpaque(false);
        sizes.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        for (int i = 0; i < SIZE_NAMES.length; i++) {
            sizeButtons[i] = new JToggleButton("<html><center>" + SIZE_NAMES[i] + "<br>"
                    + WIDTHS[i] + " x " + HEIGHTS[i] + "</center></html>", i == MEDIUM);
            sizeGroup.add(sizeButtons[i]);
            sizes.add(sizeButtons[i]);
        }

        JToggleButton[] all = {timeButton, goalsButton, sizeButtons[0], sizeButtons[1], sizeButtons[2]};
        for (int i = 0; i < all.length; i++) {
            all[i].setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            all[i].setForeground(Color.BLACK);
            all[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            all[i].setContentAreaFilled(false);
            all[i].setOpaque(true);
            all[i].setFocusPainted(false);
            all[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        paintOptions();

        JLabel sizeLabel = new JLabel("Resolucion de la cancha", SwingConstants.CENTER);
        sizeLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        sizeLabel.setForeground(Color.WHITE);
        JPanel sizeBlock = new JPanel(new BorderLayout(0, 8));
        sizeBlock.setOpaque(false);
        sizeBlock.add(sizeLabel, BorderLayout.NORTH);
        sizeBlock.add(sizes, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 15));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        center.add(types);
        center.add(quantity);
        center.add(sizeBlock);
        panel.add(center, BorderLayout.CENTER);

        backButton = new JButton("Volver");
        backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton = new JButton("Jugar");
        playButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        playButton.setBackground(GOLD);
        playButton.setForeground(Color.BLACK);
        playButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        playButton.setFocusPainted(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setPreferredSize(new Dimension(140, 45));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(backButton, BorderLayout.WEST);
        bottom.add(playButton, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
    }

    /**
     * registra los botones para que respondan al click
     */
    private void prepareActions() {
        timeButton.addActionListener(this);
        goalsButton.addActionListener(this);
        for (int i = 0; i < sizeButtons.length; i++) {
            sizeButtons[i].addActionListener(this);
        }
        playButton.addActionListener(this);
        backButton.addActionListener(this);
    }

    /**
     * pinta de dorado lo elegido, el tipo de partido y la resolucion, y de blanco lo demas
     */
    private void paintOptions() {
        if (timeButton.isSelected()) {
            timeButton.setBackground(GOLD);
            goalsButton.setBackground(Color.WHITE);
        } else {
            timeButton.setBackground(Color.WHITE);
            goalsButton.setBackground(GOLD);
        }
        for (int i = 0; i < sizeButtons.length; i++) {
            if (sizeButtons[i].isSelected()) {
                sizeButtons[i].setBackground(GOLD);
            } else {
                sizeButtons[i].setBackground(Color.WHITE);
            }
        }
    }

    /**
     * guarda a que pantalla vuelve el boton volver
     * @param screen es el nombre de la pantalla de controles de la que se vino
     */
    public void setBackScreen(String screen) {
        backScreen = screen;
    }

    /**
     * cambia lo elegido, empieza el partido o vuelve
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timeButton) {
            amountLabel.setText("Minutos del partido:");
            amount.setValue(1);
            paintOptions();
        } else if (e.getSource() == goalsButton) {
            amountLabel.setText("Goles para ganar:");
            amount.setValue(3);
            paintOptions();
        } else if (e.getSource() == playButton) {
            int value = ((Number) amount.getValue()).intValue();
            int size = MEDIUM;
            for (int i = 0; i < sizeButtons.length; i++) {
                if (sizeButtons[i].isSelected()) {
                    size = i;
                }
            }
            window.startMatch(goalsButton.isSelected(), value, WIDTHS[size], HEIGHTS[size]);
        } else if (e.getSource() == backButton) {
            window.showScreen(backScreen);
        } else {
            paintOptions();
        }
    }
}