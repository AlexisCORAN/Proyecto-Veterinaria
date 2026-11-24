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
        if (m == null) {
            throw new IllegalArgumentException("Los datos de la mascota son obligatorios.");
        }
        if (textoSeguro(m.getNombre()).isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (textoSeguro(m.getEspecie()).isEmpty()) {
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
        copiarDatos(nueva, nueva);
        validarClienteActivo(clientes, nueva.getCodCliente());
        lista.InsertarFinal(nueva);
    }

    public static Mascota actualizar(ListaClientes clientes, Mascota original, Mascota datos) {
        validar(datos);
        validarClienteActivo(clientes, textoSeguro(datos.getCodCliente()));
        Mascota copia = copiar(original);
        copiarDatos(datos, original);
        return copia;
    }

    public static void revertir(Mascota mascota, Mascota copiaAnterior) {
        copiarDatos(copiaAnterior, mascota);
    }

    public static boolean desactivar(ListaMascotas lista, Mascota mascota) {
        return mascota != null && mascota.isActivo() && lista.Desactivar(mascota.getCodMascota());
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
        destino.setNombre(textoSeguro(origen.getNombre()));
        destino.setEspecie(textoSeguro(origen.getEspecie()));
        destino.setRaza(textoSeguro(origen.getRaza()));
        destino.setCodCliente(textoSeguro(origen.getCodCliente()));
    }

    private static Mascota copiar(Mascota m) {
        Mascota copia = new Mascota(m.getCodMascota(), m.getNombre(), m.getEspecie(),
                m.getRaza(), m.getCodCliente());
        copia.setActivo(m.isActivo());
        return copia;
    }

    private static String textoSeguro(String valor) {
        return valor == null ? "" : valor.trim();
    }
}