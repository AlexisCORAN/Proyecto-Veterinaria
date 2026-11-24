package Procesos;

import Entidades.Servicio;
import EstructuraArrayList.ListaServicios;
import java.util.ArrayList;
import java.util.List;

public class ProcesosServicio {

    public static void validar(Servicio s) {
        if (s == null) {
            throw new IllegalArgumentException("Los datos del servicio son obligatorios.");
        }
        String nombre = textoSeguro(s.getNombre());
        String descripcion = textoSeguro(s.getDescripcion());

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        if (descripcion.isEmpty()) {
            throw new IllegalArgumentException("La descripción del servicio es obligatoria.");
        }
        if (!Double.isFinite(s.getPrecio()) || s.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser un número mayor que cero.");
        }
    }

    public static String generarCodigo(ListaServicios lista) {
        List<String> codigos = new ArrayList<>();
        for (Servicio servicio : lista.getServicios()) {
            codigos.add(servicio.getCodServicio());
        }
        return GeneradorCodigos.Siguiente("SER-", codigos);
    }

    public static void registrar(ListaServicios lista, Servicio nuevo) {
        validar(nuevo);
        if (buscarPorNombre(lista, nuevo.getNombre()) != null) {
            throw new IllegalArgumentException("Ya existe un servicio registrado con ese nombre.");
        }
        lista.Insertar(nuevo);
    }

    public static Servicio actualizar(ListaServicios lista, Servicio original, Servicio datos) {
        validar(datos);
        Servicio conMismoNombre = buscarPorNombre(lista, datos.getNombre());
        if (conMismoNombre != null && conMismoNombre != original) {
            throw new IllegalArgumentException("Ya existe otro servicio registrado con ese nombre.");
        }
        Servicio copia = copiar(original);
        copiarDatos(datos, original);
        return copia;
    }

    public static void revertir(Servicio servicio, Servicio copiaAnterior) {
        copiarDatos(copiaAnterior, servicio);
    }

    public static boolean desactivar(ListaServicios lista, Servicio servicio) {
        return servicio.isActivo() && lista.Desactivar(servicio.getCodServicio());
    }

    private static Servicio buscarPorNombre(ListaServicios lista, String nombre) {
        String nombreBuscado = textoSeguro(nombre);
        for (Servicio s : lista.getServicios()) {
            if (textoSeguro(s.getNombre()).equalsIgnoreCase(nombreBuscado)) {
                return s;
            }
        }
        return null;
    }

    private static void copiarDatos(Servicio origen, Servicio destino) {
        destino.setNombre(textoSeguro(origen.getNombre()));
        destino.setDescripcion(textoSeguro(origen.getDescripcion()));
        destino.setPrecio(origen.getPrecio());
    }

    private static Servicio copiar(Servicio s) {
        Servicio copia = new Servicio(s.getCodServicio(), s.getNombre(),
                s.getDescripcion(), s.getPrecio());
        copia.setActivo(s.isActivo());
        return copia;
    }

    private static String textoSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }
}