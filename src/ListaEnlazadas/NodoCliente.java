package ListaEnlazadas;

import Entidades.Cliente;
import java.io.Serializable;

public class NodoCliente implements Serializable {
    private static final long serialVersionUID = 5490285681215046198L;
    public Cliente cliente;
    public NodoCliente sig;
    public NodoCliente(Cliente cliente) {
        this.cliente = cliente;
        this.sig = null;
    }
}
