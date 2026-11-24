package Controlador;

import Arboles.ArbolCitas;
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
import Procesos.Mensajes;
import Procesos.Mostrar;
import Procesos.ProcesosCita;
import Procesos.UtilidadesUI;
import Vista.DialogAtenderCita;
import Vista.DialogNuevaCita;
import Vista.pnlCitas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class ControlGestionCitas implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código de cita o de mascota";
    private static final int COL_CODIGO = 0;

    private final pnlCitas vista;
    private final RegistroCitas registro;
    private Cita citaSeleccionada;
    private ArbolCitas indiceArbol;
    private boolean ordenAscendente = true;

    public ControlGestionCitas(pnlCitas vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.registro = AlmacenarCitas.Recuperar();

        registrarEventos();
        actualizarVista();
    }

    
    
    
    private void registrarEventos() {
        vista.btnNuevo.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.btnAtenderCita.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        vista.btnOrdenarFecha.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarDesdeTabla();
            }
        });

        vista.tablaCitas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int fila = vista.tablaCitas.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    vista.tablaCitas.setRowSelectionInterval(fila, fila);
                    if (citaSeleccionada != null) {
                        abrirDialogo(citaSeleccionada);
                    }
                }
            }
        });

        actualizarTextoOrden();
    }

    
    
    
    private void actualizarVista() {
        UtilidadesUI.restablecerPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        indiceArbol = ProcesosCita.construirIndice(registro.getPendientes(), registro.getHistorial());
        cargarTabla("");
    }

    private void cargarTabla(String texto) {
        vista.tablaCitas.clearSelection();
        Mostrar.DatosCitas(
                vista.tablaCitas,
                registro.getPendientes(),
                registro.getHistorial(),
                estadoSeleccionado(),
                texto,
                ordenAscendente);
        citaSeleccionada = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private boolean esCitaPendienteSeleccionada() {
        return citaSeleccionada != null
                && ProcesosCita.ESTADO_PENDIENTE.equalsIgnoreCase(citaSeleccionada.getEstado());
    }

    private void actualizarEstadoBotones() {
        boolean esPendiente = esCitaPendienteSeleccionada();
        vista.btnAtenderCita.setEnabled(esPendiente);
        vista.btnCancelar.setEnabled(esPendiente);
    }

    private void seleccionarDesdeTabla() {
        int fila = vista.tablaCitas.getSelectedRow();
        if (fila < 0) {
            citaSeleccionada = null;
        } else {
            int filaModelo = vista.tablaCitas.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaCitas.getModel().getValueAt(filaModelo, COL_CODIGO));
            citaSeleccionada = indiceArbol.Buscar(codigo);
        }
        actualizarEstadoBotones();
    }

    
    
    
    private void abrirDialogo(Cita cita) {
        ListaClientes clientes;
        ListaMascotas mascotas;
        ListaPersonal personal;
        ListaServicios servicios;
        try {
            clientes = AlmacenarCliente.Recuperar();
            mascotas = AlmacenarMascota.Recuperar();
            personal = AlmacenarPersonal.Recuperar();
            servicios = AlmacenarServicio.Recuperar();
        } catch (IOException | ClassNotFoundException ex) {
            Mensajes.Mostrar("Error al cargar datos actualizados: " + ex.getMessage());
            return;
        }

        DialogNuevaCita dialogo = new DialogNuevaCita(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogCita controlDialogo = new ControlDialogCita(
                dialogo, registro, clientes, mascotas, personal, servicios, cita);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
        if (cita != null) {
            mostrarDiagnosticoSiExiste(cita);
        }
    }

    private void mostrarDiagnosticoSiExiste(Cita cita) {
        String detalles = cita.getDetallesAtencion();
        if (ProcesosCita.ESTADO_ATENDIDA.equalsIgnoreCase(cita.getEstado())
                && detalles != null && !detalles.isBlank()) {
            Mensajes.Mostrar("Diagnóstico (" + cita.getCodCita() + "):\n\n" + detalles);
        }
    }

    private void buscarCita() {
        String texto = UtilidadesUI.textoBusqueda(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla(texto);

        if (!texto.isEmpty() && vista.tablaCitas.getRowCount() == 1) {
            vista.tablaCitas.setRowSelectionInterval(0, 0);
        }
    }

    private void alternarOrdenFecha() {
        ordenAscendente = !ordenAscendente;
        actualizarTextoOrden();
        buscarCita();
    }

    private void actualizarTextoOrden() {
        vista.btnOrdenarFecha.setText(ordenAscendente
                ? "Fecha: Ordenamiento Ascendente"
                : "Fecha: Ordenamiento Descendente");
    }

    private void atenderCita() {
        if (!esCitaPendienteSeleccionada()) {
            return;
        }
        DialogAtenderCita dialogo = new DialogAtenderCita(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogAtenderCita controlDialogo
                = new ControlDialogAtenderCita(dialogo, registro, citaSeleccionada);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

        private void cancelarCita() {
        if (!esCitaPendienteSeleccionada()) {
            return;
        }
        Cita cita = citaSeleccionada;

        if (!Mensajes.Preguntar("Confirmar cancelación",
                "¿Desea cancelar la cita " + cita.getCodCita() + "?")) {
            return;
        }

        ProcesosCita.cancelar(registro.getPendientes(), registro.getHistorial(), cita.getCodCita());
        actualizarVista();
        try {
            AlmacenarCitas.Guardar(registro);
            Mensajes.Mostrar("Cita cancelada correctamente.");
        } catch (IOException ex) {
            Mensajes.Mostrar("La cita se canceló, pero no se pudo guardar en el archivo: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnAtenderCita) {
            atenderCita();
        } else if (origen == vista.btnCancelar) {
            cancelarCita();
        } else if (origen == vista.btnOrdenarFecha) {
            alternarOrdenFecha();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar || origen == vista.cboEstado) {
            buscarCita();
        }
    }
}