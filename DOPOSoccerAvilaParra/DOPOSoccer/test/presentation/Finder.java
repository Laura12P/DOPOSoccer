package presentation;
import java.awt.Container;
import java.util.ArrayList;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

/**
 * clase auxiliar para las pruebas de la presentacion
 * @author Jeronimo Avila Castillo
 * @author Laura Juliana Parra Velandia
 */
public class Finder {

    /**
     * recorre todos los componentes que hay dentro de otro y los guarda en una lista
     * @param context es la informacion del componente donde se busca
     * @param found es la lista donde se van guardando
     */
    private static void collect(AccessibleContext context, ArrayList<AccessibleContext> found) {
        int children = context.getAccessibleChildrenCount();
        for (int i = 0; i < children; i++) {
            AccessibleContext child = context.getAccessibleChild(i).getAccessibleContext();
            if (child != null) {
                found.add(child);
                collect(child, found);
            }
        }
    }

    /**
     * @param container es la pantalla o panel donde se busca
     * retorna la informacion de todos los componentes que tiene adentro
     */
    private static ArrayList<AccessibleContext> all(Container container) {
        ArrayList<AccessibleContext> found = new ArrayList<AccessibleContext>();
        collect(container.getAccessibleContext(), found);
        return found;
    }

    /**
     * @param context es la informacion de un componente
     * retorna true si el componente es un boton
     */
    private static boolean isButton(AccessibleContext context) {
        AccessibleRole role = context.getAccessibleRole();
        return role == AccessibleRole.PUSH_BUTTON || role == AccessibleRole.TOGGLE_BUTTON
                || role == AccessibleRole.CHECK_BOX;
    }

    /**
     * @param context es la informacion de un componente
     * retorna el texto que muestra el componente, o vacio si no tiene
     */
    private static String textOf(AccessibleContext context) {
        String text = context.getAccessibleName();
        if (text == null) {
            return "";
        }
        return text;
    }

    /**
     * oprime un boton como si se le diera click
     * @param context es la informacion del boton
     */
    private static void press(AccessibleContext context) {
        AccessibleAction action = context.getAccessibleAction();
        action.doAccessibleAction(0);
    }

    /**
     * busca los botones que no son de navegacion
     * @param container es la pantalla o panel donde se busca
     * retorna la informacion de los botones de opciones, por ejemplo los paises o los jugadores
     */
    private static ArrayList<AccessibleContext> options(Container container) {
        ArrayList<AccessibleContext> components = all(container);
        ArrayList<AccessibleContext> found = new ArrayList<AccessibleContext>();
        for (int i = 0; i < components.size(); i++) {
            String text = textOf(components.get(i)).toLowerCase();
            if (isButton(components.get(i)) && !text.equals("listo") && !text.equals("volver")
                    && !text.equals("jugar")) {
                found.add(components.get(i));
            }
        }
        return found;
    }

    /**
     * oprime el primer boton cuyo texto contiene el texto dado
     * @param container es la pantalla o panel donde se busca
     * @param text es el texto que debe tener el boton
     * retorna true si encontro el boton y lo pulza
     */
    public static boolean click(Container container, String text) {
        ArrayList<AccessibleContext> components = all(container);
        for (int i = 0; i < components.size(); i++) {
            if (isButton(components.get(i)) && textOf(components.get(i)).contains(text)) {
                press(components.get(i));
                return true;
            }
        }
        return false;
    }

    /**
     * oprime uno de los botones de opciones, contando desde cero
     * @param container es la pantalla o panel donde se busca
     * @param index es la posicion de la opcion
     */
    public static void clickOption(Container container, int index) {
        press(options(container).get(index));
    }

    /**
     * @param container es la pantalla o panel donde se busca
     * retorna cuantos botones de opciones hay
     */
    public static int countOptions(Container container) {
        return options(container).size();
    }

    /**
     * revisa si alguna etiqueta de la pantalla contiene el texto dado
     * @param container es la pantalla o panel donde se busca
     * @param text es el texto que se busca
     * retorna true si alguna etiqueta lo contiene
     */
    public static boolean hasLabel(Container container, String text) {
        ArrayList<AccessibleContext> components = all(container);
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getAccessibleRole() == AccessibleRole.LABEL
                    && textOf(components.get(i)).contains(text)) {
                return true;
            }
        }
        return false;
    }
}