package Controlador;

import Entidades.Cita;
import Persistencia.AlmacenarCitas;
import Persistencia.AlmacenarCitas.RegistroCitas;
import Procesos.Mensajes;
import Procesos.ProcesosCita;
import Vista.DialogAtenderCita;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControlDialogAtenderCita implements ActionListener {

    private final DialogAtenderCita vista;
    private final RegistroCitas registro;
    private final Cita cita;
    private boolean guardadoExitoso;

    public ControlDialogAtenderCita(DialogAtenderCita vista, RegistroCitas registro, Cita cita) {
        this.vista = vista;
        this.registro = registro;
        this.cita = cita;

        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);

        vista.nombrePanel.setText("Registrar Atención");
        vista.txtCodigoCita.setText(cita.getCodCita());
        vista.txtCodigoCita.setEditable(false);
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void guardar() {
        try {
            ProcesosCita.atender(registro.getPendientes(), registro.getHistorial(),
                    cita.getCodCita(), vista.txtAreaCita.getText());
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
            return;
        }

        guardadoExitoso = true;
        try {
            AlmacenarCitas.Guardar(registro);
            Mensajes.Mostrar("La cita fue atendida correctamente.");
        } catch (IOException ex) {
            Mensajes.Mostrar("La cita fue atendida, pero no se pudo guardar en el archivo: " + ex.getMessage());
        }
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnCancelar) {
            vista.dispose();
        } else if (e.getSource() == vista.btnGuardar) {
            guardar();
        }
    }
}