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
import Procesos.UtilidadesUI;
import Vista.DialogNuevaMascota;
import Vista.pnlMascotas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;

public class ControlMascota implements ActionListener {

    private static final String PLACEHOLDER_BUSQUEDA = "Buscar por Código, Nombre o Cod. Cliente";
    private static final int COL_CODIGO = 1;

    private final pnlMascotas vista;
    private final ListaMascotas lista;
    private final ListaClientes clientes;
    private Mascota mascotaSeleccionada;

    public ControlMascota(pnlMascotas vista) throws IOException, ClassNotFoundException {
        this.vista = vista;
        this.lista = AlmacenarMascota.Recuperar();
        this.clientes = AlmacenarCliente.Recuperar();

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
        vista.btnEdicionMascota.addActionListener(this);
        vista.btnEstadoMascota.addActionListener(this);
        vista.btnBuscar.addActionListener(this);
        vista.txtBuscar.addActionListener(this);
        vista.cboEstado.addActionListener(this);

        UtilidadesUI.configurarPlaceholder(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);

        vista.tablaMascotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarMascotaDeTabla();
            }
        });

        vista.tablaMascotas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }
                int fila = vista.tablaMascotas.rowAtPoint(e.getPoint());
                if (fila >= 0) {
                    vista.tablaMascotas.setRowSelectionInterval(fila, fila);
                    seleccionarMascotaDeTabla();
                    if (mascotaSeleccionada != null) {
                        abrirDialogo(mascotaSeleccionada);
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
        vista.tablaMascotas.clearSelection();
        Mostrar.DatosMascotas(vista.tablaMascotas, lista, estadoSeleccionado(), texto);
        mascotaSeleccionada = null;
        actualizarEstadoBotones();
    }

    private String estadoSeleccionado() {
        Object estado = vista.cboEstado.getSelectedItem();
        return estado == null ? "TODOS" : estado.toString();
    }

    private void actualizarEstadoBotones() {
        boolean haySeleccion = mascotaSeleccionada != null;
        vista.btnEdicionMascota.setEnabled(haySeleccion);
        vista.btnEstadoMascota.setEnabled(haySeleccion && mascotaSeleccionada.isActivo());
    }

    private void seleccionarMascotaDeTabla() {
        int fila = vista.tablaMascotas.getSelectedRow();
        if (fila < 0) {
            mascotaSeleccionada = null;
        } else {
            int filaModelo = vista.tablaMascotas.convertRowIndexToModel(fila);
            String codigo = String.valueOf(vista.tablaMascotas.getModel().getValueAt(filaModelo, COL_CODIGO));
            NodoMascota nodo = lista.Buscar(codigo);
            mascotaSeleccionada = (nodo == null) ? null : nodo.mascota;
        }
        actualizarEstadoBotones();
    }

    private void abrirDialogo(Mascota mascotaEdicion) {
        DialogNuevaMascota dialogo = new DialogNuevaMascota(UtilidadesUI.obtenerFramePadre(vista), true);
        ControlDialogMascota controlDialogo = new ControlDialogMascota(dialogo, lista, clientes, mascotaEdicion);
        dialogo.setVisible(true);

        if (controlDialogo.isGuardadoExitoso()) {
            actualizarVista();
        }
    }

    private void buscarMascota() {
        String texto = UtilidadesUI.textoBusqueda(vista.txtBuscar, PLACEHOLDER_BUSQUEDA);
        cargarTabla(texto);

        if (!texto.isEmpty() && vista.tablaMascotas.getRowCount() == 1) {
            vista.tablaMascotas.setRowSelectionInterval(0, 0);
        }
    }

    private void desactivarMascota() {
        Mascota mascota = mascotaSeleccionada;
        if (mascota == null) {
            return;
        }

        if (!Mensajes.Preguntar("Confirmar baja",
                "¿Desea dar de baja a la mascota con código " + mascota.getCodMascota() + "?")) {
            return;
        }

        if (!ProcesosMascota.desactivar(lista, mascota)) {
            Mensajes.Mostrar("No se pudo dar de baja a la mascota.");
            return;
        }

        try {
            AlmacenarMascota.Guardar(lista);
            actualizarVista();
            Mensajes.Mostrar("Mascota dada de baja correctamente.");
        } catch (IOException ex) {
            mascota.setActivo(true);
            Mensajes.Mostrar("No se pudo guardar la baja de la mascota: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnNuevo) {
            abrirDialogo(null);
        } else if (origen == vista.btnEdicionMascota && mascotaSeleccionada != null) {
            abrirDialogo(mascotaSeleccionada);
        } else if (origen == vista.btnEstadoMascota) {
            desactivarMascota();
        } else if (origen == vista.btnBuscar || origen == vista.txtBuscar
                || origen == vista.cboEstado) {
            buscarMascota();
        }
    }
}