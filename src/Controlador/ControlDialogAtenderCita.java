
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

    private ColaCitas pendientes() {
        return registro.getPendientes();
    }

    private PilaCitas historial() {
        return registro.getHistorial();
    }

    private void guardar() throws IOException {
        
        String diagnostico = vista.txtAreaCita.getText().trim();
        
        
        if (diagnostico.isEmpty()) {
            Mensajes.Mostrar("Debe escribir los detalles de la atención o el diagnóstico antes de guardar.");
            return;
        }

        
        String estadoAnterior = cita.getEstado();
        String detallesAnteriores = cita.getDetallesAtencion();
        
        
        Cita atendida = ProcesosCita.AtenderCita(pendientes(), historial(),
                cita.getCodCita(), diagnostico);
                
        try {
            AlmacenarCitas.Guardar(registro);
        } catch (IOException ex) {
            ProcesosCita.RestaurarCitaPendiente(pendientes(), historial(), atendida,
                    estadoAnterior, detallesAnteriores);
            throw ex;
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
            try {
                guardar();
            } catch (IllegalArgumentException ex) {
                Mensajes.Mostrar(ex.getMessage());
            } catch (IOException ex) {
                Mensajes.Mostrar("No se pudo guardar la atención: " + ex.getMessage());
            }
        }
    }
}