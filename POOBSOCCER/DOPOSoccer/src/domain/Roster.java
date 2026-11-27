package domain;

import java.util.ArrayList;
import java.util.Random;

/**
 * catalogo de los personajes que se pueden elegir
 * es el unico lugar donde se agregan personajes y paises nuevos
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Roster {
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

    /**
     * @return los paises que hay, sin repetir y en el orden del catalogo
     */
    public static ArrayList<String> getCountries() {
        ArrayList<String> countries = new ArrayList<String>();
        for (int i = 0; i < ALL.length; i++) {
            if (!countries.contains(ALL[i].getCountry())) {
                countries.add(ALL[i].getCountry());
            }
        }
        return countries;
    }

    /**
     * @param country es el pais que se busca
     * @return los personajes de ese pais
     */
    public static ArrayList<Player> getPlayers(String country) {
        ArrayList<Player> players = new ArrayList<Player>();
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].getCountry().equals(country)) {
                players.add(ALL[i]);
            }
        }
        return players;
    }

    /**
     * @return un personaje cualquiera del catalogo
     */
    public static Player atRandom() {
        Random random = new Random();
        return ALL[random.nextInt(ALL.length)];
    }
}
