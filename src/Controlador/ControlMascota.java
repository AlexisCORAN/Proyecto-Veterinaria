
package Controlador;

import Entidades.Mascota;
import ListaEnlazadas.ListaClientes;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.NodoMascota;
import Persistencia.AlmacenarCliente;
import Persistencia.AlmacenarMascota;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosMascota;
import Vista.DialogNuevaMascota;
import Vista.pnlMascota;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ControlMascota implements ActionListener {

    private final pnlMascota vista;
    private final ListaMascotas lista;
    private ListaClientes clientes;
    private Mascota encontrado;

    public ControlMascota(pnlMascota vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarMascota.Recuperar();
        this.clientes = AlmacenarCliente.Recuperar();
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionCliente.addActionListener(this);
        vista.btnEstadoCliente.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        vista.tablaMascota.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarDesdeTabla();
            }
        });
        actualizarVista();
    }

    private void actualizarVista() {
        ProcesosMascota.LimpiarEntradas(vista);
        encontrado = null;
        vista.tablaMascota.clearSelection();
        Mostrar.DatosMascotas(vista.tablaMascota, lista, estadoSeleccionado());
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void seleccionarDesdeTabla() {
        int fila = vista.tablaMascota.getSelectedRow();
        if (fila < 0) {
            encontrado = null;
            return;
        }
        int modelo = vista.tablaMascota.convertRowIndexToModel(fila);
        String codigo = String.valueOf(vista.tablaMascota.getModel().getValueAt(modelo, 1));
        NodoMascota nodo = lista.Buscar(codigo);
        encontrado = nodo == null ? null : nodo.mascota;
        if (encontrado != null) {
            vista.txtBuscar.setText(codigo);
        }
    }

    private void seleccionarFila(String codigo) {
        for (int fila = 0; fila < vista.tablaMascota.getRowCount(); fila++) {
            int modelo = vista.tablaMascota.convertRowIndexToModel(fila);
            if (codigo.equalsIgnoreCase(String.valueOf(vista.tablaMascota.getModel().getValueAt(modelo, 1)))) {
                vista.tablaMascota.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void abrirDialogo(Mascota mascotaEdicion) {
        try {
            clientes = AlmacenarCliente.Recuperar();
        } catch (IOException | ClassNotFoundException ex) {
            Mensajes.Mostrar("No se pudo cargar la lista actual de clientes: " + ex.getMessage());
            return;
        }
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogNuevaMascota dialogo = new DialogNuevaMascota(framePadre, true);
        ControlDialogMascota controlador = new ControlDialogMascota(dialogo, lista, clientes, mascotaEdicion);
        dialogo.setVisible(true);
        if (controlador.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarMascota() {
        String codigo = vista.txtBuscar.getText().trim();
        encontrado = null;
        vista.tablaMascota.clearSelection();
        if (codigo.isEmpty() || codigo.equalsIgnoreCase("Buscar por Código")) {
            return;
        }
        NodoMascota nodo = lista.Buscar(codigo);
        if (nodo == null) {
            Mensajes.Mostrar("No existe una mascota con código " + codigo + ".");
            return;
        }
        encontrado = nodo.mascota;
        seleccionarFila(codigo);
        Mensajes.Mostrar("Mascota encontrada y seleccionada.");
    }

    private void desactivarMascota() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque la mascota que desea dar de baja.");
            return;
        }
        if (!encontrado.isActivo()) {
            Mensajes.Mostrar("La mascota ya se encuentra retirada.");
            return;
        }
        String codigo = encontrado.getCodMascota();
        int respuesta = Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja a la mascota con código " + codigo + "?");
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        boolean estadoAnterior = encontrado.isActivo();
        if (!ProcesosMascota.DesactivarMascota(lista, encontrado)) {
            Mensajes.Mostrar("No se pudo dar de baja a la mascota.");
            return;
        }
        try {
            AlmacenarMascota.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Mascota dada de baja correctamente.");
        } catch (IOException ex) {
            ProcesosMascota.RestaurarEstado(encontrado, estadoAnterior);
            Mensajes.Mostrar("No se pudo guardar la baja de la mascota: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionCliente) {
            if (encontrado == null) {
                Mensajes.Mostrar("Seleccione o busque la mascota que desea editar.");
            } else {
                abrirDialogo(encontrado);
            }
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar) {
            buscarMascota();
        } else if (origen == vista.btnEstadoCliente) {
            desactivarMascota();
        } else if (origen == vista.cboEstado) {
            encontrado = null;
            vista.tablaMascota.clearSelection();
            Mostrar.DatosMascotas(vista.tablaMascota, lista, estadoSeleccionado());
        }
    }
}
