package domain;

import java.io.Serializable;

/**
 * perfil de cada persona que esta jugando
 * guarda el nombre, guarda los puntos ganados y las estadisticas
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Profile implements Serializable {
    public static final String[] STATS = {"Speed", "Kick", "Jump", "Survival"};
    public static final int SPEED = 0;
    public static final int KICK = 1;
    public static final int JUMP = 2;
    public static final int SURVIVAL = 3;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 10;
    public static final int FIRST_PRICE = 500;

    private String name;
    private int points;
    private int[] levels;
    private int upgrades;

    /**
     * CONSTRUCTOR
     * crea el perfil nuevo sin puntos
     * y con todas las estatss en 1
     * @param name es el nombre de la persona
     */
    public Profile (String name) {
        this.name = name;
        points = 0;
        upgrades = 0;
        levels = new int[STATS.length];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = MIN_LEVEL;
        }
    }
    /**
     * retorna el nombre de cada persona
     */
    public String getName() {
        return name;
    }
    /**
     * retorna los puntos que tiene para comprar las mejoras
     */
    public int getPoints() {
        return points;
    }
    /**
     * Suma los puntos ganados en un partido
     * @Param amount son los puntos que se van sumando
     */
    public void addPoints(int amount) {
        points = points + amount;
    }
    /**
     * @param stat es las estadisticas
     * retorna el nivel de la estadistica de 1 a 10
     */
    public int getLevel(int stat){
        return levels[stat];
    }
    /**
     * el precio que arranca en 500 y se duplica con cada comprada
     * retorna lo que va costando la siguiente mejora
     */
    public int getUpgradeCost() {
        int cost = FIRST_PRICE;
        for (int i = 0; i < upgrades; i++) {
            cost = cost * 2;
        }
        return cost;
    }
    /**
     * compra punto de mejora para cada estadistica
     * @param stat es las estadisticas
     * @throws DOPOSoccerExcetcion si la estadistica no existe
     */
    public void upgrade(int stat) throws DOPOSoccerException {
        if (stat < 0 || stat >= levels.length) {
            throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_STAT);
        }
        if (levels[stat] >= MAX_LEVEL) {
            throw new DOPOSoccerException(DOPOSoccerException.MAX_LEVEL);
        }
        int cost = getUpgradeCost();
        if (points < cost) {
            throw new DOPOSoccerException(DOPOSoccerException.NOT_ENOUGH_POINTS);
        }
        points = points - cost;
        levels[stat] = levels[stat] + 1;
        upgrades = upgrades + 1;
    }
    /**
     * le pone estadisticas a un personaje del pefil
     * @param player es el personaje
     */
    public void applyTo(Player player) {
        player.setStats(levels[SPEED], levels[KICK], levels[JUMP], levels[SURVIVAL]);
    }

}