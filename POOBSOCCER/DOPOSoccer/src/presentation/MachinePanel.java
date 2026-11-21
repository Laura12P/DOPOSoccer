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
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import domain.DOPOSoccer;

/**
 * pantalla donde se escoge el tipo de maquina
 * torpe
 * ofensiva
 * defensiva
 * experta
 * @author Jeronimo Avila Castillo
 * @author Luara Juliana Parra Velandia
 */
public class MachinePanel extends JPanel implements ActionListener {
    private static final Color GOLD = new Color(230, 190, 40);
    private static final String[] DESCRIPTIONS = {
        "escoge sus movimientos al azar",
        "su prioridad es anotar",
        "su prioridad es defender el arco",
        "decide segun como va el partido"
    };
    
    private MainWindow window;
    private JLabel title;
    private JToggleButton[] machineButtons;
    private JButton readyButton;
    private JButton backButton;

    /**
     * CONSTRUCTOR
     * crea la pantalla para escoger cual maquina escoger
     * @param window es la ventana principal
     */
    public MachinePanel(MainWindow window){
        this.window = window;
        prepareElements();
        prepareActions();
    }
    /**
     * pone el titulo
     * y un boton para cada maquina con sus botones
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
        title = new JLabel("TIPO DE MAQUINA", SwingConstants.CENTER);
        title.setFont(new Font("Comic Sans MS", Font.BOLD, 56));
        title.setForeground(GOLD);
        panel.add(title, BorderLayout.NORTH);

        String[] names = DOPOSoccer.MACHINES;
        machineButtons = new JToggleButton[names.length];
        ButtonGroup group = new ButtonGroup();
        JPanel options = new JPanel(new GridLayout(2, 2, 30, 20));
        options.setOpaque(false);
        options.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        for (int i = 0; i < names.length; i++) {
            machineButtons[i] = new JToggleButton("<html><center>" + names[i]
                  + "<br><font size='3'>" + DESCRIPTIONS[i] + "</font></center></html>", i == 0 );
            machineButtons[i].setFont(new Font("Comic Sans MS", Font.BOLD, 22));
            machineButtons[i].setForeground(Color.BLACK);
            machineButtons[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            machineButtons[i].setContentAreaFilled(false);
            machineButtons[i].setOpaque(true);
            machineButtons[i].setFocusPainted(false);
            machineButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            group.add(machineButtons[i]);
            options.add(machineButtons[i]);
        }
        paintOptions();
        panel.add(options, BorderLayout.CENTER);
        backButton = new JButton("Volver");
        backButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        readyButton = new JButton("Listo");
        readyButton.setBackground(GOLD);
        readyButton.setForeground(Color.BLACK);
        readyButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        readyButton.setFocusPainted(false);
        readyButton.setPreferredSize(new Dimension(140, 45));
        JPanel bottom = new JPanel (new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(backButton, BorderLayout.WEST);
        bottom.add(readyButton, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);  
    }
    /**
     * registra clicks de todos los botones
     */
    private void prepareActions() {
        for (int i = 0; i < machineButtons.length; i++){
            machineButtons[i].addActionListener(this);
        }
        readyButton.addActionListener(this);
        backButton.addActionListener(this);
    }
    /**
     * pinta de dorado la maquina escogida
     */
    private void paintOptions() {
        for (int i = 0; i < machineButtons.length; i++) {
            if (machineButtons[i].isSelected()) {
                machineButtons[i].setBackground(GOLD);
            } else {
                machineButtons[i].setBackground(Color.WHITE);
            }
        }
    }
    /**
     * retorna el nombre de la maquina que se escogio
     */
    public String getSelected() {
        String selected = DOPOSoccer.MACHINES[0];
        for (int i = 0; i < machineButtons.length; i++){
            if (machineButtons[i].isSelected()) {
                selected = DOPOSoccer.MACHINES[i];
            }
            
        }
        return selected;
    }
    

    /**
     * guarda la maquina seleccionada y va a los controles
     * @param e es el evento del click
     */
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == readyButton){
            window.saveMachine(getSelected());
            window.showScreen(MainWindow.CONTROLS_ONE);
        }  else if (e.getSource() == backButton) {
            window.showScreen(MainWindow.PLAYER_ONE);
        } else {
            paintOptions();
        }
    }
}