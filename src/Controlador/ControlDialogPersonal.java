package Controlador;

import Entidades.Personal;
import ListasDoblesEnlazadas.ListaPersonal;
import Persistencia.AlmacenarPersonal;
import Procesos.Mensajes;
import Procesos.ProcesosPersonal;
import Vista.DialogNuevoPersonal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpinnerDateModel;


public class ControlDialogPersonal implements ActionListener {

    private final DialogNuevoPersonal vista;
    private final ListaPersonal lista;
    private final Personal personalEdicion;
    private boolean guardadoExitoso;

    public ControlDialogPersonal(DialogNuevoPersonal vista, ListaPersonal lista, Personal personalEdicion) {
        this.vista = vista;
        this.lista = lista;
        this.personalEdicion = personalEdicion;
        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.cboCargo.setModel(new DefaultComboBoxModel<>(new String[]{
            "Veterinario", "Asistente", "Recepcionista", "Administrador"
        }));
        vista.spinFechaNacimiento.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        vista.spinFechaNacimiento.setEditor(
                new javax.swing.JSpinner.DateEditor(vista.spinFechaNacimiento, "dd/MM/yyyy"));
        if (personalEdicion != null) {
            vista.nombrePanel.setText("Modificar Información del Personal");
            ProcesosPersonal.MostrarPersonalEnFormulario(vista, personalEdicion);
        } else {
            vista.txtCodigoPersonal.setText(ProcesosPersonal.GenerarCodigo(lista));
            vista.txtCodigoPersonal.setEditable(false);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void guardar() throws IOException {
        if (personalEdicion == null) {
            Personal nuevo = ProcesosPersonal.RegistrarPersonal(vista, lista);
            try {
                AlmacenarPersonal.Guardar(lista);
            } catch (IOException ex) {
                lista.Eliminar(nuevo.getCodPersonal());
                throw ex;
            }
            Mensajes.Mostrar("Personal registrado correctamente.");
        } else {
            Personal anterior = ProcesosPersonal.ActualizarPersonalDesdeFormulario(
                    vista, lista, personalEdicion);
            try {
                AlmacenarPersonal.Guardar(lista);
            } catch (IOException ex) {
                ProcesosPersonal.RestaurarPersonal(personalEdicion, anterior);
                throw ex;
            }
            Mensajes.Mostrar("Personal actualizado correctamente.");
        }
        guardadoExitoso = true;
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
                Mensajes.Mostrar("No se pudieron guardar los cambios del personal: " + ex.getMessage());
            }
        }
    }
}
