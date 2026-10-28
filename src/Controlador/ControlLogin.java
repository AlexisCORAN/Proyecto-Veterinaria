



package Controlador;

import Arreglos.ArregloUsuarios;
import Entidades.Usuario;
import Procesos.Mensajes;
import Procesos.ProcesosLogin;
import Vista.frmLogin;
import Vista.frmMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class ControlLogin implements ActionListener {

    private final frmLogin vista;
    private final ArregloUsuarios misUsuarios; 

    public ControlLogin(frmLogin vista) {
        this.vista = vista;
        this.misUsuarios = new ArregloUsuarios(); 
        
        this.vista.btnIngresar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnIngresar) {
            ingresarAlSistema();
        }
    }

    private void ingresarAlSistema() {
        String[] credenciales = ProcesosLogin.LeerCredenciales(vista);
        String user = credenciales[0];
        String pass = credenciales[1];

        if (user.isEmpty() || pass.isEmpty()) {
            Mensajes.Mostrar("Por favor, ingrese usuario y contraseña.");
            return;
        }

        Usuario usuarioValido = misUsuarios.ValidarAcceso(user, pass);

        if (usuarioValido != null) {
            Mensajes.Mostrar("Bienvenido al sistema, " + usuarioValido.getRol());
            
            frmMenu vistaMenu = new frmMenu();
            ControlMenu controlMenu = new ControlMenu(vistaMenu);
            vistaMenu.pack();
            vistaMenu.pnlContenedor.revalidate();
            vistaMenu.pnlContenedor.repaint();
            java.awt.Rectangle areaPantalla = java.awt.GraphicsEnvironment
                    .getLocalGraphicsEnvironment().getMaximumWindowBounds();
            int ancho = Math.min(1100, areaPantalla.width);
            int alto = Math.min(700, areaPantalla.height);
            vistaMenu.setMinimumSize(new java.awt.Dimension(
                    Math.min(900, ancho), Math.min(600, alto)));
            vistaMenu.setSize(ancho, alto);
            vistaMenu.setLocationRelativeTo(null);
            vistaMenu.setVisible(true);
            
            vista.dispose();
            
        } else {
            Mensajes.Mostrar("Usuario o contraseña incorrectos.");
            ProcesosLogin.LimpiarEntradas(vista);
        }
    }
}