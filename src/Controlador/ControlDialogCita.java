package Controlador;

import Entidades.Cita;
import Entidades.Mascota;
import Entidades.Personal;
import Entidades.Servicio;
import EstructuraArrayList.ListaServicios;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoMascota;
import ListasDoblesEnlazadas.NodoPersonal;
import Persistencia.AlmacenarCitas;
import Persistencia.AlmacenarCitas.RegistroCitas;
import Procesos.Mensajes;
import Procesos.ProcesosCita;
import Vista.DialogNuevaCita;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class ControlDialogCita implements ActionListener {

    private static final String PATRON_FECHA = "dd/MM/yyyy";
    private static final String PATRON_HORA = "HH:mm";
    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat(PATRON_FECHA);
    private static final SimpleDateFormat FORMATO_HORA = new SimpleDateFormat(PATRON_HORA);

    private final DialogNuevaCita vista;
    private final RegistroCitas registro;
    private final ListaClientes clientes;
    private final ListaMascotas mascotas;
    private final ListaPersonal personal;
    private final ListaServicios servicios;
    private final Cita citaEnEdicion;
    private final Map<String, Personal> opcionesPersonal = new HashMap<>();
    private final Map<String, Servicio> opcionesServicios = new HashMap<>();
    private final List<Servicio> serviciosSeleccionados = new ArrayList<>();
    private Mascota mascotaSeleccionada;
    private boolean guardadoExitoso;

    public ControlDialogCita(DialogNuevaCita vista, RegistroCitas registro, ListaClientes clientes,
            ListaMascotas mascotas, ListaPersonal personal, ListaServicios servicios, Cita cita) {
        this.vista = vista;
        this.registro = registro;
        this.clientes = clientes;
        this.mascotas = mascotas;
        this.personal = personal;
        this.servicios = servicios;
        this.citaEnEdicion = cita;

        boolean esNueva = (cita == null);
        boolean esEditable = esNueva || ProcesosCita.ESTADO_PENDIENTE.equalsIgnoreCase(cita.getEstado());

        configurarFechas();
        registrarEventos(esNueva, esEditable);

        if (esEditable) {
            cargarOpciones();
        }
        if (esNueva) {
            vista.setTitle("Nueva Cita");
            actualizarServicios();
        } else {
            vista.setTitle((esEditable ? "Editar Cita - " : "Detalle de Cita - ")
                    + cita.getCodCita() + " (" + cita.getEstado() + ")");
            cargarDatosCita(cita, esEditable);
            aplicarPermisosVista(esEditable);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }

    private void registrarEventos(boolean permitirBuscarMascota, boolean permitirEditar) {
        vista.btnCancelar.addActionListener(this);

        if (permitirBuscarMascota) {
            vista.btnBuscarMascota.addActionListener(this);
            vista.txtBuscarMascota.addActionListener(this);
            vista.txtBuscarMascota.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    invalidarMascotaSiCambio();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    invalidarMascotaSiCambio();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    invalidarMascotaSiCambio();
                }
            });
        }

        if (permitirEditar) {
            vista.btnAgregar.addActionListener(this);
            vista.btnQuitar.addActionListener(this);
            vista.btnGuardar.addActionListener(this);
        }
    }

    private void configurarFechas() {
        Date ahora = new Date();
        vista.spinFecha.setModel(new SpinnerDateModel(ahora, null, null, Calendar.DAY_OF_MONTH));
        vista.spinFecha.setEditor(new JSpinner.DateEditor(vista.spinFecha, PATRON_FECHA));
        vista.spinHora.setModel(new SpinnerDateModel(ahora, null, null, Calendar.MINUTE));
        vista.spinHora.setEditor(new JSpinner.DateEditor(vista.spinHora, PATRON_HORA));
        vista.txtTotal.setEditable(false);
    }

    private void cargarOpciones() {
        vista.cboPersonal.removeAllItems();
        opcionesPersonal.clear();
        for (NodoPersonal nodo = personal.ini; nodo != null; nodo = nodo.sig) {
            Personal persona = nodo.personal;
            if (persona.isActivo() && ProcesosCita.CARGO_VETERINARIO.equalsIgnoreCase(persona.getCargo())) {
                String opcion = textoPersonal(persona);
                opcionesPersonal.put(opcion, persona);
                vista.cboPersonal.addItem(opcion);
            }
        }

        vista.cboServicios.removeAllItems();
        opcionesServicios.clear();
        for (Servicio servicio : servicios.getServicios()) {
            if (servicio.isActivo()) {
                String opcion = servicio.getCodServicio() + " - " + servicio.getNombre();
                opcionesServicios.put(opcion, servicio);
                vista.cboServicios.addItem(opcion);
            }
        }
    }

    private void cargarDatosCita(Cita cita, boolean esEditable) {
        vista.txtBuscarMascota.setText(cita.getCodMascota());
        NodoMascota nodoMascota = mascotas.Buscar(cita.getCodMascota());
        if (nodoMascota != null) {
            mascotaSeleccionada = nodoMascota.mascota;
            NodoCliente nodoCliente = clientes.Buscar(mascotaSeleccionada.getCodCliente());
            String dueno = (nodoCliente != null)
                    ? nodoCliente.cliente.getNombres() + " " + nodoCliente.cliente.getApellidos()
                    : "Cliente no encontrado";
            vista.txtNombrePaciente.setText(mascotaSeleccionada.getNombre() + " - " + dueno);
        } else {
            vista.txtNombrePaciente.setText(cita.getCodMascota());
        }

        try {
            vista.spinFecha.setValue(FORMATO_FECHA.parse(cita.getFecha()));
            vista.spinHora.setValue(FORMATO_HORA.parse(cita.getHora()));
        } catch (ParseException ex) {
            Mensajes.Mostrar("La fecha u hora guardada de la cita no es válida. Ingrese una nueva.");
        }

        cargarVeterinario(cita, esEditable);

        serviciosSeleccionados.clear();
        for (String codigo : cita.getCodigosServicios()) {
            Servicio servicio = servicios.Buscar(codigo);
            if (servicio != null) {
                if (esEditable && !servicio.isActivo()) {

                    Servicio copiaHistorica = new Servicio(
                            servicio.getCodServicio(),
                            servicio.getNombre() + " (Retirado)",
                            servicio.getDescripcion(),
                            servicio.getPrecio());
                    copiaHistorica.setActivo(true);
                    serviciosSeleccionados.add(copiaHistorica);
                } else {
                    serviciosSeleccionados.add(servicio);
                }
            } else {
                Servicio noEncontrado = new Servicio(codigo, "Servicio histórico", "-", 0.0);
                noEncontrado.setActivo(true);
                serviciosSeleccionados.add(noEncontrado);
            }
        }
        actualizarServicios();

        if (!esEditable) {
            vista.txtTotal.setText(formatearMonto(cita.getTotal()));
        }
    }

    private void cargarVeterinario(Cita cita, boolean esEditable) {
        NodoPersonal nodo = personal.Buscar(cita.getCodPersonal());
        String texto = (nodo != null) ? textoPersonal(nodo.personal) : cita.getCodPersonal();

        if (!esEditable) {
            vista.cboPersonal.removeAllItems();
            vista.cboPersonal.addItem(texto);
            return;
        }

        if (nodo != null && !opcionesPersonal.containsKey(texto)) {
            opcionesPersonal.put(texto, nodo.personal);
            vista.cboPersonal.addItem(texto);
        }
        vista.cboPersonal.setSelectedItem(texto);
    }

    private void aplicarPermisosVista(boolean esEditable) {
        vista.txtBuscarMascota.setEditable(false);
        vista.btnBuscarMascota.setVisible(false);

        vista.spinFecha.setEnabled(esEditable);
        vista.spinHora.setEnabled(esEditable);
        vista.cboPersonal.setEnabled(esEditable);
        vista.cboServicios.setVisible(esEditable);
        vista.btnAgregar.setVisible(esEditable);
        vista.btnQuitar.setVisible(esEditable);
        vista.btnGuardar.setVisible(esEditable);
        vista.btnCancelar.setText(esEditable ? "Cancelar" : "Cerrar");
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
        for (; actual != null; actual = actual.sig) {
            if (actual.mascota.isActivo()) {
                mascotasCliente.add(actual.mascota);
            }
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
        Servicio servicio = (seleccion == null) ? null : opcionesServicios.get(seleccion.toString());
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
        int fila = vista.tablaServicios.getSelectedRow();
        if (fila < 0 || fila >= serviciosSeleccionados.size()) {
            Mensajes.Mostrar("Seleccione un servicio de la tabla para quitarlo.");
            return;
        }
        serviciosSeleccionados.remove(fila);
        actualizarServicios();
    }

    private void actualizarServicios() {
        String[] columnas = {"Código", "Nombre", "Descripción", "Precio (S/)"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas) {
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
        vista.txtTotal.setText(formatearMonto(total));
    }

    

        private void guardar() {
        try {
            if (citaEnEdicion == null) {
                guardarNuevaCita();
            } else {
                guardarEdicion();
            }
        } catch (IllegalArgumentException ex) {
            Mensajes.Mostrar(ex.getMessage());
        }
    }

    private void guardarNuevaCita() {
        if (mascotaSeleccionada == null) {
            throw new IllegalArgumentException("Debe buscar y seleccionar un paciente válido antes de guardar.");
        }
        Cita nueva = ProcesosCita.registrar(fechaSeleccionada(), horaSeleccionada(), mascotaSeleccionada,
                personalSeleccionado(), serviciosSeleccionados, clientes,
                registro.getPendientes(), registro.getHistorial());
        persistir("Cita " + nueva.getCodCita() + " registrada correctamente.");
    }

    private void guardarEdicion() {
        ProcesosCita.editar(citaEnEdicion, fechaSeleccionada(), horaSeleccionada(),
                personalSeleccionado(), serviciosSeleccionados, registro.getPendientes());
        persistir("Cita " + citaEnEdicion.getCodCita() + " actualizada correctamente.");
    }

    private void persistir(String mensajeOk) {
        guardadoExitoso = true;
        try {
            AlmacenarCitas.Guardar(registro);
            Mensajes.Mostrar(mensajeOk);
        } catch (IOException ex) {
            Mensajes.Mostrar("Los cambios se aplicaron, pero no se pudo guardar en el archivo: " + ex.getMessage());
        }
        vista.dispose();
    }

    private String fechaSeleccionada() {
        return FORMATO_FECHA.format((Date) vista.spinFecha.getValue());
    }

    private String horaSeleccionada() {
        return FORMATO_HORA.format((Date) vista.spinHora.getValue());
    }

    private Personal personalSeleccionado() {
        Object opcion = vista.cboPersonal.getSelectedItem();
        return (opcion == null) ? null : opcionesPersonal.get(opcion.toString());
    }

    private static String textoPersonal(Personal p) {
        return p.getCodPersonal() + " - " + p.getNombres() + " " + p.getApellidos();
    }

    private static String formatearMonto(double monto) {
        return String.format("%.2f", monto);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object origen = e.getSource();
        if (origen == vista.btnCancelar) {
            vista.dispose();
        } else if (origen == vista.btnBuscarMascota || origen == vista.txtBuscarMascota) {
            buscarMascota();
        } else if (origen == vista.btnAgregar) {
            agregarServicio();
        } else if (origen == vista.btnQuitar) {
            quitarServicio();
        } else if (origen == vista.btnGuardar) {
            guardar();
        }
    }
}
