package domain;

import java.util.ArrayList;

/**
 * catalogo de las maquinas que se pueden escoger
 * es el unico lugar donde se agregan maquinas nuevas
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Machines {
    private static final Machine[] ALL = {
        new ClumsyMachine(),
        new OffensiveMachine(),
        new DefensiveMachine(),
        new ExpertMachine()
    };

    /**
     * @return los nombres de las maquinas, en el orden del catalogo
     */
    public static ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < ALL.length; i++) {
            names.add(ALL[i].getName());
        }
        return names;
    }

    /**
     * @return las descripciones de las maquinas, en el mismo orden que los nombres
     */
    public static ArrayList<String> getDescriptions() {
        ArrayList<String> descriptions = new ArrayList<String>();
        for (int i = 0; i < ALL.length; i++) {
            descriptions.add(ALL[i].getDescription());
        }
        return descriptions;
    }

    /**
     * crea una maquina nueva del tipo pedido
     * @param name es el nombre del tipo de maquina
     * @return una maquina nueva de ese tipo
     * @throws DOPOSoccerException si no existe ese tipo de maquina
     */
    public static Machine create(String name) throws DOPOSoccerException {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].getName().equals(name)) {
                return ALL[i].copy();
            }
        }
        throw new DOPOSoccerException(DOPOSoccerException.UNKNOWN_MACHINE);
    }
}