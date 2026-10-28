
package Procesos;

import Entidades.Cliente;
import Entidades.Mascota;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.NodoMascota;
import Vista.DialogNuevaMascota;
import Vista.pnlMascota;
import java.util.ArrayList;
import java.util.List;


public class ProcesosMascota {

    public static void LimpiarEntradas(pnlMascota vista) {
        vista.txtBuscar.setText("Buscar por Código");
    }

    public static Mascota LeerMascotaDesdeFormulario(DialogNuevaMascota vista) {
        String codigo = vista.txtCodigoMascota.getText().trim();
        String nombre = vista.txtNombresMascota.getText().trim();
        Object especieSeleccionada = vista.cboEspecie.getSelectedItem();
        String raza = vista.txtRazaMascota.getText().trim();
        
        String propietarioCompleto = vista.txtCodigoCliente1.getText().trim();

        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        if (propietarioCompleto.isEmpty()) {
            throw new IllegalArgumentException("Debe buscar y asignar un dueño usando el DNI.");
        }
        if (especieSeleccionada == null || especieSeleccionada.toString().isBlank()) {
            throw new IllegalArgumentException("Seleccione la especie de la mascota.");
        }
        
        String codigoCliente = propietarioCompleto.split(" - ")[0].trim();

        return new Mascota(codigo, nombre, especieSeleccionada.toString(), raza, codigoCliente);
    }

    public static String GenerarCodigo(ListaMascotas lista) {
        List<String> codigos = new ArrayList<>();
        NodoMascota actual = lista.ini;
        while (actual != null) {
            codigos.add(actual.mascota.getCodMascota());
            actual = actual.sig;
        }
        return GeneradorCodigos.Siguiente("MAS-", codigos);
    }

    public static Mascota RegistrarMascota(
            DialogNuevaMascota vista, ListaMascotas lista, ListaClientes clientes) {
        Mascota datos = LeerMascotaDesdeFormulario(vista);
        String codigo = GenerarCodigo(lista);
        vista.txtCodigoMascota.setText(codigo);
        Mascota nueva = new Mascota(codigo, datos.getNombre(), datos.getEspecie(),
                datos.getRaza(), datos.getCodCliente());
        
        if (lista.Buscar(nueva.getCodMascota()) != null) {
            throw new IllegalArgumentException("Ya existe una mascota con ese código.");
        }
        ValidarClienteActivo(clientes, nueva.getCodCliente());
        lista.InsertarFinal(nueva);
        return nueva;
    }

    public static Mascota ActualizarMascotaDesdeFormulario(
            DialogNuevaMascota vista, ListaMascotas lista, ListaClientes clientes, Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("Debe seleccionar una mascota para editar.");
        }
        Mascota datos = LeerMascotaDesdeFormulario(vista);
        if (!mascota.getCodMascota().equalsIgnoreCase(datos.getCodMascota())) {
            throw new IllegalArgumentException("El código de la mascota no se puede modificar.");
        }
        ValidarClienteActivo(clientes, datos.getCodCliente());

        Mascota anterior = Copiar(mascota);
        Aplicar(mascota, datos);
        return anterior;
    }

    public static boolean DesactivarMascota(ListaMascotas lista, Mascota mascota) {
        if (lista == null || mascota == null || !mascota.isActivo()) {
            return false;
        }
        NodoMascota nodo = lista.Buscar(mascota.getCodMascota());
        return nodo != null && nodo.mascota == mascota
                && lista.Desactivar(mascota.getCodMascota());
    }

    public static void RestaurarEstado(Mascota mascota, boolean activoAnterior) {
        mascota.setActivo(activoAnterior);
    }

    public static void RestaurarMascota(Mascota mascota, Mascota anterior) {
        Aplicar(mascota, anterior);
        mascota.setActivo(anterior.isActivo());
    }

    public static void MostrarMascotaEnFormulario(DialogNuevaMascota vista, Mascota mascota, ListaClientes clientes) {
        vista.txtCodigoMascota.setEditable(false);
        vista.txtCodigoMascota.setText(mascota.getCodMascota());
        vista.txtNombresMascota.setText(mascota.getNombre());
        vista.cboEspecie.setSelectedItem(mascota.getEspecie());
        vista.txtRazaMascota.setText(mascota.getRaza());
        
        NodoCliente nodo = clientes.Buscar(mascota.getCodCliente());
        if (nodo != null) {
            Cliente c = nodo.cliente;
            vista.txtCodigoCliente1.setText(c.getCodCliente() + " - " + c.getNombres() + " " + c.getApellidos());
        } else {
            vista.txtCodigoCliente1.setText(mascota.getCodCliente());
        }
    }

    private static void ValidarClienteActivo(ListaClientes clientes, String codigoCliente) {
        if (clientes == null) {
            throw new IllegalArgumentException("No se pudo cargar la lista de clientes.");
        }
        NodoCliente nodo = clientes.Buscar(codigoCliente);
        if (nodo == null) {
            throw new IllegalArgumentException("El código de cliente no está registrado.");
        }
        Cliente cliente = nodo.cliente;
        if (!cliente.isActivo()) {
            throw new IllegalArgumentException("No se pueden registrar mascotas para un cliente retirado.");
        }
    }

    private static Mascota Copiar(Mascota mascota) {
        Mascota copia = new Mascota(mascota.getCodMascota(), mascota.getNombre(),
                mascota.getEspecie(), mascota.getRaza(), mascota.getCodCliente());
        copia.setActivo(mascota.isActivo());
        return copia;
    }

    private static void Aplicar(Mascota destino, Mascota origen) {
        destino.setNombre(origen.getNombre());
        destino.setEspecie(origen.getEspecie());
        destino.setRaza(origen.getRaza());
        destino.setCodCliente(origen.getCodCliente());
    }
}