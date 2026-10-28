
package Colas;

import Entidades.Cita;
import java.io.Serializable;


public class NodoCitaCola implements Serializable {

    private static final long serialVersionUID = 1L;

    public Cita cita;
    public NodoCitaCola sig;

    public NodoCitaCola(Cita cita) {
        this.cita = cita;
    }
}
