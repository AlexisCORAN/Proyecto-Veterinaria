



package Controlador;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import Persistencia.AlmacenarCliente;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosCliente;
import Vista.DialogNuevoCliente;
import Vista.pnlClientes;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ControlCliente implements ActionListener {

    private final pnlClientes vista;
    private final ListaClientes lista;
    private NodoCliente encontrado;

    public ControlCliente(pnlClientes vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarCliente.Recuperar();

        this.vista.btnNuevo.addActionListener(this);
        this.vista.btnEdicionCliente.addActionListener(this);
        this.vista.btnEstadoCliente.addActionListener(this);
        this.vista.btnBuscar.addActionListener(this);
        this.vista.txtBuscar.addActionListener(this);
        this.vista.cboEstado.addActionListener(this);
        this.vista.tablaClientes.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarClienteDeTabla();
            }
        });

        ActualizarVista();
    }

    private void ActualizarVista() {
        ProcesosCliente.LimpiarEntradas(vista);
        encontrado = null;
        vista.tablaClientes.clearSelection();
        Mostrar.DatosClientes(vista.tablaClientes, lista, estadoSeleccionado());
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void seleccionarClienteDeTabla() {
        int filaSeleccionada = vista.tablaClientes.getSelectedRow();
        if (filaSeleccionada < 0) {
            encontrado = null;
            return;
        }

        int filaModelo = vista.tablaClientes.convertRowIndexToModel(filaSeleccionada);
        String codigo = String.valueOf(vista.tablaClientes.getModel().getValueAt(filaModelo, 1));
        encontrado = lista.Buscar(codigo);
        ProcesosCliente.MostrarEnFormulario(vista, encontrado == null ? null : encontrado.cliente);
    }

    private void seleccionarFila(String codigo) {
        for (int fila = 0; fila < vista.tablaClientes.getRowCount(); fila++) {
            int filaModelo = vista.tablaClientes.convertRowIndexToModel(fila);
            Object valor = vista.tablaClientes.getModel().getValueAt(filaModelo, 1);
            if (codigo.equalsIgnoreCase(String.valueOf(valor))) {
                vista.tablaClientes.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void abrirDialogo(Cliente clienteEdicion) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogNuevoCliente dialogo = new DialogNuevoCliente(framePadre, true);
        ControlDialogCliente controlador = new ControlDialogCliente(dialogo, lista, clienteEdicion);
        dialogo.setVisible(true);

        if (controlador.isGuardadoExitoso()) {
            ActualizarVista();
        }
    }

    private void buscarCliente() {
        String codigo = vista.txtBuscar.getText().trim();
        encontrado = null;
        vista.tablaClientes.clearSelection();

        if (codigo.isEmpty() || codigo.equalsIgnoreCase("Buscar por Código")) {
            return;
        }

        encontrado = lista.Buscar(codigo);
        if (encontrado == null) {
            Mensajes.Mostrar("El código " + codigo + " no existe en la lista.");
            return;
        }

        ProcesosCliente.MostrarEnFormulario(vista, encontrado.cliente);
        seleccionarFila(encontrado.cliente.getCodCliente());
        Mensajes.Mostrar("Cliente encontrado y seleccionado.");
    }

    private void desactivarCliente() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque el cliente que desea dar de baja.");
            return;
        }

        Cliente cliente = encontrado.cliente;
        String codigo = cliente.getCodCliente();
        boolean activoAnterior = cliente.isActivo();
        int respuesta = Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al cliente con código " + codigo + "?");
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        if (!ProcesosCliente.DesactivarCliente(lista, cliente)) {
            Mensajes.Mostrar(cliente.isActivo()
                    ? "No se pudo dar de baja al cliente."
                    : "El cliente ya se encuentra retirado.");
            return;
        }

        try {
            AlmacenarCliente.Guardar(lista);
            ActualizarVista();
            Mensajes.Mostrar("Cliente dado de baja correctamente.");
        } catch (IOException ex) {
            ProcesosCliente.RestaurarEstado(cliente, activoAnterior);
            Mensajes.Mostrar("No se pudo guardar la baja del cliente: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionCliente) {
            if (encontrado == null) {
                Mensajes.Mostrar("Seleccione o busque el cliente que desea editar.");
            } else {
                abrirDialogo(encontrado.cliente);
            }
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar) {
            buscarCliente();
        } else if (origen == vista.btnEstadoCliente) {
            desactivarCliente();
        } else if (origen == vista.cboEstado) {
            encontrado = null;
            vista.tablaClientes.clearSelection();
            Mostrar.DatosClientes(vista.tablaClientes, lista, estadoSeleccionado());
        }
    }
}
