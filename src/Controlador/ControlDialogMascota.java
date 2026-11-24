package Controlador;

import Entidades.Cliente;
import Entidades.Mascota;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import Persistencia.AlmacenarMascota;
import Procesos.Mensajes;
import Procesos.ProcesosMascota;
import Vista.DialogNuevaMascota;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.DefaultComboBoxModel;


public class ControlDialogMascota implements ActionListener {

    private final DialogNuevaMascota vista;
    private final ListaMascotas lista;
    private final ListaClientes clientes;
    private final Mascota mascotaEdicion;
    private boolean guardadoExitoso;

    public ControlDialogMascota(DialogNuevaMascota vista, ListaMascotas lista,
            ListaClientes clientes, Mascota mascotaEdicion) {
        this.vista = vista;
        this.lista = lista;
        this.clientes = clientes;
        this.mascotaEdicion = mascotaEdicion;
        
        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        vista.btnBuscarCliente.addActionListener(this);
        vista.txtBuscarCliente.addActionListener(this);
        
        vista.cboEspecie.setModel(new DefaultComboBoxModel<>(new String[]{
            "Perro", "Gato", "Ave", "Otro"
        }));
        
        if (mascotaEdicion != null) {
            vista.nombrePanel.setText("Modificar Información de Mascota");
            ProcesosMascota.MostrarMascotaEnFormulario(vista, mascotaEdicion, clientes);
        } else {
            vista.txtCodigoMascota.setText(ProcesosMascota.GenerarCodigo(lista));
            vista.txtCodigoMascota.setEditable(false);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void guardar() throws IOException {
        if (mascotaEdicion == null) {
            Mascota nueva = ProcesosMascota.RegistrarMascota(vista, lista, clientes);
            try {
                AlmacenarMascota.Guardar(lista);
            } catch (IOException ex) {
                lista.Eliminar(nueva.getCodMascota());
                throw ex;
            }
            Mensajes.Mostrar("Mascota registrada correctamente.");
        } else {
            Mascota anterior = ProcesosMascota.ActualizarMascotaDesdeFormulario(
                    vista, lista, clientes, mascotaEdicion);
            try {
                AlmacenarMascota.Guardar(lista);
            } catch (IOException ex) {
                ProcesosMascota.RestaurarMascota(mascotaEdicion, anterior);
                throw ex;
            }
            Mensajes.Mostrar("Mascota actualizada correctamente.");
        }
        guardadoExitoso = true;
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnCancelar) {
            vista.dispose();
            
        } else if (e.getSource() == vista.btnBuscarCliente || e.getSource() == vista.txtBuscarCliente) {
            String dniABuscar = vista.txtBuscarCliente.getText().trim();
            
            if (dniABuscar.isEmpty()) {
                Mensajes.Mostrar("Ingrese un DNI para buscar al dueño.");
                return;
            }

            NodoCliente nodo = clientes.BuscarPorDni(dniABuscar);
            
            if (nodo != null && nodo.cliente.isActivo()) {
                Cliente c = nodo.cliente;
                vista.txtCodigoCliente1.setText(c.getCodCliente() + " - " + c.getNombres() + " " + c.getApellidos());
                Mensajes.Mostrar("Propietario asignado correctamente.");
            } else {
                vista.txtCodigoCliente1.setText(""); 
                Mensajes.Mostrar("No se encontró un cliente activo con el DNI proporcionado.");
            }
            
        } else if (e.getSource() == vista.btnGuardar) {
            try {
                guardar();
            } catch (IllegalArgumentException ex) {
                Mensajes.Mostrar(ex.getMessage());
            } catch (IOException ex) {
                Mensajes.Mostrar("No se pudieron guardar los cambios de la mascota: " + ex.getMessage());
            }
        }
    }
}