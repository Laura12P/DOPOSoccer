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

import javax.swing.AbstractButton;
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

import domain.DOPOSoccerException;
import domain.Log;
import domain.Series;


/**
 * pantalla donde se elige hace la serie a jugar
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class SeriesPanel extends JPanel implements  ActionListener {
    private static final Color GOLD = new Color(230, 190, 40);
    private MainWindow window;
    private JToggleButton[] bestOfButtons;
    private JToggleButton pointsButton;
    private JSpinner pointsAmount;
    private JLabel message;
    private JButton nextButton;
    private JButton backButton;

    /**
     * CONSTRUCTOR
     * crea la pantalla para armar la serie
     * @param window es la ventana principal
     */
    public SeriesPanel(MainWindow window) {
        this.window = window;
        prepareElements();
        prepareActions();
    }
    /**
     * titulo, tipo de serie, puntos y botones 
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
        JLabel title = new JLabel("SERIE", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 56));
        title.setForeground(GOLD);
        JLabel question = new JLabel("Como quieren jugar la serie?", SwingConstants.CENTER);
        question.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        question.setForeground(Color.WHITE);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.CENTER);
        top.add(question, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);
        ButtonGroup group = new ButtonGroup();
        JPanel formats = new JPanel(new GridLayout(1, Series.BEST_OF.length + 1, 15, 0));
        formats.setOpaque(false);
        formats.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        bestOfButtons = new JToggleButton[Series.BEST_OF.length];
        for (int i = 0; i < bestOfButtons.length; i++) {
            bestOfButtons[i] = new JToggleButton("Mejor de " + Series.BEST_OF[i], i == 1);
            styleOption(bestOfButtons[i]);
            group.add(bestOfButtons[i]);
            formats.add(bestOfButtons[i]);
        }
        pointsButton = new JToggleButton("A puntos");
        styleOption(pointsButton);
        group.add(pointsButton);
        formats.add(pointsButton);
        pointsAmount = new JSpinner(new SpinnerNumberModel(300, 25, 5000, 25));
        pointsAmount.setFont(new Font("Arial", Font.BOLD, 22));
        pointsAmount.setPreferredSize(new Dimension(110, 40));
        JPanel pointsBlock = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pointsBlock.setOpaque(false);
        pointsBlock.add(whiteLabel("puntos para ganar"));
        pointsBlock.add(pointsAmount);
        message = new JLabel(" ", SwingConstants.CENTER);
        message.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        message.setForeground(Color.YELLOW);
        JPanel center = new JPanel(new GridLayout(3, 1, 0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(30, 0, 60, 0));
        center.add(formats);
        center.add(pointsBlock);
        center.add(message);
        panel.add(center, BorderLayout.CENTER);
        backButton = new JButton("Volver");
        backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton = new JButton("Siguiente");
        nextButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        nextButton.setBackground(GOLD);
        nextButton.setForeground(Color.BLACK);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.setPreferredSize(new Dimension(140, 45));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(backButton, BorderLayout.WEST);
        bottom.add(nextButton, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        paintOptions();
    }
    /**
     * crea una etiqueta blanxa para la pantalla
     */
    private JLabel whiteLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
        label.setForeground(Color.WHITE);
        return label;
    }
    /**
     * estetica de las opciones del boton
     */
    private void styleOption(AbstractButton button) {
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    /**
     * prepara clikcs
     */
    private void prepareActions() {
        for (int i = 0; i < bestOfButtons.length; i++) {
            bestOfButtons[i].addActionListener(this);
        }
        pointsButton.addActionListener(this);
        nextButton.addActionListener(this);
        backButton.addActionListener(this);
    }
    /**
     * pinta el tipo de serie que se escogio
     */
    private void paintOptions() {
        for (int i = 0; i < bestOfButtons.length; i++) {
            if (bestOfButtons[i].isSelected()) {
                bestOfButtons[i].setBackground(GOLD);
            } else {
                bestOfButtons[i].setBackground(Color.WHITE);
            }
        }
        if (pointsButton.isSelected()){
            pointsButton.setBackground(GOLD);
        } else {
            pointsButton.setBackground(Color.WHITE);
        }
        pointsAmount.setEnabled(pointsButton.isSelected());
    }
    /**
     * retorna el mejor partido
     */
    private int getTarget() {
        if (pointsButton.isSelected()) {
            return ((Number) pointsAmount.getValue()).intValue();
        }
        int target = Series.BEST_OF[0];
        for (int i = 0; i < bestOfButtons.length; i++) {
            if (bestOfButtons[i].isSelected()) {
                target = Series.BEST_OF[i];
            }
        }
        return target;
    }
    /**
     * arma la serie y regresa si la serie no es valida
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            try {
                window.startSeries(pointsButton.isSelected(), getTarget());
            } catch (DOPOSoccerException ex) {
                Log.record(ex);
                message.setText(ex.getMessage());
            }
        } else if (e.getSource() == backButton) {
            window.changeConfig();
        } else {
            paintOptions();
        }
    }

}
