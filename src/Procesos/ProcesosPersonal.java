package Procesos;

import Entidades.Personal;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoPersonal;
import java.util.ArrayList;
import java.util.List;

public class ProcesosPersonal {

    public static void validar(Personal p) {
        if (p == null) {
            throw new IllegalArgumentException("Los datos del personal son obligatorios.");
        }
        String dni = textoSeguro(p.getDni());
        String nombres = textoSeguro(p.getNombres());
        String apellidos = textoSeguro(p.getApellidos());
        String telefono = textoSeguro(p.getTelefono());
        String cargo = textoSeguro(p.getCargo());

        if (!dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos numéricos.");
        }
        if (nombres.isEmpty() || apellidos.isEmpty()) {
            throw new IllegalArgumentException("Los nombres y apellidos son obligatorios.");
        }
        if (!telefono.isEmpty() && !telefono.matches("\\d{7,9}")) {
            throw new IllegalArgumentException("El teléfono debe contener entre 7 y 9 dígitos.");
        }
        if (cargo.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un cargo válido.");
        }
    }

    public static String generarCodigo(ListaPersonal lista) {
        List<String> codigos = new ArrayList<>();
        for (NodoPersonal actual = lista.ini; actual != null; actual = actual.sig) {
            codigos.add(actual.personal.getCodPersonal());
        }
        return GeneradorCodigos.Siguiente("PER-", codigos);
    }

    public static void registrar(ListaPersonal lista, Personal nuevo) {
        validar(nuevo);
        copiarDatos(nuevo, nuevo);
        if (buscarPorDni(lista, nuevo.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe personal registrado con ese DNI.");
        }
        lista.InsertarFinal(nuevo);
    }

    public static Personal actualizar(ListaPersonal lista, Personal original, Personal datos) {
        validar(datos);
        Personal conMismoDni = buscarPorDni(lista, datos.getDni());
        if (conMismoDni != null && conMismoDni != original) {
            throw new IllegalArgumentException("El DNI ya está registrado para otro miembro del personal.");
        }
        Personal copia = copiar(original);
        copiarDatos(datos, original);
        return copia;
    }

    public static void revertir(Personal personal, Personal copiaAnterior) {
        copiarDatos(copiaAnterior, personal);
    }

    public static boolean desactivar(ListaPersonal lista, Personal personal) {
        return personal != null && personal.isActivo() && lista.Desactivar(personal.getCodPersonal());
    }

    private static Personal buscarPorDni(ListaPersonal lista, String dni) {
        String dniBuscado = textoSeguro(dni);
        for (NodoPersonal actual = lista.ini; actual != null; actual = actual.sig) {
            if (textoSeguro(actual.personal.getDni()).equalsIgnoreCase(dniBuscado)) {
                return actual.personal;
            }
        }
        return null;
    }

    private static void copiarDatos(Personal origen, Personal destino) {
        destino.setDni(textoSeguro(origen.getDni()));
        destino.setNombres(textoSeguro(origen.getNombres()));
        destino.setApellidos(textoSeguro(origen.getApellidos()));
        destino.setTelefono(textoSeguro(origen.getTelefono()));
        destino.setFechaNacimiento(textoSeguro(origen.getFechaNacimiento()));
        destino.setCargo(textoSeguro(origen.getCargo()));
    }

    private static Personal copiar(Personal p) {
        Personal copia = new Personal(p.getCodPersonal(), p.getDni(), p.getNombres(),
                p.getApellidos(), p.getTelefono(), p.getFechaNacimiento(), p.getCargo());
        copia.setActivo(p.isActivo());
        return copia;
    }

    private static String textoSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }
}