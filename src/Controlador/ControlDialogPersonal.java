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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;

public class ControlDialogPersonal implements ActionListener {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

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

        vista.txtCodigoPersonal.setEditable(false);
        if (personalEdicion != null) {
            vista.nombrePanel.setText("Modificar Información del Personal");
            mostrarPersonalEnFormulario(personalEdicion);
        } else {
            vista.nombrePanel.setText("Registrar Nuevo Personal");
            vista.txtCodigoPersonal.setText(ProcesosPersonal.generarCodigo(lista));
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void mostrarPersonalEnFormulario(Personal p) {
        vista.txtCodigoPersonal.setText(p.getCodPersonal());
        vista.txtDniCliente.setText(p.getDni());
        vista.txtNombresCliente.setText(p.getNombres());
        vista.txtApellidosCliente.setText(p.getApellidos());
        vista.txtTelefonoCliente.setText(p.getTelefono());
        vista.cboCargo.setSelectedItem(p.getCargo());

        String fecha = p.getFechaNacimiento();
        if (fecha != null && !fecha.isEmpty()) {
            try {
                vista.spinFechaNacimiento.setValue(FORMATO_FECHA.parse(fecha));
            } catch (ParseException ex) {
                Mensajes.Mostrar("La fecha de nacimiento guardada no es válida. Ingrese una nueva.");
            }
        }
    }

    private Personal leerFormulario() {
        Date fecha = (Date) vista.spinFechaNacimiento.getValue();
        if (fecha == null) {
            throw new IllegalArgumentException("Seleccione una fecha de nacimiento válida.");
        }
        if (fecha.after(new Date())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        Object cargo = vista.cboCargo.getSelectedItem();
        return new Personal(
                vista.txtCodigoPersonal.getText().trim(),
                vista.txtDniCliente.getText().trim(),
                vista.txtNombresCliente.getText().trim(),
                vista.txtApellidosCliente.getText().trim(),
                vista.txtTelefonoCliente.getText().trim(),
                FORMATO_FECHA.format(fecha),
                cargo == null ? "" : cargo.toString());
    }

    private void guardar() {
        try {
            if (personalEdicion == null) {
                guardarNuevoPersonal();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudieron guardar los cambios del personal: " + ex.getMessage());
        }
    }

    private void guardarNuevoPersonal() throws IOException {
        Personal nuevo = leerFormulario();
        ProcesosPersonal.registrar(lista, nuevo);
        try {
            AlmacenarPersonal.Guardar(lista);
        } catch (IOException ex) {
            lista.Eliminar(nuevo.getCodPersonal());
            throw ex;
        }
        finalizar("Personal registrado correctamente.");
    }

    private void guardarEdicion() throws IOException {
        Personal copiaAnterior = ProcesosPersonal.actualizar(lista, personalEdicion, leerFormulario());
        try {
            AlmacenarPersonal.Guardar(lista);
        } catch (IOException ex) {
            ProcesosPersonal.revertir(personalEdicion, copiaAnterior);
            throw ex;
        }
        finalizar("Personal actualizado correctamente.");
    }

    private void finalizar(String mensaje) {
        guardadoExitoso = true;
        Mensajes.Mostrar(mensaje);
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