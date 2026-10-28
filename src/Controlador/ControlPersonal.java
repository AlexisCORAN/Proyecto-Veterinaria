
package Controlador;

import Entidades.Personal;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoPersonal;
import Persistencia.AlmacenarPersonal;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosPersonal; 
import Vista.DialogNuevoPersonal;
import Vista.pnlPersonal;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


public class ControlPersonal implements ActionListener {

    private final pnlPersonal vista;
    private final ListaPersonal lista;
    private Personal encontrado;

    public ControlPersonal(pnlPersonal vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarPersonal.Recuperar();
        
        vista.btnNuevo.addActionListener(this);
        vista.btnEdicionPersonal.addActionListener(this);
        vista.btnEstadoCliente.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        
        vista.tablaPersonal.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarDesdeTabla();
            }
        });
        
        actualizarVista();
    }

    private void actualizarVista() {
        ProcesosPersonal.LimpiarEntradas(vista);
        encontrado = null;
        vista.tablaPersonal.clearSelection();
        Mostrar.DatosPersonal(vista.tablaPersonal, lista, estadoSeleccionado());
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void seleccionarDesdeTabla() {
        int fila = vista.tablaPersonal.getSelectedRow();
        if (fila < 0) {
            encontrado = null;
            return;
        }
        int filaModelo = vista.tablaPersonal.convertRowIndexToModel(fila);
        String codigo = String.valueOf(vista.tablaPersonal.getModel().getValueAt(filaModelo, 1));
        NodoPersonal nodo = lista.Buscar(codigo);
        encontrado = nodo == null ? null : nodo.personal;
        if (encontrado != null) {
            vista.txtBuscar.setText(codigo);
        }
    }

    private void seleccionarFila(String codigo) {
        for (int fila = 0; fila < vista.tablaPersonal.getRowCount(); fila++) {
            int modelo = vista.tablaPersonal.convertRowIndexToModel(fila);
            if (codigo.equalsIgnoreCase(String.valueOf(vista.tablaPersonal.getModel().getValueAt(modelo, 1)))) {
                vista.tablaPersonal.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void abrirDialogo(Personal personalEdicion) {
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogNuevoPersonal dialogo = new DialogNuevoPersonal(framePadre, true);
        ControlDialogPersonal controlador = new ControlDialogPersonal(dialogo, lista, personalEdicion);
        dialogo.setVisible(true);
        
        if (controlador.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarPersonal() {
        String codigo = vista.txtBuscar.getText().trim();
        encontrado = null;
        vista.tablaPersonal.clearSelection();
        
        if (codigo.isBlank() || codigo.equalsIgnoreCase("Buscar por Código")) {
            return;
        }
        
        NodoPersonal nodo = lista.Buscar(codigo);
        if (nodo == null) {
            Mensajes.Mostrar("No existe personal con código " + codigo + ".");
            return;
        }
        
        encontrado = nodo.personal;
        seleccionarFila(codigo);
        Mensajes.Mostrar("Personal encontrado y seleccionado.");
    }

    private void desactivarPersonal() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque el personal que desea dar de baja.");
            return;
        }
        if (!encontrado.isActivo()) {
            Mensajes.Mostrar("El personal ya se encuentra retirado.");
            return;
        }
        
        String codigo = encontrado.getCodPersonal();
        int respuesta = Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja al personal con código " + codigo + "?");
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        
        boolean estadoAnterior = encontrado.isActivo();
        
        if (!lista.Desactivar(codigo)) {
            Mensajes.Mostrar("No se pudo dar de baja al personal.");
            return;
        }
        
        try {
            AlmacenarPersonal.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Personal dado de baja correctamente.");
        } catch (IOException ex) {
            encontrado.setActivo(estadoAnterior); 
            Mensajes.Mostrar("No se pudo guardar la baja del personal: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionPersonal) {
            if (encontrado == null) {
                Mensajes.Mostrar("Seleccione o busque el personal que desea editar.");
            } else {
                abrirDialogo(encontrado);
            }
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar) {
            buscarPersonal();
        } else if (origen == vista.btnEstadoCliente) {
            desactivarPersonal();
        } else if (origen == vista.cboEstado) {
            encontrado = null;
            vista.tablaPersonal.clearSelection();
            Mostrar.DatosPersonal(vista.tablaPersonal, lista, estadoSeleccionado());
        }
    }
}