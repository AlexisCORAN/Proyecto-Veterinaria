
package Procesos;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import Vista.DialogNuevoCliente;
import Vista.pnlClientes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class ProcesosCliente {

    
    
    

    public static void LimpiarEntradas(pnlClientes pc) {
        pc.txtBuscar.setText("Buscar por Código");
        pc.txtBuscar.requestFocus();
    }

    public static void MostrarEnFormulario(pnlClientes pc, Cliente cliente) {
        if (cliente != null) {
            pc.txtBuscar.setText(cliente.getCodCliente());
        }
    }

    
    
    

    
    public static Cliente LeerClienteDesdeFormulario(DialogNuevoCliente vista) {
        String codigo = vista.txtCodigoCliente.getText().trim();
        String dni = vista.txtDniCliente.getText().trim();
        String nombres = vista.txtNombresCliente.getText().trim();
        String apellidos = vista.txtApellidosCliente.getText().trim();
        String telefono = vista.txtTelefonoCliente.getText().trim();
        String fechaNacimiento = new SimpleDateFormat("dd/MM/yyyy")
                .format((Date) vista.spinFechaNacimiento.getValue());

        if (dni.isEmpty() || nombres.isEmpty()) {
            throw new IllegalArgumentException("DNI y nombres son obligatorios.");
        }

        return new Cliente(codigo, dni, nombres, apellidos, telefono, fechaNacimiento);
    }

    public static String GenerarCodigo(ListaClientes lista) {
        List<String> codigos = new ArrayList<>();
        NodoCliente actual = lista.ini;
        while (actual != null) {
            codigos.add(actual.cliente.getCodCliente());
            actual = actual.sig;
        }
        return GeneradorCodigos.Siguiente("CLI-", codigos);
    }

    public static Cliente RegistrarCliente(DialogNuevoCliente vista, ListaClientes lista) {
        Cliente datos = LeerClienteDesdeFormulario(vista);
        String codigo = GenerarCodigo(lista);
        vista.txtCodigoCliente.setText(codigo);
        Cliente nuevoCliente = new Cliente(codigo, datos.getDni(), datos.getNombres(),
                datos.getApellidos(), datos.getTelefono(), datos.getFechaNacimiento());
        if (lista.Buscar(nuevoCliente.getCodCliente()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese código.");
        }
        if (lista.BuscarPorDni(nuevoCliente.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI.");
        }

        lista.InsertarFinal(nuevoCliente);
        return nuevoCliente;
    }

    public static void RevertirRegistro(ListaClientes lista, Cliente cliente) {
        lista.Eliminar(cliente);
    }

    public static Cliente ActualizarClienteDesdeFormulario(
            DialogNuevoCliente vista, ListaClientes lista, Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente para editar.");
        }

        Cliente datosActualizados = LeerClienteDesdeFormulario(vista);
        if (!cliente.getCodCliente().equalsIgnoreCase(datosActualizados.getCodCliente())) {
            throw new IllegalArgumentException("El código del cliente no se puede modificar.");
        }
        NodoCliente clienteConDni = lista.BuscarPorDni(datosActualizados.getDni());
        if (clienteConDni != null && clienteConDni.cliente != cliente) {
            throw new IllegalArgumentException("El DNI ya está registrado para otro cliente.");
        }

        Cliente copiaAnterior = CopiarCliente(cliente);
        cliente.setDni(datosActualizados.getDni());
        cliente.setNombres(datosActualizados.getNombres());
        cliente.setApellidos(datosActualizados.getApellidos());
        cliente.setTelefono(datosActualizados.getTelefono());
        cliente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
        return copiaAnterior;
    }

    public static void RestaurarCliente(Cliente cliente, Cliente copiaAnterior) {
        cliente.setDni(copiaAnterior.getDni());
        cliente.setNombres(copiaAnterior.getNombres());
        cliente.setApellidos(copiaAnterior.getApellidos());
        cliente.setTelefono(copiaAnterior.getTelefono());
        cliente.setFechaNacimiento(copiaAnterior.getFechaNacimiento());
        cliente.setActivo(copiaAnterior.isActivo());
    }

    private static Cliente CopiarCliente(Cliente cliente) {
        Cliente copia = new Cliente(
                cliente.getCodCliente(),
                cliente.getDni(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getTelefono(),
                cliente.getFechaNacimiento());
        copia.setActivo(cliente.isActivo());
        return copia;
    }

    public static boolean DesactivarCliente(ListaClientes lista, Cliente cliente) {
        if (lista == null || cliente == null || !cliente.isActivo()) {
            return false;
        }
        NodoCliente nodo = lista.Buscar(cliente.getCodCliente());
        return nodo != null && nodo.cliente == cliente && lista.Desactivar(cliente.getCodCliente());
    }

    public static void RestaurarEstado(Cliente cliente, boolean activoAnterior) {
        cliente.setActivo(activoAnterior);
    }

    
    public static void MostrarClienteEnFormulario(DialogNuevoCliente vista, Cliente cliente) {
        vista.txtCodigoCliente.setEditable(false);
        if (cliente != null) {
            vista.txtCodigoCliente.setText(cliente.getCodCliente());
            vista.txtDniCliente.setText(cliente.getDni());
            vista.txtNombresCliente.setText(cliente.getNombres());
            vista.txtApellidosCliente.setText(cliente.getApellidos());
            vista.txtTelefonoCliente.setText(cliente.getTelefono());
            if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
                try {
                    Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(cliente.getFechaNacimiento());
                    vista.spinFechaNacimiento.setValue(fecha);
                } catch (java.text.ParseException ex) {
                    throw new IllegalStateException("La fecha de nacimiento guardada no es válida.", ex);
                }
            }
        }
    }
}