
package Pilas;

import Entidades.Cita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PilaCitas implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Cita> citas = new ArrayList<>();

    public void Apilar(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula.");
        }
        citas.add(cita);
    }

    public Cita Desapilar() {
        return citas.isEmpty() ? null : citas.remove(citas.size() - 1);
    }

    public Cita Buscar(String codigo) {
        for (int i = citas.size() - 1; i >= 0; i--) {
            Cita cita = citas.get(i);
            if (cita.getCodCita().equalsIgnoreCase(codigo)) {
                return cita;
            }
        }
        return null;
    }

    public boolean Eliminar(String codigo) {
        Cita cita = Buscar(codigo);
        return cita != null && citas.remove(cita);
    }

    public int Cantidad() {
        return citas.size();
    }

    public List<Cita> getCitas() {
        return new ArrayList<>(citas);
    }
}
