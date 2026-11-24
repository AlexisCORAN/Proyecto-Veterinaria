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
import Vista.pnlGestionarCitas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;

public class ControlGestionarCitas implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código de Cita o Mascota";
    private static final int COL_CODIGO = 0;

    private final pnlGestionarCitas vista;
    private final RegistroCitas registro;
    private final ListaClientes clientes;
    private final ListaMascotas mascotas;
    private final ListaPersonal personal;
    private final ListaServicios servicios;
    private ArbolCitas indiceCitas;
    private Cita citaSeleccionada;
    private boolean ordenAscendente = true;

    public ControlGestionarCitas(pnlGestionarCitas vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.registro = AlmacenarCitas.Recuperar();
        this.clientes = AlmacenarCliente.Recuperar();
        this.mascotas = AlmacenarMascota.Recuperar();
        this.personal = AlmacenarPersonal.Recuperar();
        this.servicios = AlmacenarServicio.Recuperar();

        configurarFiltros();
        registrarEventos();
        actualizarVista();
    }

    private void configurarFiltros() {
        vista.cboEstado.setModel(new DefaultComboBoxModel<>(new String[]{
            "TODOS", "PENDIENTES", "ATENDIDAS", "CANCELADAS"
        }));
        actualizarTextoOrden();
    }

    private void actualizarTextoOrden() {
        vista.btnOrdenarFecha.setText(ordenAscendente
                ? "Orden: Más próximas primero"
                : "Orden: Más recientes primero");
    }

    private void registrarEventos() {
        vista.btnNuevo.addActionListener(this);
        vista.btnAtenderCita.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);
        vista.btnOrdenarFecha.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaCitas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarCitaDeTabla();
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
                    seleccionarCitaDeTabla();
                    if (citaSeleccionada != null) {
                        abrirDialogoCita(citaSeleccionada);
                    }
                }
            }
        });
    }

    private void actualizarVista() {
        indiceCitas = ProcesosCita.construirIndice(registro.getPendientes(), registro.getHistorial());
        vista.tablaCitas.setToolTipText("Último movimiento: "
                + ProcesosCita.ultimoMovimiento(registro.getHistorial())
                + " | Doble clic en una fila para editar o ver detalle");
        UtilidadesUI.restablecerPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla("");
    }

    private void cargarTabla(String texto) {
        vista.tablaCitas.clearSelection();
        Mostrar.DatosCitas(vista.tablaCitas, registro.getPendientes(), registro.getHistorial(),
                estadoSeleccionado(), texto, ordenAscendente);
        citaSeleccionada = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = citaSeleccionada != null;
        boolean esPendiente = haySeleccion
                && ProcesosCita.ESTADO_PENDIENTE.equalsIgnoreCase(citaSeleccionada.getEstado());

        vista.btnAtenderCita.setEnabled(esPendiente);
        vista.btnCancelar.setEnabled(esPendiente);
    }

    private void seleccionarCitaDeTabla() {
        int fila = vista.tablaCitas.getSelectedRow();
        if (fila < 0) {
            citaSeleccionada = null;
        } else {
            int filaModelo = vista.tablaCitas.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaCitas.getModel().getValueAt(filaModelo, COL_CODIGO));
            citaSeleccionada = indiceCitas.Buscar(codigo);
        }
        actualizarEstadoBotones();
    }

    private void abrirDialogoCita(Cita citaEdicion) {
        DialogNuevaCita dialogo = new DialogNuevaCita(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogCita control = new ControlDialogCita(dialogo, registro, clientes,
                mascotas, personal, servicios, citaEdicion);
        dialogo.setVisible(true);

        if (control.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void atenderCita() {
        if (citaSeleccionada == null) {
            return;
        }
        DialogAtenderCita dialogo = new DialogAtenderCita(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogAtenderCita control = new ControlDialogAtenderCita(dialogo, registro, citaSeleccionada);
        dialogo.setVisible(true);

        if (control.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void cancelarCita() {
        Cita cita = citaSeleccionada;
        if (cita == null) {
            return;
        }
        if (!Mensajes.Preguntar("Confirmar cancelación",
                "¿Desea cancelar la cita " + cita.getCodCita() + "?")) {
            return;
        }
        String estadoAnterior = cita.getEstado();
        try {
            ProcesosCita.cancelar(registro.getPendientes(), registro.getHistorial(),
                    cita.getCodCita());
            AlmacenarCitas.Guardar(registro);
            actualizarVista();
            Mensajes.Mostrar("Cita cancelada correctamente.");
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            registro.getHistorial().Desapilar();
            cita.setEstado(estadoAnterior);
            registro.getPendientes().Encolar(cita);
            actualizarVista();
            Mensajes.Mostrar("No se pudo guardar la cancelación en el archivo: " + ex.getMessage());
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogoCita(null);
        } else if (origen == vista.btnAtenderCita) {
            atenderCita();
        } else if (origen == vista.btnCancelar) {
            cancelarCita();
        } else if (origen == vista.btnOrdenarFecha) {
            alternarOrdenFecha();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar
                || origen == vista.cboEstado) {
            buscarCita();
        }
    }
}