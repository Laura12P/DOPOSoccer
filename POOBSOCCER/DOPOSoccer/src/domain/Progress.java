package domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * guarda y abre el progreso de una persona, los puntos y estadisticas de sus jugadores
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Progress {
    /**
     * dice en que archivo se guarda el perfil de una persona
     * solo usa letras y numeros del nombre, sin importar mayusculas
     * @param profile es el nombre del perfil
     * @return el nombre del archivo, o null si el nombre no tiene letras ni numeros
     */
    public static String fileName(String profile) {
        String clean = "";
        for (int i = 0; i < profile.length(); i++) {
            char letter = Character.toLowerCase(profile.charAt(i));
            if (Character.isLetterOrDigit(letter)) {
                clean = clean + letter;
            }
        }
        if (clean.equals("")) {
            return null;
        }
        return "perfil_" + clean + ".dat";
    }

    /**
     * guarda a los jugadores con sus puntos y estadisticas en un archivo
     * @param fileName es el nombre del archivo donde se guarda
     * @param players son los jugadores que se guardan
     * @throws DOPOSoccerException si no se pudo guardar el archivo
     */
    public static void save(String fileName, Player[] players) throws DOPOSoccerException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(players);
            out.close();
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.CANNOT_SAVE);
        }
    }

    /**
     * abre los jugadores que estaban guardados en un archivo
     * @param fileName es el nombre del archivo que se abre
     * @return los jugadores con el progreso que tenian guardado
     * @throws DOPOSoccerException si no existe el archivo o no es un progreso valido
     */
    public static Player[] open(String fileName) throws DOPOSoccerException {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
            Player[] players = (Player[]) in.readObject();
            in.close();
            return players;
        } catch (FileNotFoundException e) {
            throw new DOPOSoccerException(DOPOSoccerException.FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        } catch (ClassNotFoundException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        } catch (ClassCastException e) {
            throw new DOPOSoccerException(DOPOSoccerException.INVALID_FILE);
        }
    }
}
