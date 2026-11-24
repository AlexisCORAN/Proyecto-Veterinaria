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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControlDialogMascota implements ActionListener {

    private static final String[] ESPECIES = {"Perro", "Gato", "Ave", "Otro"};

    private final DialogNuevaMascota vista;
    private final ListaMascotas lista;
    private final ListaClientes clientes;
    private final Mascota mascotaEdicion;
    private Cliente propietario;
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

        vista.txtBuscarCliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { invalidarPropietarioSiCambio(); }
            @Override
            public void removeUpdate(DocumentEvent e) { invalidarPropietarioSiCambio(); }
            @Override
            public void changedUpdate(DocumentEvent e) { invalidarPropietarioSiCambio(); }
        });

        vista.cboEspecie.setModel(new DefaultComboBoxModel<>(ESPECIES));
        vista.txtCodigoMascota.setEditable(false);

        if (mascotaEdicion != null) {
            vista.nombrePanel.setText("Modificar Información de Mascota");
            mostrarMascotaEnFormulario(mascotaEdicion);
        } else {
            vista.nombrePanel.setText("Registrar Nueva Mascota");
            vista.txtCodigoMascota.setText(ProcesosMascota.generarCodigo(lista));
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void mostrarMascotaEnFormulario(Mascota m) {
        vista.txtCodigoMascota.setText(m.getCodMascota());
        vista.txtNombresMascota.setText(m.getNombre());
        vista.cboEspecie.setSelectedItem(m.getEspecie());
        vista.txtRazaMascota.setText(m.getRaza());

        NodoCliente nodo = clientes.Buscar(m.getCodCliente());
        if (nodo != null) {
            vista.txtBuscarCliente.setText(nodo.cliente.getDni());
            asignarPropietario(nodo.cliente);
        } else {
            vista.txtCodigoCliente1.setText(m.getCodCliente());
        }
    }

    private Mascota leerFormulario() {
        if (propietario == null) {
            throw new IllegalArgumentException("Debe buscar y asignar un dueño usando el DNI.");
        }
        Object especie = vista.cboEspecie.getSelectedItem();
        return new Mascota(
                vista.txtCodigoMascota.getText().trim(),
                vista.txtNombresMascota.getText().trim(),
                especie == null ? "" : especie.toString(),
                vista.txtRazaMascota.getText().trim(),
                propietario.getCodCliente());
    }


    private void asignarPropietario(Cliente cliente) {
        propietario = cliente;
        vista.txtCodigoCliente1.setText(cliente == null ? ""
                : cliente.getCodCliente() + " - " + cliente.getNombres() + " " + cliente.getApellidos());
    }

    private void invalidarPropietarioSiCambio() {
        if (propietario != null && !propietario.getDni().equals(vista.txtBuscarCliente.getText().trim())) {
            asignarPropietario(null);
        }
    }

    private void buscarPropietario() {
        String dni = vista.txtBuscarCliente.getText().trim();
        if (dni.isEmpty()) {
            Mensajes.Mostrar("Ingrese un DNI para buscar al dueño.");
            return;
        }

        NodoCliente nodo = clientes.BuscarPorDni(dni);
        if (nodo != null && nodo.cliente.isActivo()) {
            asignarPropietario(nodo.cliente);
        } else {
            asignarPropietario(null);
            Mensajes.Mostrar("No se encontró un cliente activo con el DNI proporcionado.");
        }
    }

    private void guardar() {
        try {
            if (mascotaEdicion == null) {
                guardarNuevaMascota();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        } catch (IOException ex) {
            Mensajes.Mostrar("No se pudieron guardar los cambios de la mascota: " + ex.getMessage());
        }
    }

    private void guardarNuevaMascota() throws IOException {
        Mascota nueva = leerFormulario();
        ProcesosMascota.registrar(lista, clientes, nueva);
        try {
            AlmacenarMascota.Guardar(lista);
        } catch (IOException ex) {
            lista.Eliminar(nueva.getCodMascota());
            throw ex;
        }
        finalizar("Mascota registrada correctamente.");
    }

    private void guardarEdicion() throws IOException {
        Mascota copiaAnterior = ProcesosMascota.actualizar(clientes, mascotaEdicion, leerFormulario());
        try {
            AlmacenarMascota.Guardar(lista);
        } catch (IOException ex) {
            ProcesosMascota.revertir(mascotaEdicion, copiaAnterior);
            throw ex;
        }
        finalizar("Mascota actualizada correctamente.");
    }

    private void finalizar(String mensaje) {
        guardadoExitoso = true;
        Mensajes.Mostrar(mensaje);
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnCancelar) {
            vista.dispose();
        } else if (origen == vista.btnBuscarCliente || origen == vista.txtBuscarCliente) {
            buscarPropietario();
        } else if (origen == vista.btnGuardar) {
            guardar();
        }
    }
}