package Procesos;

import Entidades.Cliente;
import Entidades.Mascota;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.NodoMascota;
import java.util.ArrayList;
import java.util.List;

public class ProcesosMascota {

    public static void validar(Mascota m) {
        if (m.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (m.getEspecie().isBlank()) {
            throw new IllegalArgumentException("Seleccione la especie de la mascota.");
        }
    }

    public static String generarCodigo(ListaMascotas lista) {
        List<String> codigos = new ArrayList<>();
        for (NodoMascota actual = lista.ini; actual != null; actual = actual.sig) {
            codigos.add(actual.mascota.getCodMascota());
        }
        return GeneradorCodigos.Siguiente("MAS-", codigos);
    }

    public static void registrar(ListaMascotas lista, ListaClientes clientes, Mascota nueva) {
        validar(nueva);
        validarClienteActivo(clientes, nueva.getCodCliente());
        lista.InsertarFinal(nueva);
    }

    public static Mascota actualizar(ListaClientes clientes, Mascota original, Mascota datos) {
        validar(datos);
        validarClienteActivo(clientes, datos.getCodCliente());
        Mascota copia = copiar(original);
        copiarDatos(datos, original);
        return copia;
    }

    public static void revertir(Mascota mascota, Mascota copiaAnterior) {
        copiarDatos(copiaAnterior, mascota);
    }

    public static boolean desactivar(ListaMascotas lista, Mascota mascota) {
        return mascota.isActivo() && lista.Desactivar(mascota.getCodMascota());
    }

    private static void validarClienteActivo(ListaClientes clientes, String codigoCliente) {
        NodoCliente nodo = clientes.Buscar(codigoCliente);
        if (nodo == null) {
            throw new IllegalArgumentException("El código de cliente no está registrado.");
        }
        Cliente propietario = nodo.cliente;
        if (!propietario.isActivo()) {
            throw new IllegalArgumentException("El propietario se encuentra retirado.");
        }
    }

    private static void copiarDatos(Mascota origen, Mascota destino) {
        destino.setNombre(origen.getNombre());
        destino.setEspecie(origen.getEspecie());
        destino.setRaza(origen.getRaza());
        destino.setCodCliente(origen.getCodCliente());
    }

    private static Mascota copiar(Mascota m) {
        Mascota copia = new Mascota(m.getCodMascota(), m.getNombre(), m.getEspecie(),
                m.getRaza(), m.getCodCliente());
        copia.setActivo(m.isActivo());
        return copia;
    }
}