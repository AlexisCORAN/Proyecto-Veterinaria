
package ListasDoblesEnlazadas;

import Entidades.Mascota;
import java.io.Serializable;


public class ListaMascotas implements Serializable {

    private static final long serialVersionUID = 1L;

    public NodoMascota ini;
    public NodoMascota fin;

    public ListaMascotas() {
        ini = fin = null;
    }

    public void InsertarFinal(Mascota m) {
        NodoMascota nuevo = new NodoMascota(m);
        if (ini == null) {
            ini = fin = nuevo;
        } else {
            nuevo.ant = fin;
            fin.sig = nuevo;
            fin = nuevo;
        }
    }

    public NodoMascota Buscar(String codMascota) {
        NodoMascota aux = ini;
        while (aux != null) {
            if (aux.mascota.getCodMascota().equalsIgnoreCase(codMascota)) {
                return aux;
            }
            aux = aux.sig;
        }
        return null;
    }

    public ListaMascotas BuscarPorCliente(String codCliente) {
        ListaMascotas sublista = new ListaMascotas();
        NodoMascota aux = ini;
        while (aux != null) {
            if (aux.mascota.getCodCliente().equalsIgnoreCase(codCliente)) {
                sublista.InsertarFinal(aux.mascota);
            }
            aux = aux.sig;
        }
        return sublista;
    }

    public boolean Eliminar(String codMascota) {
        NodoMascota actual = Buscar(codMascota);
        if (actual != null) {
            if (actual == ini) {
                ini = actual.sig;
                if (ini != null) {
                    ini.ant = null;
                } else {
                    fin = null;
                }
            } else if (actual == fin) {
                fin = actual.ant;
                fin.sig = null;
            } else {
                actual.ant.sig = actual.sig;
                actual.sig.ant = actual.ant;
            }
            return true;
        }
        return false;
    }

    public boolean Desactivar(String codMascota) {
        NodoMascota nodo = Buscar(codMascota);
        if (nodo == null || !nodo.mascota.isActivo()) {
            return false;
        }
        nodo.mascota.setActivo(false);
        return true;
    }
}
