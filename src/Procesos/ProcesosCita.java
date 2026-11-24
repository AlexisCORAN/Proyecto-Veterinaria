package Procesos;

import Arboles.ArbolCitas;
import Colas.ColaCitas;
import Entidades.Cita;
import Entidades.Cliente;
import Entidades.Mascota;
import Entidades.Personal;
import Entidades.Servicio;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
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

public class ProcesosCita {

    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_ATENDIDA = "Atendida";
    public static final String ESTADO_CANCELADA = "Cancelada";
    public static final String CARGO_VETERINARIO = "Veterinario";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public static ArbolCitas construirIndice(ColaCitas pendientes, PilaCitas historial) {
        ArbolCitas arbol = new ArbolCitas();
        for (Cita cita : pendientes.getCitas()) {
            arbol.Insertar(cita);
        }
        for (Cita cita : historial.getCitas()) {
            arbol.Insertar(cita);
        }
        return arbol;
    }

    public static Cita registrar(String fecha, String hora, Mascota mascota, Personal personal,
            List<Servicio> servicios, ListaClientes clientes, ColaCitas pendientes, PilaCitas historial) {
        if (!mascota.isActivo()) {
            throw new IllegalArgumentException("Seleccione una mascota activa.");
        }
        validarPersonal(personal);
        List<String> codigos = new ArrayList<>();
        double total = validarServicios(servicios, codigos);
        validarPropietarioActivo(clientes, mascota);
        validarFechaFutura(fecha, hora);
        validarDisponibilidad(pendientes, fecha, hora, mascota.getCodMascota(),
                personal.getCodPersonal(), null);

        Cita cita = new Cita(siguienteCodigo(pendientes, historial), fecha, hora,
                mascota.getCodMascota(), personal.getCodPersonal(), codigos.get(0));
        cita.setCodigosServicios(codigos);
        cita.setTotal(total);
        pendientes.Encolar(cita);
        return cita;
    }

    public static void editar(Cita cita, String fecha, String hora, Personal personal,
            List<Servicio> servicios, ColaCitas pendientes) {
        if (!ESTADO_PENDIENTE.equalsIgnoreCase(cita.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden editar citas en estado Pendiente.");
        }
        validarPersonal(personal);
        List<String> codigos = new ArrayList<>();
        double total = validarServicios(servicios, codigos);
        validarFechaFutura(fecha, hora);
        validarDisponibilidad(pendientes, fecha, hora, cita.getCodMascota(),
                personal.getCodPersonal(), cita.getCodCita());

        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setCodPersonal(personal.getCodPersonal());
        cita.setCodigosServicios(codigos);
        cita.setTotal(total);
    }

    public static Cita atender(ColaCitas pendientes, PilaCitas historial, String codigo, String detalles) {
        if (detalles == null || detalles.isBlank()) {
            throw new IllegalArgumentException("Ingrese los detalles de la atención.");
        }
        Cita cita = retirarDePendientes(pendientes, codigo, "atender");
        cita.setEstado(ESTADO_ATENDIDA);
        cita.setDetallesAtencion(detalles.trim());
        historial.Apilar(cita);
        return cita;
    }

    public static Cita cancelar(ColaCitas pendientes, PilaCitas historial, String codigo) {
        Cita cita = retirarDePendientes(pendientes, codigo, "cancelar");
        cita.setEstado(ESTADO_CANCELADA);
        historial.Apilar(cita);
        return cita;
    }

    public static String ultimoMovimiento(PilaCitas historial) {
        Cita tope = historial.VerTope();
        return tope == null ? "Sin movimientos"
                : tope.getCodCita() + " (" + tope.getEstado() + ")";
    }

    private static Cita retirarDePendientes(ColaCitas pendientes, String codigo, String accion) {
        Cita cita = pendientes.Buscar(codigo);
        if (cita == null) {
            throw new IllegalArgumentException("Solo se pueden " + accion + " citas pendientes.");
        }
        pendientes.Eliminar(codigo);
        return cita;
    }

    private static void validarPersonal(Personal personal) {
        if (personal == null || !personal.isActivo()
                || !CARGO_VETERINARIO.equalsIgnoreCase(personal.getCargo())) {
            throw new IllegalArgumentException("Seleccione un veterinario activo.");
        }
    }

    private static double validarServicios(List<Servicio> servicios, List<String> codigosSalida) {
        if (servicios == null || servicios.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un servicio a la cita.");
        }
        Set<String> vistos = new HashSet<>();
        double total = 0;
        for (Servicio servicio : servicios) {
            if (!servicio.isActivo()) {
                throw new IllegalArgumentException("Seleccione únicamente servicios activos.");
            }
            if (!vistos.add(servicio.getCodServicio().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("No agregue el mismo servicio más de una vez.");
            }
            codigosSalida.add(servicio.getCodServicio());
            total += servicio.getPrecio();
        }
        return total;
    }

    private static void validarPropietarioActivo(ListaClientes clientes, Mascota mascota) {
        NodoCliente nodo = clientes.Buscar(mascota.getCodCliente());
        Cliente propietario = nodo == null ? null : nodo.cliente;
        if (propietario == null || !propietario.isActivo()) {
            throw new IllegalArgumentException("La mascota pertenece a un cliente retirado o inexistente.");
        }
    }

    private static void validarFechaFutura(String fecha, String hora) {
        LocalDate dia = parsearFecha(fecha);
        LocalTime horaCita = parsearHora(hora);
        LocalDate hoy = LocalDate.now();
        if (dia.isBefore(hoy) || (dia.isEqual(hoy) && !horaCita.isAfter(LocalTime.now()))) {
            throw new IllegalArgumentException("La fecha y hora de la cita deben ser futuras.");
        }
    }

    private static void validarDisponibilidad(ColaCitas pendientes, String fecha, String hora,
            String codMascota, String codPersonal, String codigoExcluir) {
        String fechaLimpia = fecha == null ? "" : fecha.trim();
        String horaLimpia = hora == null ? "" : hora.trim();
        for (Cita existente : pendientes.getCitas()) {
            if (codigoExcluir != null && existente.getCodCita().equalsIgnoreCase(codigoExcluir)) {
                continue;
            }
            boolean mismoHorario = existente.getFecha().trim().equals(fechaLimpia)
                    && existente.getHora().trim().equals(horaLimpia);
            boolean mismoRecurso = existente.getCodMascota().equalsIgnoreCase(codMascota)
                    || existente.getCodPersonal().equalsIgnoreCase(codPersonal);
            if (mismoHorario && mismoRecurso) {
                throw new IllegalArgumentException(
                        "La mascota o el personal ya tienen una cita pendiente en esa fecha y hora.");
            }
        }
    }

    private static LocalDate parsearFecha(String fecha) {
        try {
            return LocalDate.parse(fecha.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("La fecha de la cita no es válida.", ex);
        }
    }

    private static LocalTime parsearHora(String hora) {
        try {
            return LocalTime.parse(hora.trim(), FORMATO_HORA);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("La hora de la cita no es válida.", ex);
        }
    }

    private static String siguienteCodigo(ColaCitas pendientes, PilaCitas historial) {
        List<String> codigos = new ArrayList<>();
        for (Cita c : pendientes.getCitas()) {
            codigos.add(c.getCodCita());
        }
        for (Cita c : historial.getCitas()) {
            codigos.add(c.getCodCita());
        }
        return GeneradorCodigos.Siguiente("CIT-", codigos);
    }

}
