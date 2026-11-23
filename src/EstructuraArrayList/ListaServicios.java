package EstructuraArrayList;

import Entidades.Servicio;
import java.io.Serializable;
import java.util.ArrayList;

public class ListaServicios implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Servicio> servicios;

    public ListaServicios() {
        servicios = new ArrayList<>();}

    public void Insertar(Servicio servicio) {
        servicios.add(servicio);}

    public Servicio Buscar(String codServicio) {
        for (int i = 0; i < servicios.size(); i++) {
            if (servicios.get(i).getCodServicio().equalsIgnoreCase(codServicio)) {
                return servicios.get(i);
            }
        } return null;
    }

    public boolean Eliminar(String codServicio) {
        Servicio servicio = Buscar(codServicio);
        if (servicio != null) {
            servicios.remove(servicio);
            return true;
        } return false;
    }

    public boolean Desactivar(String codServicio) {
        Servicio servicio = Buscar(codServicio);
        if (servicio == null || !servicio.isActivo()) {
            return false;
        }
        servicio.setActivo(false);
        return true;
    }

    public int Cantidad() {
        return servicios.size();}

    public ArrayList<Servicio> getServicios() {
        return servicios;}
}
