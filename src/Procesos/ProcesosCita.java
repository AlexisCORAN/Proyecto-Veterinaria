
package Procesos;

import Arboles.ArbolCitas;
import Colas.ColaCitas;
import EstructuraArrayList.ListaServicios;
import Entidades.Cita;
import Entidades.Cliente;
import Entidades.Mascota;
import Entidades.Personal;
import Entidades.Servicio;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.ListaPersonal;
import Pilas.PilaCitas;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import Vista.pnlCitas;


public class ProcesosCita {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static void LimpiarEntradas(pnlCitas vista) {
        vista.txtBuscar.setText("Buscar por Código");
    }

    public static Cita Buscar(ColaCitas pendientes, PilaCitas historial, String codigo) {
        Cita cita = pendientes.Buscar(codigo);
        return cita == null ? historial.Buscar(codigo) : cita;
    }

    public static Cita RegistrarCita(String fecha, String hora, Mascota mascota,
            Personal personal, List<Servicio> servicios, ListaServicios serviciosDisponibles, ColaCitas pendientes,
            PilaCitas historial, ListaClientes clientes, ListaMascotas mascotas,
            ListaPersonal personalActivo) {
        if (mascota == null || !mascota.isActivo()) {
            throw new IllegalArgumentException("Seleccione una mascota activa.");
        }
        if (personal == null || !personal.isActivo()) {
            throw new IllegalArgumentException("Seleccione personal activo.");
        }
        if (servicios == null || servicios.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un servicio a la cita.");
        }
        ValidarRelaciones(mascota, personal, servicios, serviciosDisponibles,
                clientes, mascotas, personalActivo);

        LocalDate dia = ParsearFecha(fecha);
        LocalTime horaCita = ParsearHora(hora);
        LocalDate hoy = LocalDate.now();
        if (dia.isBefore(hoy) || (dia.isEqual(hoy) && !horaCita.isAfter(LocalTime.now()))) {
            throw new IllegalArgumentException("La fecha y hora de la cita deben ser futuras.");
        }

        Set<String> codigosServicios = new HashSet<>();
        double total = 0;
        List<String> codigos = new ArrayList<>();
        for (Servicio servicio : servicios) {
            if (servicio == null || !servicio.isActivo()) {
                throw new IllegalArgumentException("La cita contiene un servicio retirado.");
            }
            if (!codigosServicios.add(servicio.getCodServicio().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("No agregue el mismo servicio más de una vez.");
            }
            codigos.add(servicio.getCodServicio());
            total += servicio.getPrecio();
            if (!Double.isFinite(total)) {
                throw new IllegalArgumentException("El total de los servicios no es válido.");
            }
        }

        for (Cita existente : pendientes.getCitas()) {
            if (existente.getFecha().equals(fecha) && existente.getHora().equals(hora)
                    && (existente.getCodMascota().equalsIgnoreCase(mascota.getCodMascota())
                    || existente.getCodPersonal().equalsIgnoreCase(personal.getCodPersonal()))) {
                throw new IllegalArgumentException(
                        "La mascota o el personal ya tienen una cita pendiente en esa fecha y hora.");
            }
        }

        String codigo = SiguienteCodigo(pendientes, historial);
        Cita cita = new Cita(codigo, fecha, hora, mascota.getCodMascota(),
                personal.getCodPersonal(), codigos.get(0));
        cita.setCodigosServicios(codigos);
        cita.setTotal(total);
        pendientes.Encolar(cita);
        return cita;
    }

    public static Cita AtenderCita(ColaCitas pendientes, PilaCitas historial,
            String codigo, String detalles) {
        Cita cita = pendientes.Buscar(codigo);
        if (cita == null) {
            throw new IllegalArgumentException("Solo se pueden atender citas pendientes.");
        }
        if (detalles == null || detalles.isBlank()) {
            throw new IllegalArgumentException("Ingrese los detalles de la atención.");
        }
        if (!pendientes.Eliminar(codigo)) {
            throw new IllegalStateException("No se pudo retirar la cita de la cola de pendientes.");
        }
        cita.setEstado("Atendida");
        cita.setDetallesAtencion(detalles.trim());
        historial.Apilar(cita);
        return cita;
    }

    public static Cita CancelarCita(ColaCitas pendientes, PilaCitas historial, String codigo) {
        Cita cita = pendientes.Buscar(codigo);
        if (cita == null) {
            throw new IllegalArgumentException("Solo se pueden cancelar citas pendientes.");
        }
        if (!pendientes.Eliminar(codigo)) {
            throw new IllegalStateException("No se pudo retirar la cita de la cola de pendientes.");
        }
        cita.setEstado("Cancelada");
        historial.Apilar(cita);
        return cita;
    }

    public static void RestaurarCitaPendiente(ColaCitas pendientes, PilaCitas historial,
            Cita cita, String estadoAnterior, String detallesAnteriores) {
        historial.Eliminar(cita.getCodCita());
        cita.setEstado(estadoAnterior);
        cita.setDetallesAtencion(detallesAnteriores);
        pendientes.InsertarAlInicio(cita);
    }

    public static void RevertirRegistro(ColaCitas pendientes, String codigo) {
        pendientes.Eliminar(codigo);
    }

    
    public static ArbolCitas ConstruirIndice(ColaCitas pendientes, PilaCitas historial) {
        ArbolCitas arbol = new ArbolCitas();
        for (Cita cita : pendientes.getCitas()) {
            arbol.Insertar(cita);
        }
        for (Cita cita : historial.getCitas()) {
            arbol.Insertar(cita);
        }
        return arbol;
    }

    private static void ValidarRelaciones(Mascota mascota, Personal personal, List<Servicio> servicios,
            ListaServicios serviciosDisponibles, ListaClientes clientes,
            ListaMascotas mascotas, ListaPersonal personalActivo) {
        if (clientes == null || mascotas == null || personalActivo == null || serviciosDisponibles == null) {
            throw new IllegalArgumentException("No se pudieron cargar las listas requeridas para la cita.");
        }
        if (mascotas.Buscar(mascota.getCodMascota()) == null
                || mascotas.Buscar(mascota.getCodMascota()).mascota != mascota) {
            throw new IllegalArgumentException("La mascota seleccionada ya no está disponible.");
        }
        NodoCliente nodoCliente = clientes.Buscar(mascota.getCodCliente());
        Cliente cliente = nodoCliente == null ? null : nodoCliente.cliente;
        if (cliente == null || !cliente.isActivo()) {
            throw new IllegalArgumentException("La mascota pertenece a un cliente retirado o inexistente.");
        }
        if (personalActivo.Buscar(personal.getCodPersonal()) == null
                || personalActivo.Buscar(personal.getCodPersonal()).personal != personal
                || !"Veterinario".equalsIgnoreCase(personal.getCargo())) {
            throw new IllegalArgumentException("Seleccione un veterinario activo.");
        }
        for (Servicio servicio : servicios) {
            if (servicio == null || !servicio.isActivo()
                    || serviciosDisponibles.Buscar(servicio.getCodServicio()) != servicio) {
                throw new IllegalArgumentException("Seleccione únicamente servicios activos.");
            }
        }
    }

    private static LocalDate ParsearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("Ingrese la fecha de la cita.");
        }
        try {
            return LocalDate.parse(fecha, FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha de la cita no es válida.", ex);
        }
    }

    private static LocalTime ParsearHora(String hora) {
        if (hora == null || hora.isBlank()) {
            throw new IllegalArgumentException("Ingrese la hora de la cita.");
        }
        try {
            return LocalTime.parse(hora, FORMATO_HORA);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La hora de la cita no es válida.", ex);
        }
    }

    private static String SiguienteCodigo(ColaCitas pendientes, PilaCitas historial) {
        int consecutivo = 1;
        String codigo;
        do {
            codigo = String.format("CIT-%04d", consecutivo++);
        } while (Buscar(pendientes, historial, codigo) != null);
        return codigo;
    }
}