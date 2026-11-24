package Colas;

import Entidades.Cita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class ColaCitas implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LinkedList<Cita> cola = new LinkedList<>();

    public void Encolar(Cita cita) {
        if (cita == null) { throw new IllegalArgumentException("La cita no puede ser nula."); }
        cola.offer(cita);
    }

    public Cita Desencolar() { return cola.poll(); }

    public Cita VerPrimero() { return cola.peek(); }

    public Cita Buscar(String codigo) {
        for (Cita c : cola) {
            if (c.getCodCita().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }

    public boolean Eliminar(String codigo) {
        Iterator<Cita> it = cola.iterator();
        while (it.hasNext()) {
            if (it.next().getCodCita().equalsIgnoreCase(codigo)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public int Cantidad() { return cola.size(); }

    public List<Cita> getCitas() { return new ArrayList<>(cola); }
}