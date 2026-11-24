package Procesos;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import java.util.ArrayList;
import java.util.List;

public class ProcesosCliente {

    public static void validar(Cliente c) {
        if (!c.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe contener exactamente 8 dígitos numéricos.");
        }
        if (c.getNombres().isEmpty() || c.getApellidos().isEmpty()) {
            throw new IllegalArgumentException("Los nombres y apellidos son obligatorios.");
        }
        if (!c.getTelefono().isEmpty() && !c.getTelefono().matches("\\d{7,9}")) {
            throw new IllegalArgumentException("El teléfono debe contener entre 7 y 9 dígitos.");
        }
    }

    public static String generarCodigo(ListaClientes lista) {
        List<String> codigos = new ArrayList<>();
        for (NodoCliente actual = lista.ini; actual != null; actual = actual.sig) {
            codigos.add(actual.cliente.getCodCliente());
        }
        return GeneradorCodigos.Siguiente("CLI-", codigos);
    }

    public static void registrar(ListaClientes lista, Cliente nuevo) {
        validar(nuevo);
        if (lista.BuscarPorDni(nuevo.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con ese DNI.");
        }
        lista.InsertarFinal(nuevo);
    }

    public static Cliente actualizar(ListaClientes lista, Cliente original, Cliente datos) {
        validar(datos);
        NodoCliente conMismoDni = lista.BuscarPorDni(datos.getDni());
        if (conMismoDni != null && conMismoDni.cliente != original) {
            throw new IllegalArgumentException("El DNI ya está registrado para otro cliente.");
        }
        Cliente copia = copiar(original);
        copiarDatos(datos, original);
        return copia;
    }

    public static void revertir(Cliente cliente, Cliente copiaAnterior) {
        copiarDatos(copiaAnterior, cliente);
    }

    public static boolean desactivar(ListaClientes lista, Cliente cliente) {
        return cliente.isActivo() && lista.Desactivar(cliente.getCodCliente());
    }

    private static void copiarDatos(Cliente origen, Cliente destino) {
        destino.setDni(origen.getDni());
        destino.setNombres(origen.getNombres());
        destino.setApellidos(origen.getApellidos());
        destino.setTelefono(origen.getTelefono());
        destino.setFechaNacimiento(origen.getFechaNacimiento());
    }

    private static Cliente copiar(Cliente c) {
        Cliente copia = new Cliente(c.getCodCliente(), c.getDni(), c.getNombres(),
                c.getApellidos(), c.getTelefono(), c.getFechaNacimiento());
        copia.setActivo(c.isActivo());
        return copia;
    }
}