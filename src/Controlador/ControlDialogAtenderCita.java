package Controlador;

import Colas.ColaCitas;
import Entidades.Cita;
import Persistencia.AlmacenarCitas;
import Persistencia.AlmacenarCitas.RegistroCitas;
import Pilas.PilaCitas;
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
        ColaCitas pendientes = registro.getPendientes();
        PilaCitas historial = registro.getHistorial();
        String detallesAnteriores = cita.getDetallesAtencion();

        try {
            Cita atendida = ProcesosCita.atender(pendientes, historial,
                    cita.getCodCita(), vista.txtAreaCita.getText());
            try {
                AlmacenarCitas.Guardar(registro);
            } catch (IOException ex) {
                ProcesosCita.restaurarPendiente(pendientes, historial, atendida, detallesAnteriores);
                throw ex;
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
            return;
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudo guardar la atención: " + ex.getMessage());
            return;
        }

        guardadoExitoso = true;
        Mensajes.Mostrar("La cita fue atendida correctamente.");
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