package Controlador;
import Vista.*;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JOptionPane;




public class ControlMenu implements ActionListener {

    private final frmMenu vista;
    
    
    private final pnlClientes panelClientes;
    private final pnlMascota panelMascotas;
    private final pnlPersonal panelPersonal;
    private final pnlServicio panelServicios;
    private final pnlCitas panelCitas;
    private ControlCliente controlClientes;
    private ControlMascota controlMascotas;
    private ControlPersonal controlPersonal;
    private ControlServicio controlServicios;
    private ControlGestionCitas controlCitas;

    public ControlMenu(frmMenu vista) {
        this.vista = vista;
        
        panelClientes = new pnlClientes();
        panelMascotas = new pnlMascota();
        panelPersonal = new pnlPersonal();
        panelServicios = new pnlServicio();
        panelCitas = new pnlCitas();

        this.vista.pnlContenedor.add(panelClientes, "CLIENTES");
        this.vista.pnlContenedor.add(panelMascotas, "MASCOTAS");
        this.vista.pnlContenedor.add(panelPersonal, "PERSONAL");
        this.vista.pnlContenedor.add(panelServicios, "SERVICIOS");
        this.vista.pnlContenedor.add(panelCitas, "CITAS");

        this.vista.btnMenuClientes.addActionListener(this);
        this.vista.btnMenuMascotas.addActionListener(this);
        this.vista.btnMenuPersonal.addActionListener(this);
        this.vista.btnMenuServicios.addActionListener(this);
        this.vista.btnMenuCitas.addActionListener(this);

        conectarControlador("CLIENTES");
    }

    private void conectarControlador(String nombrePanel) {
        try {
            switch (nombrePanel) {
                case "CLIENTES" -> {
                    if (controlClientes == null) {
                        controlClientes = new ControlCliente(panelClientes);
                    }
                }
                case "MASCOTAS" -> {
                    if (controlMascotas == null) {
                        controlMascotas = new ControlMascota(panelMascotas);
                    }
                }
                case "PERSONAL" -> {
                    if (controlPersonal == null) {
                        controlPersonal = new ControlPersonal(panelPersonal);
                    }
                }
                case "SERVICIOS" -> {
                    if (controlServicios == null) {
                        controlServicios = new ControlServicio(panelServicios);
                    }
                }
                case "CITAS" -> {
                    if (controlCitas == null) {
                        controlCitas = new ControlGestionCitas(panelCitas);
                    }
                }
                default -> throw new IllegalArgumentException("No existe el panel " + nombrePanel + ".");
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(vista,
                    "No se pudo inicializar el módulo " + nombrePanel + ": " + ex.getMessage(),
                    "Error al cargar el módulo", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CardLayout layout = (CardLayout) vista.pnlContenedor.getLayout();

        if (e.getSource() == vista.btnMenuClientes) {
            layout.show(vista.pnlContenedor, "CLIENTES");
            conectarControlador("CLIENTES");
        } else if (e.getSource() == vista.btnMenuMascotas) {
            layout.show(vista.pnlContenedor, "MASCOTAS");
            conectarControlador("MASCOTAS");
        } else if (e.getSource() == vista.btnMenuPersonal) {
            layout.show(vista.pnlContenedor, "PERSONAL");
            conectarControlador("PERSONAL");
        } else if (e.getSource() == vista.btnMenuServicios) {
            layout.show(vista.pnlContenedor, "SERVICIOS");
            conectarControlador("SERVICIOS");
        } else if (e.getSource() == vista.btnMenuCitas) {
            layout.show(vista.pnlContenedor, "CITAS");
            conectarControlador("CITAS");
        }
    }
}
