package Principal;

import Controlador.ControlLogin;
import Vista.frmLogin;
import javax.swing.UIManager;


public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("No se pudo aplicar el tema visual: " + ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            
            frmLogin vistaLogin = new frmLogin();
            
            ControlLogin controladorLogin = new ControlLogin(vistaLogin);
            
            vistaLogin.setLocationRelativeTo(null);
            vistaLogin.setVisible(true);

        });
    }
}