package Controlador;

import Procesos.UtilidadesUI;
import Vista.*;
import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ControlMenu {

    private enum Modulo {
        CLIENTES, MASCOTAS, CITAS, SERVICIOS, PERSONAL
    }

    private final frmMenu vista;

    public ControlMenu(frmMenu vista) {
        this.vista = vista;
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
        try {
            JPanel panel;
            switch (modulo) {
                case CLIENTES -> {
                    pnlClientes p = new pnlClientes();
                    new ControlCliente(p);
                    panel = p;
                }
                case MASCOTAS -> {
                    pnlMascotas p = new pnlMascotas();
                    new ControlMascota(p);
                    panel = p;
                }
                case CITAS -> {
                    pnlGestionarCitas p = new pnlGestionarCitas();
                    new ControlGestionarCitas(p);
                    panel = p;
                }
                case SERVICIOS -> {
                    pnlServicio p = new pnlServicio();
                    new ControlServicio(p);
                    panel = p;
                }
                case PERSONAL -> {
                    pnlPersonal p = new pnlPersonal();
                    new ControlPersonal(p);
                    panel = p;
                }
                default -> {
                    return;
                }
            }

            vista.pnlContenedor.removeAll();
            vista.pnlContenedor.add(panel, modulo.name());

            CardLayout layout = (CardLayout) vista.pnlContenedor.getLayout();
            layout.show(vista.pnlContenedor, modulo.name());
            vista.pnlContenedor.revalidate();
            vista.pnlContenedor.repaint();

            UtilidadesUI.resaltarBotonMenu(botonActivo,
                    vista.btnMenuClientes,
                    vista.btnMenuMascotas,
                    vista.btnMenuCitas,
                    vista.btnMenuServicios,
                    vista.btnMenuPersonal);
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo inicializar el módulo " + modulo.name() + ": " + ex.getMessage(),
                    "Error al cargar el módulo", JOptionPane.ERROR_MESSAGE);
        }
    }
}