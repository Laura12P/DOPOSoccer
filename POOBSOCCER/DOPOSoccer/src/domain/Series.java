package domain;

import java.io.Serializable;

/**
 * serie de los partidos entre los jugadores
 * mejor de 1, 3 o 5 partidos
 * gana partido 100 puntos, y 25 el perdedor
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Series implements Serializable {
    public static final int[] BEST_OF = {1, 3, 5};
    public static final String MACHINE_NAME = "Maquina";
    private String[] names;
    private Profile[] profiles;
    private boolean byPoints;
    private int target;
    private int[] wins;
    private int[] points;
    private int matches;
    private int winner;
    private int rematches;
    
    /**
     * CONSTRUCTOR
     * @param nameOne es el nombre del jugador de la izq
     * @param one es el perfil del jugador de la izq
     * @param nameTwo es el nombre del jugador de la der
     * @param two es el perfil de jugador de la der
     * @param byPoints es true si gana el que llegue primero a los puntos necesarios
     * @param target son los puntos que se necesitan para ganar
     * @throws DOPOSoccerException son las excepciones de esta parte
     */
    public Series (String nameOne, Profile one, String nameTwo, 
            Profile two, boolean byPoints, int target) throws DOPOSoccerException{
        if (byPoints && target <= 0) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_SERIES);
        }
        if (!byPoints && !isBestOf(target)) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_SERIES);
        }
        names = new String[] {nameOne, nameTwo};
        profiles = new Profile[] {one, two};
        this.byPoints = byPoints;
        this.target = target;
        wins = new int[2];
        points = new int[2];
        matches = 0;
        winner = 0;
        rematches = 0;
    }
    /**
     * @param amount es la cantidad de partidos
     * retorna true si es 1, 3, 5
     */
    private boolean isBestOf(int amount) {
        for (int i = 0; i < BEST_OF.length; i++) {
            if (BEST_OF[i] == amount) {
                return true;
            }
        }
        return false;
    }
    /**
     * anota el resultado de cada partido
     * suma puntos ganando y con esos puntos se puede ganar cosas
     * si alguien fana la serie la serie se acaba
     * @param matchWinner es 1 y gano izq o 2 si gano der
     */
    public void recordMatch(int matchWinner) {
        if (isOver()) {
            return;
        }
        int won = matchWinner - 1;
        int lost = 1 - won;
        matches = matches + 1;
        wins[won] = wins[won] + 1;
        givePoints(won, DOPOSoccer.WIN_POINTS);
        givePoints(lost, DOPOSoccer.LOSE_POINTS);
        if (byPoints) {
            if (points[won] >= target) {
            winner = matchWinner;
        } else if (points[lost] >= target) {
            winner = lost + 1;
        }
        } else if (wins[won] > target / 2) {
            winner = matchWinner;
        }
    }
    /**
     * suma los puntos de la serie para el perfil
     * @param index es 0 para el jugador de la izd y 1 el de la der
     * @param amount son los puntos
     */
    private void givePoints(int index, int amount) {
        points[index] = points[index] + amount;
        if (profiles[index] != null) {
            profiles[index].addPoints(amount);
        }
    }
    /**
     * revancha
     * se hace un partido mas despues que alguien gano 
     * @param matchWinner es 1 si gano izq o 2 si gano der
     */
    public void recordRematch(int matchWinner) {
        int won = matchWinner -1;
        int lost = 1 - won;
        matches = matches + 1;
        rematches = rematches + 1;
        wins[won] = wins[won] + 1;
        givePoints(won, DOPOSoccer.WIN_POINTS);
        givePoints(lost, DOPOSoccer.LOSE_POINTS);
    }
    /**
     * cambia el perfil de un jugador por el del archivo al abrir una partida
     * @param number es 1 el de la izq y 2 el de la der
     * @param profile es el perfil que se abrio
     */
    public void updateProfile(int number, Profile profile) {
        profiles[number - 1] = profile;
    }
    /**
     * retorna true si alguien ya gano la serie
     */
    public boolean isOver() {
        return winner != 0;
    }
    /**
     * returna 1 o 2 segun el que gano la serie
     */
    public int getWinner() {
        return winner;
    }
    /**
     * @param number es 1 el jugador de la izq, y 2 el de la der
     * retorna el perfil del jugador
     */
    public Profile getProfile(int number) {
        return profiles[number - 1];
    }
    /**
     * @param number es 1 el jugador de la izq, y 2 el de la der
     */
    public String getName(int number) {
        return names[number - 1];
    }
    /**
     * 
     * @param number es 1 el jugador de la izq, y 2 el de la der
     * retorna los partidos que se han ganado
     */
    public int getWins(int number){
        return wins[number - 1];
    }
    /**
     * @param number es 1 el jugador de la izq, y 2 el de la der
     * retorna los partidos que se ah ganado
     */
    public int getPoints(int number) {
        return points[number -1];
    }
    /**
     * retorna cuantos partidos se han jugado
     */
    public int getMatches() {
        return matches;
    }
    /**
     * retorna true su la serie esa a puntos
     */
    public boolean isByPoints() {
        return byPoints;
    }
    /**
     * retorna cuantas revanchas se han jugado
     */
     public int getRematches() {
        return rematches;
     }
    /**
     * retorna los puntos necesarios para ganar
     */
    public int getTarget() {
        return target;
    }
}