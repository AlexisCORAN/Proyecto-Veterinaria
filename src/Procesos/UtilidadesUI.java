package Procesos;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UtilidadesUI {

    private static final Color FONDO_NORMAL = new Color(240, 240, 240);
    private static final Color FONDO_ACTIVO = new Color(0, 153, 153);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FUENTE_ACTIVA = new Font("Segoe UI", Font.BOLD, 14);

    public static Frame obtenerFramePadre(Component componente) {
        Window ventana = SwingUtilities.getWindowAncestor(componente);
        return ventana instanceof Frame ? (Frame) ventana : null;
    }

    
    public static void configurarPlaceholder(JTextField campo, String placeholder) {
        restablecerPlaceholder(campo, placeholder);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().trim().equalsIgnoreCase(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    restablecerPlaceholder(campo, placeholder);
                }
            }
        });
    }

    
    public static void restablecerPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.setForeground(Color.GRAY);
    }

    
    public static String textoBusqueda(JTextField campo, String placeholder) {
        String texto = campo.getText().trim();
        return texto.equalsIgnoreCase(placeholder) ? "" : texto;
    }

    public static void resaltarBotonMenu(JButton botonActivo, JButton... todosLosBotones) {
        for (JButton btn : todosLosBotones) {
            boolean activo = (btn == botonActivo);
            btn.setBackground(activo ? FONDO_ACTIVO : FONDO_NORMAL);
            btn.setForeground(activo ? Color.WHITE : Color.BLACK);
            btn.setFont(activo ? FUENTE_ACTIVA : FUENTE_NORMAL);
        }
    }
}