package Controlador;

import EstructuraArrayList.ListaServicios;
import Entidades.Servicio;
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
        if (servicioEdicion != null) {
            vista.nombrePanel.setText("Modificar Información del Servicio");
            ProcesosServicio.MostrarServicioEnFormulario(vista, servicioEdicion);
        } else {
            vista.nombrePanel.setText("Nuevo Servicio");
            vista.txtCodigoServicio.setText(ProcesosServicio.GenerarCodigo(lista));
            vista.txtCodigoServicio.setEditable(false);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void guardar() throws IOException {
        if (servicioEdicion == null) {
            Servicio nuevo = ProcesosServicio.RegistrarServicio(vista, lista);
            try {
                AlmacenarServicio.Guardar(lista);
            } catch (IOException ex) {
                lista.Eliminar(nuevo.getCodServicio());
                throw ex;
            }
            Mensajes.Mostrar("Servicio registrado correctamente.");
        } else {
            Servicio anterior = ProcesosServicio.ActualizarServicioDesdeFormulario(
                    vista, lista, servicioEdicion);
            try {
                AlmacenarServicio.Guardar(lista);
            } catch (IOException ex) {
                ProcesosServicio.RestaurarServicio(servicioEdicion, anterior);
                throw ex;
            }
            Mensajes.Mostrar("Servicio actualizado correctamente.");
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
                Mensajes.Mostrar("No se pudieron guardar los cambios del servicio: " + ex.getMessage());
            }
        }
    }
}
