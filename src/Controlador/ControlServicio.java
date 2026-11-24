package Controlador;

import Entidades.Servicio;
import EstructuraArrayList.ListaServicios;
import Persistencia.AlmacenarServicio;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosServicio;
import Procesos.UtilidadesUI;
import Vista.DialogNuevoServicio;
import Vista.pnlServicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ControlServicio implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código, Nombre o Descripción";
    private static final int COL_CODIGO = 1;

    private final pnlServicio vista;
    private final ListaServicios lista;
    private Servicio servicioSeleccionado;

    public ControlServicio(pnlServicio vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarServicio.Recuperar();

        registrarEventos();
        cargarTabla("");
    }

    private void registrarEventos() {
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionServicio.addActionListener(this);
        vista.btnEliminarServicio.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaServicio.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarServicioDeTabla();
            }
        });

        vista.tablaServicio.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int fila = vista.tablaServicio.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    vista.tablaServicio.setRowSelectionInterval(fila, fila);
                    seleccionarServicioDeTabla();
                    if (servicioSeleccionado != null) {
                        abrirDialogo(servicioSeleccionado);
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
        vista.tablaServicio.clearSelection();
        Mostrar.DatosServicios(vista.tablaServicio, lista, estadoSeleccionado(), texto);
        servicioSeleccionado = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = servicioSeleccionado != null;
        vista.btnEdicionServicio.setEnabled(haySeleccion);
        vista.btnEliminarServicio.setEnabled(haySeleccion && servicioSeleccionado.isActivo());
    }

    private void seleccionarServicioDeTabla() {
        int fila = vista.tablaServicio.getSelectedRow();
        if (fila < 0) {
            servicioSeleccionado = null;
        } else {
            int filaModelo = vista.tablaServicio.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaServicio.getModel().getValueAt(filaModelo, COL_CODIGO));
            servicioSeleccionado = lista.Buscar(codigo);
        }
        actualizarEstadoBotones();
    }

    private void abrirDialogo(Servicio servicioEdicion) {
        DialogNuevoServicio dialogo = new DialogNuevoServicio(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogServicio controlDialogo = new ControlDialogServicio(dialogo, lista, servicioEdicion);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarServicio() {
        String texto = UtilidadesUI.textoBusqueda(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla(texto);

        if (!texto.isEmpty() && vista.tablaServicio.getRowCount() == 1) {
            vista.tablaServicio.setRowSelectionInterval(0, 0);
        }
    }

    private void desactivarServicio() {
        if (servicioSeleccionado == null) {
            return;
        }
        Servicio servicio = servicioSeleccionado;

        if (!Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al servicio con código " + servicio.getCodServicio() + "?")) {
            return;
        }

        if (!ProcesosServicio.desactivar(lista, servicio)) {
            Mensajes.Mostrar("No se pudo dar de baja al servicio.");
            return;
        }

        try {
            AlmacenarServicio.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Servicio dado de baja correctamente.");
        } catch (IOException ex) {
            servicio.setActivo(true);
            Mensajes.Mostrar("No se pudo guardar la baja del servicio: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionServicio && servicioSeleccionado != null) {
            abrirDialogo(servicioSeleccionado);
        } else if (origen == vista.btnEliminarServicio) {
            desactivarServicio();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar || origen == vista.cboEstado) {
            buscarServicio();
        }
    }
}