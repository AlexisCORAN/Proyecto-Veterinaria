package Controlador;

import EstructuraArrayList.ListaServicios;
import Entidades.Servicio;
import Persistencia.AlmacenarServicio;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosServicio;
import Vista.DialogNuevoServicio;
import Vista.pnlServicio;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ControlServicio implements ActionListener {

    private final pnlServicio vista;
    private final ListaServicios lista;
    private Servicio encontrado;

    public ControlServicio(pnlServicio vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarServicio.Recuperar();
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionServicio.addActionListener(this);
        vista.btnEliminarServicio.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        vista.tablaServicio.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarDesdeTabla();
            }
        });
        actualizarVista();
    }

    private void actualizarVista() {
        ProcesosServicio.LimpiarEntradas(vista);
        encontrado = null;
        vista.tablaServicio.clearSelection();
        Mostrar.DatosServicios(vista.tablaServicio, lista, estadoSeleccionado());
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void seleccionarDesdeTabla() {
        int fila = vista.tablaServicio.getSelectedRow();
        if (fila < 0) {
            encontrado = null;
            return;
        }
        int modelo = vista.tablaServicio.convertRowIndexToModel(fila);
        String codigo = String.valueOf(vista.tablaServicio.getModel().getValueAt(modelo, 1));
        encontrado = lista.Buscar(codigo);
        if (encontrado != null) {
            vista.txtBuscar.setText(codigo);
        }
    }

    private void seleccionarFila(String codigo) {
        for (int fila = 0; fila < vista.tablaServicio.getRowCount(); fila++) {
            int modelo = vista.tablaServicio.convertRowIndexToModel(fila);
            if (codigo.equalsIgnoreCase(String.valueOf(vista.tablaServicio.getModel().getValueAt(modelo, 1)))) {
                vista.tablaServicio.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void abrirDialogo(Servicio servicioEdicion) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogNuevoServicio dialogo = new DialogNuevoServicio(framePadre, true);
        ControlDialogServicio controlador = new ControlDialogServicio(dialogo, lista, servicioEdicion);
        dialogo.setVisible(true);
        if (controlador.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarServicio() {
        String codigo = vista.txtBuscar.getText().trim();
        encontrado = null;
        vista.tablaServicio.clearSelection();
        if (codigo.isEmpty() || codigo.equalsIgnoreCase("Buscar por Código")) {
            return;
        }
        encontrado = lista.Buscar(codigo);
        if (encontrado == null) {
            Mensajes.Mostrar("No existe un servicio con código " + codigo + ".");
            return;
        }
        seleccionarFila(codigo);
        Mensajes.Mostrar("Servicio encontrado y seleccionado.");
    }

    private void desactivarServicio() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque el servicio que desea dar de baja.");
            return;
        }
        if (!encontrado.isActivo()) {
            Mensajes.Mostrar("El servicio ya se encuentra retirado.");
            return;
        }
        String codigo = encontrado.getCodServicio();
        int respuesta = Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al servicio con código " + codigo + "?");
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        boolean estadoAnterior = encontrado.isActivo();
        if (!ProcesosServicio.DesactivarServicio(lista, encontrado)) {
            Mensajes.Mostrar("No se pudo dar de baja al servicio.");
            return;
        }
        try {
            AlmacenarServicio.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Servicio dado de baja correctamente.");
        } catch (IOException ex) {
            ProcesosServicio.RestaurarEstado(encontrado, estadoAnterior);
            Mensajes.Mostrar("No se pudo guardar la baja del servicio: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionServicio) {
            if (encontrado == null) {
                Mensajes.Mostrar("Seleccione o busque el servicio que desea editar.");
            } else {
                abrirDialogo(encontrado);
            }
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar) {
            buscarServicio();
        } else if (origen == vista.btnEliminarServicio) {
            desactivarServicio();
        } else if (origen == vista.cboEstado) {
            encontrado = null;
            vista.tablaServicio.clearSelection();
            Mostrar.DatosServicios(vista.tablaServicio, lista, estadoSeleccionado());
        }
    }
}
