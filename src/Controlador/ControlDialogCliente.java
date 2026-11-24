package Controlador;

import Entidades.Cliente;
import ListaEnlazadas.ListaClientes;
import Persistencia.AlmacenarCliente;
import Procesos.Mensajes;
import Procesos.ProcesosCliente;
import Vista.DialogNuevoCliente;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControlDialogCliente implements ActionListener {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("dd/MM/yyyy");

    private final DialogNuevoCliente vista;
    private final ListaClientes lista;
    private final Cliente clienteEdicion;
    private boolean guardadoExitoso;

    public ControlDialogCliente(DialogNuevoCliente vista, ListaClientes lista, Cliente clienteEdicion) {
        this.vista = vista;
        this.lista = lista;
        this.clienteEdicion = clienteEdicion;

        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);

        vista.txtCodigoCliente.setEditable(false);
        if (clienteEdicion != null) {
            vista.nombrePanel.setText("Modificar Información de Cliente");
            mostrarClienteEnFormulario(clienteEdicion);
        } else {
            vista.txtCodigoCliente.setText(ProcesosCliente.generarCodigo(lista));
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

   

    private void mostrarClienteEnFormulario(Cliente c) {
        vista.txtCodigoCliente.setText(c.getCodCliente());
        vista.txtDniCliente.setText(c.getDni());
        vista.txtNombresCliente.setText(c.getNombres());
        vista.txtApellidosCliente.setText(c.getApellidos());
        vista.txtTelefonoCliente.setText(c.getTelefono());

        String fecha = c.getFechaNacimiento();
        if (fecha != null && !fecha.isEmpty()) {
            try {
                vista.spinFechaNacimiento.setValue(FORMATO_FECHA.parse(fecha));
            } catch (ParseException ex) {
                Mensajes.Mostrar("La fecha de nacimiento guardada no es válida. Ingrese una nueva.");
            }
        }
    }

    private Cliente leerFormulario() {
        Date fecha = (Date) vista.spinFechaNacimiento.getValue();
        if (fecha.after(new Date())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        return new Cliente(
                vista.txtCodigoCliente.getText().trim(),
                vista.txtDniCliente.getText().trim(),
                vista.txtNombresCliente.getText().trim(),
                vista.txtApellidosCliente.getText().trim(),
                vista.txtTelefonoCliente.getText().trim(),
                FORMATO_FECHA.format(fecha));
    }

   
    private void guardar() {
        try {
            if (clienteEdicion == null) {
                guardarNuevoCliente();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudieron guardar los cambios del cliente: " + ex.getMessage());
        }
    }

    private void guardarNuevoCliente() throws IOException {
        Cliente nuevo = leerFormulario();
        ProcesosCliente.registrar(lista, nuevo);
        try {
            AlmacenarCliente.Guardar(lista);
        } catch (IOException ex) {
            lista.Eliminar(nuevo);
            throw ex;
        }
        finalizar("Cliente registrado exitosamente.");
    }

    private void guardarEdicion() throws IOException {
        Cliente copiaAnterior = ProcesosCliente.actualizar(lista, clienteEdicion, leerFormulario());
        try {
            AlmacenarCliente.Guardar(lista);
        } catch (IOException ex) {
            ProcesosCliente.revertir(clienteEdicion, copiaAnterior);
            throw ex;
        }
        finalizar("Cliente actualizado correctamente.");
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