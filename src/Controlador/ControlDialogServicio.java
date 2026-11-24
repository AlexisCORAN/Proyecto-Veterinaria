package Controlador;

import Entidades.Servicio;
import EstructuraArrayList.ListaServicios;
import Persistencia.AlmacenarServicio;
import Procesos.Mensajes;
import Procesos.ProcesosServicio;
import Vista.DialogNuevoServicio;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ControlDialogServicio implements ActionListener {

    private final DialogNuevoServicio vista;
    private final ListaServicios lista;
    private final Servicio servicioEdicion;
    private boolean guardadoExitoso;

    public ControlDialogServicio(DialogNuevoServicio vista, ListaServicios lista, Servicio servicioEdicion) {
        this.vista = vista;
        this.lista = lista;
        this.servicioEdicion = servicioEdicion;

        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);

        vista.txtCodigoServicio.setEditable(false);
        if (servicioEdicion != null) {
            vista.nombrePanel.setText("Modificar Información del Servicio");
            mostrarServicioEnFormulario(servicioEdicion);
        } else {
            vista.nombrePanel.setText("Registrar Nuevo Servicio");
            vista.txtCodigoServicio.setText(ProcesosServicio.generarCodigo(lista));
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void mostrarServicioEnFormulario(Servicio s) {
        vista.txtCodigoServicio.setText(s.getCodServicio());
        vista.txtNombreServicio.setText(s.getNombre());
        vista.txtDescripcionServicio.setText(s.getDescripcion());
        vista.txtPrecioServicio.setText(String.valueOf(s.getPrecio()));
    }

    private Servicio leerFormulario() {
        String precioTexto = vista.txtPrecioServicio.getText().trim().replace(',', '.');
        if (precioTexto.isEmpty()) {
            throw new IllegalArgumentException("El precio del servicio es obligatorio.");
        }

        double precio;
        try {
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El precio debe ser un número válido.", ex);
        }

        return new Servicio(
                vista.txtCodigoServicio.getText().trim(),
                vista.txtNombreServicio.getText().trim(),
                vista.txtDescripcionServicio.getText().trim(),
                precio);
    }

    private void guardar() {
        try {
            if (servicioEdicion == null) {
                guardarNuevoServicio();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudieron guardar los cambios del servicio: " + ex.getMessage());
        }
    }

    private void guardarNuevoServicio() throws IOException {
        Servicio nuevo = leerFormulario();
        ProcesosServicio.registrar(lista, nuevo);
        try {
            AlmacenarServicio.Guardar(lista);
        } catch (IOException ex) {
            lista.Eliminar(nuevo.getCodServicio());
            throw ex;
        }
        finalizar("Servicio registrado correctamente.");
    }

    private void guardarEdicion() throws IOException {
        Servicio copiaAnterior = ProcesosServicio.actualizar(lista, servicioEdicion, leerFormulario());
        try {
            AlmacenarServicio.Guardar(lista);
        } catch (IOException ex) {
            ProcesosServicio.revertir(servicioEdicion, copiaAnterior);
            throw ex;
        }
        finalizar("Servicio actualizado correctamente.");
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