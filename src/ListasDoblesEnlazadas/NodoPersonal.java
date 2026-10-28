
package ListasDoblesEnlazadas;

import Entidades.Personal;
import java.io.Serializable;


public class NodoPersonal implements Serializable {

    private static final long serialVersionUID = 1L;

    public Personal personal;
    public NodoPersonal sig;
    public NodoPersonal ant;

    public NodoPersonal(Personal personal) {
        this.personal = personal;
        sig = ant = null;
    }
}
