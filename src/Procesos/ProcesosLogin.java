
package Procesos;

import Vista.frmLogin;


public class ProcesosLogin {

    public static void LimpiarEntradas(frmLogin fl) {
        fl.txtUsuario.setText("");
        fl.txtPassword.setText("");
        fl.txtUsuario.requestFocus();
    }

    public static String[] LeerCredenciales(frmLogin fl) {
        String user = fl.txtUsuario.getText();
        String pass = new String(fl.txtPassword.getPassword());
        return new String[]{user, pass};
    }
}
