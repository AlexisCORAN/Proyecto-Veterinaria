

package ListasDoblesEnlazadas;

import Entidades.Personal;
import java.io.Serializable;


public class ListaPersonal implements Serializable {



    public NodoPersonal ini;
    public NodoPersonal fin;

    public ListaPersonal() {
        ini = fin = null;
    }

    public void InsertarFinal(Personal p) {
        NodoPersonal nuevo = new NodoPersonal(p);
        if (ini == null) {
            ini = fin = nuevo;
        } else {
            nuevo.ant = fin;
            fin.sig = nuevo;
            fin = nuevo;
        }
    }

    public NodoPersonal Buscar(String cod) {
        NodoPersonal aux = ini;
        while (aux != null) {
            if (aux.personal.getCodPersonal().equalsIgnoreCase(cod)) {
                return aux;
            }
            aux = aux.sig;
        }
        return null;
    }

    public NodoPersonal BuscarPorDni(String dni) {
        NodoPersonal aux = ini;
        while (aux != null) {
            if (aux.personal.getDni().equalsIgnoreCase(dni)) {
                return aux;
            }
            aux = aux.sig;
        }
        return null;
    }

    public boolean Desactivar(String cod) {
        NodoPersonal nodo = Buscar(cod);
        if (nodo == null || !nodo.personal.isActivo()) {
            return false;
        }
        nodo.personal.setActivo(false);
        return true;
    }

    public boolean Eliminar(String cod) {
        NodoPersonal actual = Buscar(cod);
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
}