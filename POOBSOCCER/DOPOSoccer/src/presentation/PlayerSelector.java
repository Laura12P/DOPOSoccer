package presentation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import domain.BlueAura;
import domain.Confusion;
import domain.DOPOSoccerException;
import domain.Illusion;
import domain.Lightning;
import domain.Log;
import domain.Player;
import domain.Progress;
import domain.Tornado;

import java.util.Random;
import java.awt.Image;
import javax.swing.ImageIcon;
/**
 * fila con los jugadores del pais elegido, para que cada jugador escoja el suyo
 * muestra la ficha del jugador elegido
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class PlayerSelector extends JPanel implements ActionListener {
    private static final Player[] ALL = {
        new Player("Son Heung-min", "Corea Del Sur", 1, 1, 1, 1, new BlueAura()),
        new Player("Lee Kang-in", "Corea Del Sur", 1, 1, 1, 1, new BlueAura()),
        new Player("Bryan Mbeumo", "Camerun", 1, 1, 1, 1, new Lightning()),
        new Player("Carlos Baleba", "Camerun", 1, 1, 1, 1, new Lightning()),
        new Player("Victor Osimhen", "Nigeria", 1, 1, 1, 1, new Tornado()),
        new Player("Ademola Lookman", "Nigeria", 1, 1, 1, 1, new Tornado()),
        new Player("Haaland", "Noruega", 1, 1, 1, 1, new Confusion()),
        new Player("Odegaard", "Noruega", 1, 1, 1, 1, new Confusion()),
        new Player("Christian Pulisic", "EE.UU", 1, 1, 1, 1, new Illusion()),
        new Player("Weston McKennie", "EE.UU", 1, 1, 1, 1, new Illusion())
    };

    private static final Color NORMAL = Color.WHITE;
    private static final Color SELECTED = new Color(255, 215, 0);

    private JLabel playerName;
    private JTextField profileField;
    private JLabel profile;
    private JPanel row;
    private ArrayList<Player> available;
    private ArrayList<JButton> buttons;
    private Player selected;
    private Player[] roster;
    private String profileName;

    /**
     * CONSTRUCTOR
     * crea el selector vacio para un jugador
     * empieza sin perfil, con los jugadores en cero
     * @param player es el texto que identifica al jugador
     */
    public PlayerSelector(String player) {
        selected = null;
        profileName = null;
        roster = newRoster();
        available = new ArrayList<Player>();
        buttons = new ArrayList<JButton>();
        prepareElements(player);
    }

    /**
     * crea los jugadores de un perfil nuevo, sin puntos
     * @return los jugadores con sus estadisticas iniciales
     */
    private static Player[] newRoster() {
        Player[] players = new Player[ALL.length];
        for (int i = 0; i < ALL.length; i++) {
            players[i] = ALL[i].copy();
        }
        return players;
    }

    /**
     * ubica el nombre del jugador, el perfil, la fila de botones y la ficha
     * @param player es el texto que identifica al jugador
     */
    private void prepareElements(String player) {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        playerName = new JLabel(player);
        playerName.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        playerName.setForeground(Color.WHITE);
        JLabel profileLabel = new JLabel("Perfil:");
        profileLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
        profileLabel.setForeground(Color.WHITE);
        profileField = new JTextField(12);
        profileField.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        JPanel profileBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        profileBox.setOpaque(false);
        profileBox.add(profileLabel);
        profileBox.add(profileField);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(playerName, BorderLayout.WEST);
        top.add(profileBox, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        add(row, BorderLayout.CENTER);
        profile = new JLabel("ningun jugador elegido");
        profile.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        profile.setForeground(Color.WHITE);
        profile.setVerticalAlignment(SwingConstants.TOP);
        profile.setPreferredSize(new Dimension(100, 48));
        add(profile, BorderLayout.SOUTH);
        profileField.addActionListener(this);
    }

    /**
     * muestra los jugadores del pais que se eligio antes
     * @param country es el pais elegido
     */
    public void loadCountry(String country) {
        row.removeAll();
        available.clear();
        buttons.clear();
        selected = null;
        profile.setText("ningun jugador elegido");
        for (int i = 0; i < roster.length; i++) {
            if (roster[i].getCountry().equals(country)) {
                JButton button = new JButton(roster[i].getName());
                String file = roster[i].getName().toLowerCase().replace(" ", "");
                Image photo = new ImageIcon("imagenes/jugadores/" + file + ".png").getImage();
                button.setIcon(new ImageIcon(photo.getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
                button.setVerticalTextPosition(JButton.BOTTOM);
                button.setHorizontalTextPosition(JButton.CENTER);
                button.setBackground(NORMAL);
                button.setFocusPainted(false);
                button.addActionListener(this);
                available.add(roster[i]);
                buttons.add(button);
                row.add(button);
            }
        }
        row.revalidate();
        row.repaint();
    }

    /**
     * abre el perfil que esta escrito, o lo crea si no existe, con los jugadores en cero
     * si no hay nada escrito se queda sin perfil y no se guardan puntos
     * siempre lee el archivo, para no perder los puntos que gano en otra pantalla
     * si el archivo no sirve o el nombre no es valido muestra el mensaje
     */
    public void loadProfile() {
        String text = profileField.getText().trim();
        String file = Progress.fileName(text);
        Player[] loaded = null;
        if (file != null) {
            try {
                loaded = Progress.open(file);
            } catch (DOPOSoccerException e) {
                if (!DOPOSoccerException.FILE_NOT_FOUND.equals(e.getMessage())) {
                    Log.record(e);
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }
        } else if (!text.equals("")) {
            JOptionPane.showMessageDialog(this, "el perfil debe tener letras o numeros");
        }
        if (loaded == null || loaded.length != ALL.length) {
            loaded = newRoster();
        }
        roster = loaded;
        if (file == null) {
            profileName = null;
        } else {
            profileName = text;
        }
        for (int i = 0; i < available.size(); i++) {
            for (int j = 0; j < roster.length; j++) {
                if (roster[j].getName().equals(available.get(i).getName())) {
                    available.set(i, roster[j]);
                }
            }
        }
        if (selected != null) {
            for (int j = 0; j < roster.length; j++) {
                if (roster[j].getName().equals(selected.getName())) {
                    selected = roster[j];
                }
            }
            showSelected();
        }
    }

    /**
     * muestra la ficha del jugador elegido, con los puntos si tiene perfil
     * la ficha queda al lado del jugador elegido, a la izq o a la der
     * siempre ocupa dos lineas para que los botones no cambien de tamano
     */
    private void showSelected() {
        String points = "Sin perfil, no guarda puntos";
        if (profileName != null) {
            points = "Puntos: " + selected.getPoints();
        }
        String side = "left";
        profile.setHorizontalAlignment(SwingConstants.LEFT);
        if (available.indexOf(selected) == 1) {
            side = "right";
            profile.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        profile.setText("<html><div align='" + side + "'>" + selected.getName() + " - "
                + selected.getCountry() + "  Poder: " + selected.getPower().getName()
                + "<br>Velocidad: " + selected.getSpeed()
                + "  Salto: " + selected.getJump()
                + "  Patada: " + selected.getKick()
                + "  Vida: " + selected.getSurvival()
                + "  " + points + "</div></html>");
    }

    /**
     * si se oprime enter en el perfil lo abre, si se da click a un jugador lo marca,
     * desmarca el otro y muestra su ficha
     * @param e es el evento cuando se da click
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == profileField) {
            loadProfile();
            return;
        }
        loadProfile();
        for (int i = 0; i < buttons.size(); i++) {
            if (e.getSource() == buttons.get(i)) {
                selected = available.get(i);
                buttons.get(i).setBackground(SELECTED);
            } else {
                buttons.get(i).setBackground(NORMAL);
            }
        }
        showSelected();
    }

    /**
     * dice si el jugador ya escogio personaje
     * @return es true si ya hay un personaje elegido
     */
    public boolean hasSelected() {
        return selected != null;
    }

    /**
     * devuelve el personaje que se escogio
     * @return es el personaje elegido
     */
    public Player getSelected() {
        return selected;
    }

    /**
     * escribe el nombre del perfil en el cuadro de texto, se usa en las pruebas
     * @param name es el nombre del perfil
     */
    void writeProfile(String name) {
        profileField.setText(name);
    }

    /**
     * @return el nombre del perfil que se abrio, o null si juega sin perfil
     */
    public String getProfile() {
        return profileName;
    }

    /**
     * @return los jugadores del perfil, con sus puntos
     */
    public Player[] getRoster() {
        return roster;
    }

    /**
     * escoge un jugador cualquiera, para el que controla la maquina
     * @return es un jugador escogido al azar
     */
    public static Player atRandom() {
        Random random = new Random();
        return ALL[random.nextInt(ALL.length)];
    }
}