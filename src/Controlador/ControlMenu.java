package Controlador;

import Procesos.UtilidadesUI;
import Vista.*;
import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class ControlMenu {

    private enum Modulo {
        CLIENTES, MASCOTAS, CITAS, SERVICIOS, PERSONAL
    }

    private final frmMenu vista;

    private final pnlClientes panelClientes;
    private final pnlMascotas panelMascotas;
    private final pnlGestionarCitas panelCitas;
    private final pnlServicio panelServicios;
    private final pnlPersonal panelPersonal;

    public ControlMenu(frmMenu vista) {
        this.vista = vista;

        panelClientes = new pnlClientes();
        panelMascotas = new pnlMascotas();
        panelCitas = new pnlGestionarCitas();
        panelServicios = new pnlServicio();
        panelPersonal = new pnlPersonal();

        this.vista.pnlContenedor.add(panelClientes, Modulo.CLIENTES.name());
        this.vista.pnlContenedor.add(panelMascotas, Modulo.MASCOTAS.name());
        this.vista.pnlContenedor.add(panelCitas, Modulo.CITAS.name());
        this.vista.pnlContenedor.add(panelServicios, Modulo.SERVICIOS.name());
        this.vista.pnlContenedor.add(panelPersonal, Modulo.PERSONAL.name());

        registrarEventos();
        mostrarModulo(Modulo.CLIENTES, vista.btnMenuClientes);
    }

    private void registrarEventos() {
        vista.btnMenuClientes.addActionListener(e -> mostrarModulo(Modulo.CLIENTES, vista.btnMenuClientes));
        vista.btnMenuMascotas.addActionListener(e -> mostrarModulo(Modulo.MASCOTAS, vista.btnMenuMascotas));
        vista.btnMenuCitas.addActionListener(e -> mostrarModulo(Modulo.CITAS, vista.btnMenuCitas));
        vista.btnMenuServicios.addActionListener(e -> mostrarModulo(Modulo.SERVICIOS, vista.btnMenuServicios));
        vista.btnMenuPersonal.addActionListener(e -> mostrarModulo(Modulo.PERSONAL, vista.btnMenuPersonal));
    }

    private void mostrarModulo(Modulo modulo, JButton botonActivo) {
        if (!conectarControlador(modulo)) {
            return;
        }

        CardLayout layout = (CardLayout) vista.pnlContenedor.getLayout();
        layout.show(vista.pnlContenedor, modulo.name());

        UtilidadesUI.resaltarBotonMenu(botonActivo,
                vista.btnMenuClientes,
                vista.btnMenuMascotas,
                vista.btnMenuCitas,
                vista.btnMenuServicios,
                vista.btnMenuPersonal);
    }

    private boolean conectarControlador(Modulo modulo) {
        try {
            switch (modulo) {
                case CLIENTES -> new ControlCliente(panelClientes);
                case MASCOTAS -> new ControlMascota(panelMascotas);
                case CITAS -> new ControlGestionarCitas(panelCitas);
                case SERVICIOS -> new ControlServicio(panelServicios);
                case PERSONAL -> new ControlPersonal(panelPersonal);
            }
            return true;
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo inicializar el módulo " + modulo.name() + ": " + ex.getMessage(),
                    "Error al cargar el módulo", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}