
package ListaEnlazadas;

import Entidades.Cliente;
import java.io.Serializable;


public class ListaClientes implements Serializable {

    private static final long serialVersionUID = 5556036544276897017L;

    public NodoCliente ini;

    public ListaClientes() {
        ini = null;
    }

    public void InsertarFinal(Cliente c) {
        if (c == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        NodoCliente nuevo = new NodoCliente(c);
        if (ini == null) {
            ini = nuevo;
        } else {
            NodoCliente aux = ini;
            while (aux.sig != null) {
                aux = aux.sig;
            }
            aux.sig = nuevo;
        }
    }

    public NodoCliente Buscar(String cod) {
        if (cod == null) {
            return null;
        }
        NodoCliente aux = ini;
        while (aux != null) {
            String codigoCliente = aux.cliente.getCodCliente();
            if (codigoCliente != null && codigoCliente.equalsIgnoreCase(cod)) {
                return aux;
            }
            aux = aux.sig;
        }
        return null;
    }

    public NodoCliente BuscarPorDni(String dni) {
        if (dni == null) {
            return null;
        }
        NodoCliente aux = ini;
        while (aux != null) {
            String dniCliente = aux.cliente.getDni();
            if (dniCliente != null && dniCliente.equalsIgnoreCase(dni)) {
                return aux;
            }
            aux = aux.sig;
        }
        return null;
    }

    public boolean Desactivar(String cod) {
        NodoCliente encontrado = Buscar(cod);
        if (encontrado == null || !encontrado.cliente.isActivo()) {
            return false;
        }
        encontrado.cliente.setActivo(false);
        return true;
    }

    public boolean Eliminar(Cliente cliente) {
        if (cliente == null) {
            return false;
        }

        NodoCliente anterior = null;
        NodoCliente actual = ini;
        while (actual != null) {
            if (actual.cliente == cliente) {
                if (anterior == null) {
                    ini = actual.sig;
                } else {
                    anterior.sig = actual.sig;
                }
                return true;
            }
            anterior = actual;
            actual = actual.sig;
        }
        return false;
    }

    public boolean Eliminar(String cod) {
        if (ini == null) {
            return false;
        }

        if (ini.cliente.getCodCliente().equalsIgnoreCase(cod)) {
            ini = ini.sig;
            return true;
        }

        NodoCliente aux = ini;
        while (aux.sig != null && !aux.sig.cliente.getCodCliente().equalsIgnoreCase(cod)) {
            aux = aux.sig;
        }

        if (aux.sig != null) {
            aux.sig = aux.sig.sig;
            return true;
        }

        return false;
    }
}
