package Arboles;

import Entidades.Cita;

public class ArbolCitas {
    public NodoCitaArbol raiz;
    public ArbolCitas() { this.raiz = null; }

    public void Insertar(Cita cita) {
        if (cita == null || cita.getCodCita() == null) {
            return;
        } raiz = InsertarRecursivo(raiz, cita);
    }

    private NodoCitaArbol InsertarRecursivo(NodoCitaArbol nodo, Cita cita) {
        if (nodo == null) { return new NodoCitaArbol(cita); }
        if (cita.getCodCita().compareToIgnoreCase(nodo.cita.getCodCita()) < 0) {
            nodo.izq = InsertarRecursivo(nodo.izq, cita);
        } else if (cita.getCodCita().compareToIgnoreCase(nodo.cita.getCodCita()) > 0) {
            nodo.der = InsertarRecursivo(nodo.der, cita);
        } return nodo;
    }

    public Cita Buscar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        NodoCitaArbol resultado = BuscarRecursivo(raiz, codigo.trim());
        if (resultado != null) {
            return resultado.cita;
        } return null;
    }

    private NodoCitaArbol BuscarRecursivo(NodoCitaArbol nodo, String codigo) {
        if (nodo == null || nodo.cita.getCodCita().equalsIgnoreCase(codigo)) { return nodo; }

        if (codigo.compareToIgnoreCase(nodo.cita.getCodCita()) < 0) {
            return BuscarRecursivo(nodo.izq, codigo);
        } else { return BuscarRecursivo(nodo.der, codigo); }
    }
}
