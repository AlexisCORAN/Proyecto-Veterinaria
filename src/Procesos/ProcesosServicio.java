
package Procesos;

import EstructuraArrayList.ListaServicios;
import Entidades.Servicio;
import Vista.DialogNuevoServicio;
import Vista.pnlServicio;
import java.util.ArrayList;
import java.util.List;


public class ProcesosServicio {

    public static void LimpiarEntradas(pnlServicio vista) {
        vista.txtBuscar.setText("Buscar por Código");
    }

    public static Servicio LeerServicioDesdeFormulario(DialogNuevoServicio vista) {
        String codigo = vista.txtCodigoServicio.getText().trim();
        String nombre = vista.txtNombreServicio.getText().trim();
        String descripcion = vista.txtDescripcionServicio.getText().trim();
        String precioTexto = vista.txtPrecioServicio.getText().trim().replace(',', '.');
        if (nombre.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty()) {
            throw new IllegalArgumentException("Nombre, descripción y precio son obligatorios.");
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser un número válido.", ex);
        }
        if (!Double.isFinite(precio) || precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        return new Servicio(codigo, nombre, descripcion, precio);
    }

    public static String GenerarCodigo(ListaServicios lista) {
        List<String> codigos = new ArrayList<>();
        for (Servicio servicio : lista.getServicios()) {
            codigos.add(servicio.getCodServicio());
        }
        return GeneradorCodigos.Siguiente("SER-", codigos);
    }

    public static Servicio RegistrarServicio(DialogNuevoServicio vista, ListaServicios lista) {
        Servicio datos = LeerServicioDesdeFormulario(vista);
        String codigo = GenerarCodigo(lista);
        vista.txtCodigoServicio.setText(codigo);
        Servicio nuevo = new Servicio(codigo, datos.getNombre(), datos.getDescripcion(), datos.getPrecio());
        if (lista.Buscar(nuevo.getCodServicio()) != null) {
            throw new IllegalArgumentException("Ya existe un servicio con ese código.");
        }
        lista.Insertar(nuevo);
        return nuevo;
    }

    public static Servicio ActualizarServicioDesdeFormulario(
            DialogNuevoServicio vista, ListaServicios lista, Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("Debe seleccionar un servicio para editar.");
        }
        Servicio datos = LeerServicioDesdeFormulario(vista);
        if (!servicio.getCodServicio().equalsIgnoreCase(datos.getCodServicio())) {
            throw new IllegalArgumentException("El código del servicio no se puede modificar.");
        }

        Servicio anterior = Copiar(servicio);
        Aplicar(servicio, datos);
        return anterior;
    }

    public static boolean DesactivarServicio(ListaServicios lista, Servicio servicio) {
        if (lista == null || servicio == null || !servicio.isActivo()) {
            return false;
        }
        return lista.Buscar(servicio.getCodServicio()) == servicio
                && lista.Desactivar(servicio.getCodServicio());
    }

    public static void RestaurarEstado(Servicio servicio, boolean activoAnterior) {
        servicio.setActivo(activoAnterior);
    }

    public static void RestaurarServicio(Servicio servicio, Servicio anterior) {
        Aplicar(servicio, anterior);
        servicio.setActivo(anterior.isActivo());
    }

    public static void MostrarServicioEnFormulario(DialogNuevoServicio vista, Servicio servicio) {
        vista.txtCodigoServicio.setEditable(false);
        vista.txtCodigoServicio.setText(servicio.getCodServicio());
        vista.txtNombreServicio.setText(servicio.getNombre());
        vista.txtDescripcionServicio.setText(servicio.getDescripcion());
        vista.txtPrecioServicio.setText(String.valueOf(servicio.getPrecio()));
    }

    private static Servicio Copiar(Servicio servicio) {
        Servicio copia = new Servicio(servicio.getCodServicio(), servicio.getNombre(),
                servicio.getDescripcion(), servicio.getPrecio());
        copia.setActivo(servicio.isActivo());
        return copia;
    }

    private static void Aplicar(Servicio destino, Servicio origen) {
        destino.setNombre(origen.getNombre());
        destino.setDescripcion(origen.getDescripcion());
        destino.setPrecio(origen.getPrecio());
    }
}
