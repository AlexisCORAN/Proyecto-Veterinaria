package Controlador;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import Persistencia.AlmacenarCliente;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosCliente;
import Procesos.UtilidadesUI;
import Vista.DialogNuevoCliente;
import Vista.pnlClientes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;

public class ControlCliente implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código, DNI o Nombre";
    private static final int COL_CODIGO = 1;

    private final pnlClientes vista;
    private final ListaClientes lista;
    private Cliente clienteSeleccionado;

    public ControlCliente(pnlClientes vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarCliente.Recuperar();

        configurarFiltros();
        registrarEventos();
        cargarTabla("");
    }

    private void configurarFiltros() {
        vista.cboEstado.setModel(new DefaultComboBoxModel<>(new String[]{
            "TODOS", "ACTIVOS", "RETIRADOS"
        }));
    }

    private void registrarEventos() {
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionCliente.addActionListener(this);
        vista.btnEstadoCliente.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarClienteDeTabla();
            }
        });

        vista.tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int fila = vista.tablaClientes.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    vista.tablaClientes.setRowSelectionInterval(fila, fila);
                    seleccionarClienteDeTabla();
                    if (clienteSeleccionado != null) {
                        abrirDialogo(clienteSeleccionado);
                    }
                }
            }
        });
    }

    private void actualizarVista() {
        UtilidadesUI.restablecerPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla("");
    }

    private void cargarTabla(String texto) {
        vista.tablaClientes.clearSelection();
        Mostrar.DatosClientes(vista.tablaClientes, lista, estadoSeleccionado(), texto);
        clienteSeleccionado = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = clienteSeleccionado != null;
        vista.btnEdicionCliente.setEnabled(haySeleccion);
        vista.btnEstadoCliente.setEnabled(haySeleccion && clienteSeleccionado.isActivo());
    }

    private void seleccionarClienteDeTabla() {
        int fila = vista.tablaClientes.getSelectedRow();
        if (fila < 0) {
            clienteSeleccionado = null;
        } else {
            int filaModelo = vista.tablaClientes.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaClientes.getModel().getValueAt(filaModelo, COL_CODIGO));
            NodoCliente nodo = lista.Buscar(codigo);
            clienteSeleccionado = (nodo == null) ? null : nodo.cliente;
        }
        actualizarEstadoBotones();
    }

    private void abrirDialogo(Cliente clienteEdicion) {
        DialogNuevoCliente dialogo = new DialogNuevoCliente(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogCliente controlDialogo = new ControlDialogCliente(dialogo, lista, clienteEdicion);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarCliente() {
        String texto = UtilidadesUI.textoBusqueda(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla(texto);

        if (!texto.isEmpty() && vista.tablaClientes.getRowCount() == 1) {
            vista.tablaClientes.setRowSelectionInterval(0, 0);
        }
    }

    private void desactivarCliente() {
        Cliente cliente = clienteSeleccionado;
        if (cliente == null) {
            return;
        }

        if (!Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al cliente con código " + cliente.getCodCliente() + "?")) {
            return;
        }

        if (!ProcesosCliente.desactivar(lista, cliente)) {
            Mensajes.Mostrar("No se pudo dar de baja al cliente.");
            return;
        }

        try {
            AlmacenarCliente.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Cliente dado de baja correctamente.");
        } catch (IOException ex) {
            cliente.setActivo(true);
            Mensajes.Mostrar("No se pudo guardar la baja del cliente: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionCliente && clienteSeleccionado != null) {
            abrirDialogo(clienteSeleccionado);
        } else if (origen == vista.btnEstadoCliente) {
            desactivarCliente();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar || origen == vista.cboEstado) {
            buscarCliente();
        }
    }
}