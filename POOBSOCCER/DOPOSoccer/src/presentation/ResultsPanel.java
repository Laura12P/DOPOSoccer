package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import domain.Profile;
import domain.Series;

/**
 * pantalla que aparece al final de cada partido de la serie
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class ResultsPanel extends JPanel implements ActionListener {
    private static final Color GOLD = new Color(230, 190, 40);
    private static final String[] HEADERS = {"Jugador", "Partidos ganados",
        "Puntos de la serie", "Puntos para mejoras"};

    private MainWindow window;
    private JLabel title;
    private JLabel matchResult;
    private JLabel seriesInfo;
    private JLabel[][] cells;
    private JButton nextButton;
    private JButton restartButton;
    private JButton changeButton;
    private JButton homeButton;

    /**
     * CONSTRUCTOR
     * crea la pantalla de resultados
     * @param window es la ventana principal
     */
    public ResultsPanel(MainWindow window) {
        this.window = window;
        prepareElements();
        prepareActions();
    }

    /**
     * pone el titulo el
     * resultado del partido 
     * la tabla y los botones
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
        panel.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));
        title = new JLabel("RESULTADOS", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 44));
        title.setForeground(GOLD);
        panel.add(title, BorderLayout.NORTH);
        matchResult = createLabel(" ", 22, Color.WHITE);
        seriesInfo = createLabel(" ", 18, Color.WHITE);
        JPanel table = new JPanel(new GridLayout(3, HEADERS.length, 4, 4));
        table.setBackground(Color.WHITE);
        table.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        cells = new JLabel[3][HEADERS.length];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < HEADERS.length; j++) {
                cells[i][j] = createLabel(" ", 18, Color.WHITE);
                cells[i][j].setOpaque(true);
                if (i == 0) {
                    cells[i][j].setText(HEADERS[j]);
                    cells[i][j].setBackground(GOLD);
                    cells[i][j].setForeground(Color.BLACK);
                } else {
                    cells[i][j].setBackground(new Color(30, 90, 45));
                }
                table.add(cells[i][j]);
            }
        }
        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 4));
        texts.setOpaque(false);
        texts.add(matchResult);
        texts.add(seriesInfo);
        JPanel center = new JPanel(new BorderLayout(0, 15));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 0, 70, 0));
        center.add(texts, BorderLayout.NORTH);
        center.add(table, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        nextButton = createButton("Siguiente partido", GOLD);
        restartButton = createButton("Reiniciar serie", GOLD);
        changeButton = createButton("Cambiar serie", Color.WHITE);
        homeButton = createButton("Inicio", Color.WHITE);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottom.setOpaque(false);
        bottom.add(homeButton);
        bottom.add(changeButton);
        bottom.add(restartButton);
        bottom.add(nextButton);
        panel.add(bottom, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }

    /**
     * crea una etiqueta centrada con el estilo de la pantalla
     * @param text es lo que dice
     * @param size es el tamano de la letra
     * @param color es el color de la letra
     * retorna la etiqueta creada
     */
    private JLabel createLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, size));
        label.setForeground(color);
        return label;
    }

    /**
     * crea un boton con el estilo de la pantalla
     * @param text es lo que dice el boton
     * @param color es el color de fondo
     * retorna el boton creado
     */
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * registra los clicks de todos los botones
     */
    private void prepareActions() {
        nextButton.addActionListener(this);
        restartButton.addActionListener(this);
        changeButton.addActionListener(this);
        homeButton.addActionListener(this);
    }

    /**
     * llena la pantalla con el resultado del ultimo partido y como va la serie
     * @param series es la serie que se esta jugando
     * @param result es el texto con el ganador y el marcador del partido
     */
    public void showResults(Series series, String result) {
        matchResult.setText(result);
        for (int i = 1; i <= 2; i++) {
            Profile profile = series.getProfile(i);
            cells[i][0].setText(series.getName(i));
            cells[i][1].setText("" + series.getWins(i));
            cells[i][2].setText("" + series.getPoints(i));
            if (profile == null) {
                cells[i][3].setText("-");
            } else {
                cells[i][3].setText("" + profile.getPoints());
            }
        }
        if (series.isByPoints()) {
            seriesInfo.setText("Serie a " + series.getTarget() + " puntos");
        } else {
            seriesInfo.setText("Serie al mejor de " + series.getTarget() + " partidos");
        }
        boolean over = series.isOver();
        if (over) {
            title.setText(series.getName(series.getWinner()).toUpperCase() + " GANA LA SERIE");
        } else {
            title.setText("PARTIDO " + series.getMatches());
        }
        nextButton.setVisible(!over);
        restartButton.setVisible(over);
        changeButton.setVisible(over);
    }

    /**
     * siguiente partido va a las mejoras
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            window.showUpgrades();
        } else if (e.getSource() == restartButton) {
            window.restartSeries();
        } else if (e.getSource() == changeButton) {
            window.changeSeries();
        } else {
            window.showScreen(MainWindow.START);
        }
    }
}