package Controlador;

import Entidades.Personal;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoPersonal;
import Persistencia.AlmacenarPersonal;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosPersonal;
import Procesos.UtilidadesUI;
import Vista.DialogNuevoPersonal;
import Vista.pnlPersonal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ControlPersonal implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código, DNI, Nombre o Cargo";
    private static final int COL_CODIGO = 1;

    private final pnlPersonal vista;
    private final ListaPersonal lista;
    private Personal personalSeleccionado;

    public ControlPersonal(pnlPersonal vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarPersonal.Recuperar();

        registrarEventos();
        cargarTabla("");
    }

    private void registrarEventos() {
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionPersonal.addActionListener(this);
        vista.btnEstadoCliente.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaPersonal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPersonalDeTabla();
            }
        });

        vista.tablaPersonal.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int fila = vista.tablaPersonal.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    vista.tablaPersonal.setRowSelectionInterval(fila, fila);
                    seleccionarPersonalDeTabla();
                    if (personalSeleccionado != null) {
                        abrirDialogo(personalSeleccionado);
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
        vista.tablaPersonal.clearSelection();
        Mostrar.DatosPersonal(vista.tablaPersonal, lista, estadoSeleccionado(), texto);
        personalSeleccionado = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = personalSeleccionado != null;
        vista.btnEdicionPersonal.setEnabled(haySeleccion);
        vista.btnEstadoCliente.setEnabled(haySeleccion && personalSeleccionado.isActivo());
    }

    private void seleccionarPersonalDeTabla() {
        int fila = vista.tablaPersonal.getSelectedRow();
        if (fila < 0) {
            personalSeleccionado = null;
        } else {
            int filaModelo = vista.tablaPersonal.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaPersonal.getModel().getValueAt(filaModelo, COL_CODIGO));
            NodoPersonal nodo = lista.Buscar(codigo);
            personalSeleccionado = (nodo == null) ? null : nodo.personal;
        }
        actualizarEstadoBotones();
    }

    private void abrirDialogo(Personal personalEdicion) {
        DialogNuevoPersonal dialogo = new DialogNuevoPersonal(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogPersonal controlDialogo = new ControlDialogPersonal(dialogo, lista, personalEdicion);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarPersonal() {
        String texto = UtilidadesUI.textoBusqueda(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla(texto);

        if (!texto.isEmpty() && vista.tablaPersonal.getRowCount() == 1) {
            vista.tablaPersonal.setRowSelectionInterval(0, 0);
        }
    }

    private void desactivarPersonal() {
        if (personalSeleccionado == null) {
            return;
        }
        Personal personal = personalSeleccionado;

        if (!Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al personal con código " + personal.getCodPersonal() + "?")) {
            return;
        }

        if (!ProcesosPersonal.desactivar(lista, personal)) {
            Mensajes.Mostrar("No se pudo dar de baja al personal.");
            return;
        }

        try {
            AlmacenarPersonal.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Personal dado de baja correctamente.");
        } catch (IOException ex) {
            personal.setActivo(true);
            Mensajes.Mostrar("No se pudo guardar la baja del personal: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionPersonal && personalSeleccionado != null) {
            abrirDialogo(personalSeleccionado);
        } else if (origen == vista.btnEstadoCliente) {
            desactivarPersonal();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar || origen == vista.cboEstado) {
            buscarPersonal();
        }
    }
}