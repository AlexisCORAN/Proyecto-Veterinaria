
package Colas;

import Entidades.Cita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ColaCitas implements Serializable {

    private static final long serialVersionUID = 1L;

    public NodoCitaCola ini;
    public NodoCitaCola fin;
    private int cantidad;

    public void Encolar(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula.");
        }
        NodoCitaCola nuevo = new NodoCitaCola(cita);
        if (ini == null) {
            ini = fin = nuevo;
        } else {
            fin.sig = nuevo;
            fin = nuevo;
        }
        cantidad++;
    }

    public void InsertarAlInicio(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula.");
        }
        NodoCitaCola nuevo = new NodoCitaCola(cita);
        nuevo.sig = ini;
        ini = nuevo;
        if (fin == null) {
            fin = nuevo;
        }
        cantidad++;
    }

    public Cita Buscar(String codigo) {
        NodoCitaCola actual = ini;
        while (actual != null) {
            if (actual.cita.getCodCita().equalsIgnoreCase(codigo)) {
                return actual.cita;
            }
            actual = actual.sig;
        }
        return null;
    }

    public Cita Desencolar() {
        if (ini == null) {
            return null;
        }
        Cita cita = ini.cita;
        ini = ini.sig;
        if (ini == null) {
            fin = null;
        }
        cantidad--;
        return cita;
    }

    public boolean Eliminar(String codigo) {
        NodoCitaCola anterior = null;
        NodoCitaCola actual = ini;
        while (actual != null) {
            if (actual.cita.getCodCita().equalsIgnoreCase(codigo)) {
                if (anterior == null) {
                    ini = actual.sig;
                } else {
                    anterior.sig = actual.sig;
                }
                if (actual == fin) {
                    fin = anterior;
                }
                cantidad--;
                return true;
            }
            anterior = actual;
            actual = actual.sig;
        }
        return false;
    }

    public int Cantidad() {
        return cantidad;
    }

    public List<Cita> getCitas() {
        List<Cita> citas = new ArrayList<>();
        NodoCitaCola actual = ini;
        while (actual != null) {
            citas.add(actual.cita);
            actual = actual.sig;
        }
        return citas;
    }
}
