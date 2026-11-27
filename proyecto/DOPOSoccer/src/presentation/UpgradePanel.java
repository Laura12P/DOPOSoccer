package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

import domain.DOPOSoccerException;
import domain.Log;
import domain.Profile;
import domain.Series;
/**
 * pantalla para mejorar las stats
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 * 
 */
public class UpgradePanel extends JPanel implements ActionListener {
    private static final Color GOLD = new Color(230, 190, 40);
    private MainWindow window;
    private Series series;
    private JLabel[] names;
    private JLabel[] points;
    private JLabel[] costs;
    private JLabel[][] levels;
    private JButton[][] upgradeButtons;
    private JLabel message;
    private JButton playButton;

    /**
     * CONSTRUCTOR
     * crea la pantalla de las mejoras de stats
     * @param window es la ventan principal
     */
    public UpgradePanel(MainWindow window) {
        this.window = window;
        prepareElements();
        prepareActions();
    }
    /**
     * titulo y columna de las estadisticas de cada jugador
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
        JLabel title = new JLabel("MEJORAS", SwingConstants.CENTER);
         title.setFont(new Font("Comic Sans MS", Font.BOLD, 52));
        title.setForeground(GOLD);
        message = new JLabel(" ", SwingConstants.CENTER);
        message.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        message.setForeground(Color.YELLOW);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.CENTER);
        top.add(message, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        names = new JLabel[2];
        points = new JLabel[2];
        costs = new JLabel[2];
        levels = new JLabel[2][Profile.STATS.length];
        upgradeButtons = new JButton[2][Profile.STATS.length];
        JPanel columns = new JPanel(new GridLayout(1, 2, 40, 0));
        columns.setOpaque(false);
        for (int i = 0; i < 2; i++) {
            columns.add(createColumn(i));
        }
        columns.setBorder(BorderFactory.createEmptyBorder(10, 0, 90, 0));
        panel.add(columns, BorderLayout.CENTER);
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
        bottom.add(playButton, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
    }
    /**
     * arma la columna del jugador
     * nombre
     * puntos
     * precio
     * estaditicas
     */
    private JPanel createColumn(int index) {
        JPanel column = new JPanel(new GridLayout(Profile.STATS.length + 3, 1, 0, 6));
        column.setOpaque(false);
        names[index] = createLabel(" ", 24, GOLD);
        points[index] = createLabel(" ", 18, Color.WHITE);
        costs[index] = createLabel(" ", 18, Color.WHITE);
        column.add(names[index]);
        column.add(points[index]);
        column.add(costs[index]);
        for (int j = 0; j < Profile.STATS.length; j++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            levels[index][j] = createLabel(" ", 18, Color.WHITE);
            upgradeButtons[index][j] = new JButton(Profile.STATS[j] + " +");
            upgradeButtons[index][j].setFont(new Font("Comic Sans MS", Font.BOLD, 14));
            upgradeButtons[index][j].setBackground(Color.WHITE);
            upgradeButtons[index][j].setForeground(Color.BLACK);
            upgradeButtons[index][j].setFocusPainted(false);
            upgradeButtons[index][j].setCursor(new Cursor(Cursor.HAND_CURSOR));
            upgradeButtons[index][j].setPreferredSize(new Dimension(120, 30));
            row.add(levels[index][j], BorderLayout.CENTER);
            row.add(upgradeButtons[index][j], BorderLayout.EAST);
            column.add(row);
        }
        return column;
    }
    /**
     * crea una etiqueta de la pantalla
     * @param text es lo que dice
     * @param size es el e tamanio de la letra
     * @param color es el color del texto
     * retorna la etiqueta que se creo
     */
    private JLabel createLabel(String text, int size, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, size));
        label.setForeground(color);
        return label;
    }
    /**
     * registra los clicks
     */
    private void prepareActions() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < Profile.STATS.length; j++) {
                upgradeButtons[i][j].addActionListener(this);
            }
        }
        playButton.addActionListener(this);
    }
    /**
     * recibe las series que esta jugando
     * @param series se la serie actual
     */
    public void showSeries(Series series) {
        this.series = series;
        message.setText(" ");
        refresh();
    }
    /**
     * se reinicia puntos, precios y levels de cada jugador
     */
     private void refresh() {
        for (int i = 0; i < 2; i++) {
            Profile profile = series.getProfile(i + 1);
            names[i].setText(series.getName(i + 1));
            boolean hasProfile = profile != null;
            if (hasProfile) {
                points[i].setText("Puntos: " + profile.getPoints());
                costs[i].setText("Siguiente mejora: " + profile.getUpgradeCost());
            } else {
                points[i].setText("Sin perfil, no gana puntos ni mejora");
                costs[i].setText(" ");
            }
            for (int j = 0; j < Profile.STATS.length; j++) {
                if (hasProfile) {
                    levels[i][j].setText(Profile.STATS[j] + ": " + profile.getLevel(j)
                        + " / " + Profile.MAX_LEVEL);
                } else {
                    levels[i][j].setText(" ");
                }
                upgradeButtons[i][j].setVisible(hasProfile);
            }
        }
     }
     /**
      * botones de empezar el partido
      */
     public void actionPerformed(ActionEvent e) {
        if (e.getSource() == playButton) {
            window.playSeriesMatch();
        } else {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < Profile.STATS.length; j++) {
                    if (e.getSource() == upgradeButtons[i][j]) {
                        try {
                            window.upgrade(i + 1, j);
                            message.setText(series.getName(i + 1) + " mejoro " + Profile.STATS[j]);
                        } catch (DOPOSoccerException ex) {
                            Log.record(ex);
                            message.setText(ex.getMessage());
                        }
                    }
                }
            }
             refresh();
        }
     }

}