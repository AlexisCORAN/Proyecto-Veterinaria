package Pilas;

import Entidades.Cita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class PilaCitas implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Stack<Cita> pila = new Stack<>();

    public void Apilar(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula.");
        } pila.push(cita);
    }

    public Cita Desapilar() { return pila.isEmpty() ? null : pila.pop(); }

    public Cita VerTope() { return pila.isEmpty() ? null : pila.peek(); }

    public int Cantidad() { return pila.size(); }

    public List<Cita> getCitas() {
        List<Cita> lista = new ArrayList<>(pila);
        Collections.reverse(lista);
        return lista;
    }
}

