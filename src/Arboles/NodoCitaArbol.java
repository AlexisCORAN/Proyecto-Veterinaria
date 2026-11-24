package Arboles;

import Entidades.Cita;

public class NodoCitaArbol {
    public Cita cita;
    public NodoCitaArbol izq;
    public NodoCitaArbol der;

    public NodoCitaArbol(Cita cita) {
        this.cita = cita;
        this.izq = null;
        this.der = null;
    }
}
