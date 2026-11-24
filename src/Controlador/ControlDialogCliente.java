package Controlador;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import Persistencia.AlmacenarCliente;
import Procesos.Mensajes;
import Procesos.ProcesosCliente;
import Vista.DialogNuevoCliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ControlDialogCliente implements ActionListener {

    private final DialogNuevoCliente vista;
    private final ListaClientes lista;
    private final Cliente clienteEdicion;
    private boolean guardadoExitoso;

    public ControlDialogCliente(DialogNuevoCliente vista, ListaClientes lista, Cliente clienteEdicion) {
        this.vista = vista;
        this.lista = lista;
        this.clienteEdicion = clienteEdicion;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnCancelar.addActionListener(this);

        if (clienteEdicion != null) {
            this.vista.nombrePanel.setText("Modificar Información de Cliente");
            ProcesosCliente.MostrarClienteEnFormulario(vista, clienteEdicion);
        } else {
            vista.txtCodigoCliente.setText(ProcesosCliente.GenerarCodigo(lista));
            vista.txtCodigoCliente.setEditable(false);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void guardarNuevoCliente() throws IOException {
        Cliente nuevoCliente = ProcesosCliente.RegistrarCliente(vista, lista);
        try {
            AlmacenarCliente.Guardar(lista);
        } catch (IOException ex) {
            ProcesosCliente.RevertirRegistro(lista, nuevoCliente);
            throw ex;
        }

        guardadoExitoso = true;
        Mensajes.Mostrar("Cliente registrado exitosamente.");
        vista.dispose();
    }

    private void guardarEdicion() throws IOException {
        Cliente copiaAnterior = ProcesosCliente.ActualizarClienteDesdeFormulario(
                vista, lista, clienteEdicion);
        try {
            AlmacenarCliente.Guardar(lista);
        } catch (IOException ex) {
            ProcesosCliente.RestaurarCliente(clienteEdicion, copiaAnterior);
            throw ex;
        }

        guardadoExitoso = true;
        Mensajes.Mostrar("Cliente actualizado correctamente.");
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnCancelar) {
            vista.dispose();
            return;
        }
        if (e.getSource() != vista.btnGuardar) {
            return;
        }

        try {
            if (clienteEdicion == null) {
                guardarNuevoCliente();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudieron guardar los cambios del cliente: " + ex.getMessage());
        }
    }
}
