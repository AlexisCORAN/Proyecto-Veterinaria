package Controlador;

import Arboles.ArbolCitas;
import Colas.ColaCitas;
import Entidades.Cita;
import EstructuraArrayList.ListaServicios;
import ListaEnlazadas.ListaClientes;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.ListaPersonal;
import Persistencia.AlmacenarCitas;
import Persistencia.AlmacenarCitas.RegistroCitas;
import Persistencia.AlmacenarCliente;
import Persistencia.AlmacenarMascota;
import Persistencia.AlmacenarPersonal;
import Persistencia.AlmacenarServicio;
import Pilas.PilaCitas;
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosCita;
import Vista.DialogAtenderCita;
import Vista.DialogNuevaCita;
import Vista.pnlCitas;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ControlGestionCitas implements ActionListener {

    private final pnlCitas vista;
    private final RegistroCitas registro;
    private ListaClientes clientes;
    private ListaMascotas mascotas;
    private ListaPersonal personal;
    private ListaServicios servicios;
    private Cita encontrado;
    private ArbolCitas indiceArbol;

    public ControlGestionCitas(pnlCitas vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.registro = AlmacenarCitas.Recuperar();
        this.clientes = AlmacenarCliente.Recuperar();
        this.mascotas = AlmacenarMascota.Recuperar();
        this.personal = AlmacenarPersonal.Recuperar();
        this.servicios = AlmacenarServicio.Recuperar();
        
        vista.btnNuevo.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.btnAtenderCita.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        
        vista.tablaCitas.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                seleccionarDesdeTabla();
            }
        });
        actualizarVista();
    }

    private ColaCitas pendientes() {
        return registro.getPendientes();
    }

    private PilaCitas historial() {
        return registro.getHistorial();
    }

    private void actualizarVista() {
        ProcesosCita.LimpiarEntradas(vista);
        encontrado = null;
        vista.tablaCitas.clearSelection();
        Mostrar.DatosCitas(vista.tablaCitas, pendientes(), historial(), estadoSeleccionado());
        
        
        indiceArbol = ProcesosCita.ConstruirIndice(pendientes(), historial());
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void seleccionarDesdeTabla() {
        int fila = vista.tablaCitas.getSelectedRow();
        if (fila < 0) {
            encontrado = null;
            return;
        }
        int modelo = vista.tablaCitas.convertRowIndexToModel(fila);
        String codigo = String.valueOf(vista.tablaCitas.getModel().getValueAt(modelo, 1));
        
        
        encontrado = indiceArbol.Buscar(codigo);
        
        if (encontrado != null) {
            vista.txtBuscar.setText(codigo);
        }
    }

    private void seleccionarFila(String codigo) {
        for (int fila = 0; fila < vista.tablaCitas.getRowCount(); fila++) {
            int modelo = vista.tablaCitas.convertRowIndexToModel(fila);
            if (codigo.equalsIgnoreCase(String.valueOf(vista.tablaCitas.getModel().getValueAt(modelo, 1)))) {
                vista.tablaCitas.setRowSelectionInterval(fila, fila);
                return;
            }
        }
    }

    private void crearCita() {
        try {
            clientes = AlmacenarCliente.Recuperar();
            mascotas = AlmacenarMascota.Recuperar();
            personal = AlmacenarPersonal.Recuperar();
            servicios = AlmacenarServicio.Recuperar();
        } catch (IOException | ClassNotFoundException ex) {
            Mensajes.Mostrar("No se pudieron cargar los datos actuales para registrar una cita: "
                    + ex.getMessage());
            return;
        }
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogNuevaCita dialogo = new DialogNuevaCita(framePadre, true);
        ControlDialogCita controlador = new ControlDialogCita(dialogo, registro,
                clientes, mascotas, personal, servicios);
        dialogo.setVisible(true);
        if (controlador.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void atenderCita() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque la cita que desea atender.");
            return;
        }
        if (!"Pendiente".equalsIgnoreCase(encontrado.getEstado())) {
            Mensajes.Mostrar("Solo se pueden atender citas pendientes.");
            return;
        }
        Window ventanaPadre = SwingUtilities.getWindowAncestor(vista);
        Frame framePadre = ventanaPadre instanceof Frame ? (Frame) ventanaPadre : null;
        DialogAtenderCita dialogo = new DialogAtenderCita(framePadre, true);
        ControlDialogAtenderCita controlador = new ControlDialogAtenderCita(
                dialogo, registro, encontrado);
        dialogo.setVisible(true);
        if (controlador.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void cancelarCita() {
        if (encontrado == null) {
            Mensajes.Mostrar("Seleccione o busque la cita que desea cancelar.");
            return;
        }
        if (!"Pendiente".equalsIgnoreCase(encontrado.getEstado())) {
            Mensajes.Mostrar("Solo se pueden cancelar citas pendientes.");
            return;
        }
        int respuesta = Mensajes.Preguntar("Confirmar cancelación",
                "¿Desea cancelar la cita " + encontrado.getCodCita() + "?");
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        String estadoAnterior = encontrado.getEstado();
        String detallesAnteriores = encontrado.getDetallesAtencion();
        Cita cancelada;
        try {
            cancelada = ProcesosCita.CancelarCita(pendientes(), historial(), encontrado.getCodCita());
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
            return;
        }
        
        try {
            AlmacenarCitas.Guardar(registro);
            actualizarVista();
            Mensajes.Mostrar("Cita cancelada correctamente.");
        } catch (IOException ex) {
            ProcesosCita.RestaurarCitaPendiente(pendientes(), historial(), cancelada,
                    estadoAnterior, detallesAnteriores);
            Mensajes.Mostrar("No se pudo guardar la cancelación: " + ex.getMessage());
        }
    }

    private void buscarCita() {
        String codigo = vista.txtBuscar.getText().trim();
        encontrado = null;
        vista.tablaCitas.clearSelection();
        
        if (codigo.isBlank() || codigo.equalsIgnoreCase("Buscar por Código")) {
            return;
        }
        
        
        encontrado = indiceArbol.Buscar(codigo);
        
        if (encontrado == null) {
            Mensajes.Mostrar("No existe una cita con código " + codigo + ".");
            return;
        }
        seleccionarFila(codigo);
        Mensajes.Mostrar("Cita encontrada y seleccionada.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            crearCita();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar) {
            buscarCita();
        } else if (origen == vista.btnAtenderCita) {
            atenderCita();
        } else if (origen == vista.btnCancelar) {
            cancelarCita();
        } else if (origen == vista.cboEstado) {
            encontrado = null;
            vista.tablaCitas.clearSelection();
            Mostrar.DatosCitas(vista.tablaCitas, pendientes(), historial(), estadoSeleccionado());
        }
    }
}