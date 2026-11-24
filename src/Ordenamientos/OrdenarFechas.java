package Ordenamientos;

import Entidades.Cita;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OrdenarFechas {

    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    


    public static void porFechaHora(List<Cita> lista, boolean ascendente) {
        for (int i = 1; i < lista.size(); i++) {
            Cita actual = lista.get(i);
            LocalDateTime clave = aFechaHora(actual);
            int j = i - 1;

            while (j >= 0 && debeDesplazar(aFechaHora(lista.get(j)), clave, ascendente)) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
    }

    private static boolean debeDesplazar(LocalDateTime elemento, LocalDateTime clave, boolean ascendente) {
        return ascendente ? elemento.isAfter(clave) : elemento.isBefore(clave);
    }

    private static LocalDateTime aFechaHora(Cita c) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(c.getFecha().trim(), FMT_FECHA);
        } catch (DateTimeParseException | NullPointerException ex) {
            fecha = LocalDate.MAX;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(c.getHora().trim(), FMT_HORA);
        } catch (DateTimeParseException | NullPointerException ex) {
            hora = LocalTime.MAX;
        }
        return LocalDateTime.of(fecha, hora);
    }
}