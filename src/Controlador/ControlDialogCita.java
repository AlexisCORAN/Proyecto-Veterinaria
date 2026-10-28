
package Controlador;

import Entidades.Cita;
import Entidades.Mascota;
import Entidades.Personal;
import Entidades.Servicio;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.NodoMascota;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoPersonal;
import EstructuraArrayList.ListaServicios;
import Persistencia.AlmacenarCitas;
import Persistencia.AlmacenarCitas.RegistroCitas;
import Procesos.Mensajes;
import Procesos.ProcesosCita;
import Vista.DialogNuevaCita;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ControlDialogCita implements ActionListener {

    private final DialogNuevaCita vista;
    private final RegistroCitas registro;
    private final ListaClientes clientes;
    private final ListaMascotas mascotas;
    private final ListaPersonal personal;
    private final ListaServicios servicios;
    private final Map<String, Personal> opcionesPersonal = new HashMap<>();
    private final Map<String, Servicio> opcionesServicios = new HashMap<>();
    private final List<Servicio> serviciosSeleccionados = new ArrayList<>();
    private Mascota mascotaSeleccionada;
    private boolean guardadoExitoso;

    public ControlDialogCita(DialogNuevaCita vista, RegistroCitas registro, ListaClientes clientes,
            ListaMascotas mascotas, ListaPersonal personal, ListaServicios servicios) {
        this.vista = vista;
        this.registro = registro;
        this.clientes = clientes;
        this.mascotas = mascotas;
        this.personal = personal;
        this.servicios = servicios;


        vista.btnBuscarMascota.addActionListener(this);
        vista.txtBuscarMascota.addActionListener(this);
        vista.btnAgregar1.addActionListener(this); 
        vista.btnQuitar.addActionListener(this);
        vista.btnGuardar.addActionListener(this);
        vista.btnCancelar.addActionListener(this);
        
        vista.txtBuscarMascota.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { invalidarMascotaSiCambio(); }
            @Override
            public void removeUpdate(DocumentEvent e) { invalidarMascotaSiCambio(); }
            @Override
            public void changedUpdate(DocumentEvent e) { invalidarMascotaSiCambio(); }
        });
        
        configurarFechas();
        cargarOpciones();
        actualizarServicios();
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void configurarFechas() {
        Date ahora = new Date();
        vista.spinFecha.setModel(new SpinnerDateModel(ahora, null, null, Calendar.DAY_OF_MONTH));
        vista.spinFecha.setEditor(new javax.swing.JSpinner.DateEditor(vista.spinFecha, "dd/MM/yyyy"));
        vista.spinHora.setModel(new SpinnerDateModel(ahora, null, null, Calendar.MINUTE));
        vista.spinHora.setEditor(new javax.swing.JSpinner.DateEditor(vista.spinHora, "HH:mm"));
        
        
        vista.txtTotal.setEditable(false);
    }

    private void cargarOpciones() {
        vista.cboPersonal.removeAllItems();
        NodoPersonal nodoPersonal = personal.ini;
        while (nodoPersonal != null) {
            Personal persona = nodoPersonal.personal;
            if (persona.isActivo() && "Veterinario".equalsIgnoreCase(persona.getCargo())) {
                String opcion = persona.getCodPersonal() + " - " + persona.getNombres()
                        + " " + persona.getApellidos();
                opcionesPersonal.put(opcion, persona);
                vista.cboPersonal.addItem(opcion);
            }
            nodoPersonal = nodoPersonal.sig;
        }

        vista.cboServicios.removeAllItems();
        for (Servicio servicio : servicios.getServicios()) {
            if (servicio.isActivo()) {
                String opcion = servicio.getCodServicio() + " - " + servicio.getNombre();
                opcionesServicios.put(opcion, servicio);
                vista.cboServicios.addItem(opcion);
            }
        }
    }

    private void buscarMascota() {
        String dato = vista.txtBuscarMascota.getText().trim();
        mascotaSeleccionada = null;
        vista.txtNombrePaciente.setText("");
        
        if (dato.isBlank()) {
            Mensajes.Mostrar("Ingrese el código de la mascota o el DNI del cliente.");
            return;
        }

        NodoMascota nodoMascota = mascotas.Buscar(dato);
        if (nodoMascota != null) {
            seleccionarMascota(nodoMascota.mascota);
            return;
        }

        NodoCliente nodoCliente = clientes.BuscarPorDni(dato);
        if (nodoCliente == null || !nodoCliente.cliente.isActivo()) {
            Mensajes.Mostrar("No se encontró una mascota ni un cliente activo con ese dato.");
            return;
        }
        
        List<Mascota> mascotasCliente = new ArrayList<>();
        NodoMascota actual = mascotas.BuscarPorCliente(nodoCliente.cliente.getCodCliente()).ini;
        while (actual != null) {
            if (actual.mascota.isActivo()) {
                mascotasCliente.add(actual.mascota);
            }
            actual = actual.sig;
        }
        
        if (mascotasCliente.isEmpty()) {
            Mensajes.Mostrar("El cliente no tiene mascotas activas registradas.");
            return;
        }

        Mascota elegida = mascotasCliente.get(0);
        if (mascotasCliente.size() > 1) {
            String[] opciones = mascotasCliente.stream()
                    .map(m -> m.getCodMascota() + " - " + m.getNombre() + " (" + m.getEspecie() + ")")
                    .toArray(String[]::new);
            int seleccion = JOptionPane.showOptionDialog(vista, "Seleccione la mascota para la cita",
                    "Mascotas del cliente", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[0]);
            if (seleccion < 0) {
                return;
            }
            elegida = mascotasCliente.get(seleccion);
        }
        seleccionarMascota(elegida);
    }

    private void seleccionarMascota(Mascota mascota) {
        if (!mascota.isActivo()) {
            Mensajes.Mostrar("No se pueden crear citas para una mascota retirada.");
            return;
        }
        NodoCliente propietario = clientes.Buscar(mascota.getCodCliente());
        if (propietario == null || !propietario.cliente.isActivo()) {
            Mensajes.Mostrar("La mascota pertenece a un cliente retirado o inexistente.");
            return;
        }
        vista.txtBuscarMascota.setText(mascota.getCodMascota());
        mascotaSeleccionada = mascota;
        vista.txtNombrePaciente.setText(mascota.getNombre() + " - "
                + propietario.cliente.getNombres() + " " + propietario.cliente.getApellidos());
    }

    private void invalidarMascotaSiCambio() {
        if (mascotaSeleccionada != null
                && !mascotaSeleccionada.getCodMascota().equalsIgnoreCase(vista.txtBuscarMascota.getText().trim())) {
            mascotaSeleccionada = null;
            vista.txtNombrePaciente.setText("");
        }
    }

    private void agregarServicio() {
        Object seleccion = vista.cboServicios.getSelectedItem();
        Servicio servicio = seleccion == null ? null : opcionesServicios.get(seleccion.toString());
        if (servicio == null) {
            Mensajes.Mostrar("No hay servicios activos disponibles para agregar.");
            return;
        }
        if (serviciosSeleccionados.contains(servicio)) {
            Mensajes.Mostrar("Ese servicio ya está agregado a la cita.");
            return;
        }
        serviciosSeleccionados.add(servicio);
        actualizarServicios();
    }

    private void quitarServicio() {
        int filaSeleccionada = vista.tablaServicios.getSelectedRow();
        if (filaSeleccionada >= 0 && filaSeleccionada < serviciosSeleccionados.size()) {
            serviciosSeleccionados.remove(filaSeleccionada);
            actualizarServicios();
        } else {
            Mensajes.Mostrar("Seleccione un servicio de la tabla para quitarlo.");
        }
    }

    private void actualizarServicios() {
        String[] columnas = {"Código", "Nombre", "Descripción", "Precio (S/)"};
        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        double total = 0;
        for (Servicio servicio : serviciosSeleccionados) {
            modelo.addRow(new Object[]{servicio.getCodServicio(), servicio.getNombre(),
                servicio.getDescripcion(), servicio.getPrecio()});
            total += servicio.getPrecio();
        }
        vista.tablaServicios.setModel(modelo);
        
        vista.txtTotal.setText(String.valueOf(total)); 
    }

    private void guardar() throws IOException {
        
        
        if (mascotaSeleccionada == null) {
            throw new IllegalArgumentException("Debe buscar y seleccionar un paciente válido antes de guardar.");
        }
        if (serviciosSeleccionados.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un servicio a la cita.");
        }
        
        Object opcionPersonal = vista.cboPersonal.getSelectedItem();
        Personal seleccionado = opcionPersonal == null
                ? null : opcionesPersonal.get(opcionPersonal.toString());
        
        Date fecha = (Date) vista.spinFecha.getValue();
        Date hora = (Date) vista.spinHora.getValue();
        String fechaTexto = new SimpleDateFormat("dd/MM/yyyy").format(fecha);
        String horaTexto = new SimpleDateFormat("HH:mm").format(hora);
        
        Cita nueva = ProcesosCita.RegistrarCita(fechaTexto, horaTexto, mascotaSeleccionada,
                seleccionado, serviciosSeleccionados, servicios,
                registro.getPendientes(), registro.getHistorial(),
                clientes, mascotas, personal);
                
        try {
            AlmacenarCitas.Guardar(registro);
        } catch (IOException ex) {
            ProcesosCita.RevertirRegistro(registro.getPendientes(), nueva.getCodCita());
            throw ex;
        }
        
        guardadoExitoso = true;
        Mensajes.Mostrar("Cita " + nueva.getCodCita() + " registrada correctamente.");
        vista.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        
        if (origen == vista.btnCancelar) {
            vista.dispose();
            
        } else if (origen == vista.btnBuscarMascota || origen == vista.txtBuscarMascota) {
            buscarMascota();
            
        } else if (origen == vista.btnAgregar1) { 
            agregarServicio();
            
        } else if (origen == vista.btnQuitar) { 
            quitarServicio();
            
        } else if (origen == vista.btnGuardar) {
            try {
                guardar();
            } catch (IllegalArgumentException ex) {
                Mensajes.Mostrar(ex.getMessage());
            } catch (IOException ex) {
                Mensajes.Mostrar("No se pudo guardar la cita: " + ex.getMessage());
            }
        }
    }
}