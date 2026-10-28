
package ListasDoblesEnlazadas;

import Entidades.Mascota;
import java.io.Serializable;


public class NodoMascota implements Serializable {

    private static final long serialVersionUID = 1L;

    public Mascota mascota;
    public NodoMascota sig;
    public NodoMascota ant;

    public NodoMascota(Mascota mascota) {
        this.mascota = mascota;
        sig = ant = null;
    }
}
