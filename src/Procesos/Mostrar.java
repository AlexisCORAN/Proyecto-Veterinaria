package Procesos;

import Colas.ColaCitas;
import Entidades.Cita;
import Entidades.Cliente;
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
import Ordenamientos.OrdenarFechas;
import Pilas.PilaCitas;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Mostrar {

    private static final String[] TITULOS_CLIENTES = {"Nro", "Código", "DNI", "Nombres",
        "Apellidos", "Teléfono", "Fecha de nacimiento", "Estado"};
    private static final String[] TITULOS_PERSONAL = {"Nro", "Código", "DNI", "Nombres",
        "Apellidos", "Teléfono", "Fecha de nacimiento", "Cargo", "Estado"};
    private static final String[] TITULOS_MASCOTAS = {"Nro", "Código", "Nombre", "Especie",
        "Raza", "Cod. Cliente", "Estado"};
    private static final String[] TITULOS_SERVICIOS = {"Nro", "Código", "Nombre",
        "Descripción", "Precio (S/)", "Estado"};
    private static final String[] TITULOS_CITAS = {"Código", "Fecha", "Hora",
        "Cód. Mascota", "Estado", "Total (S/)"};

    public static void DatosClientes(JTable tabla, ListaClientes lista, String estado, String textoBusqueda) {
        DefaultTableModel modelo = modeloSoloLectura(TITULOS_CLIENTES);
        String filtro = normalizarFiltro(textoBusqueda);

        int numero = 0;
        for (NodoCliente actual = lista.ini; actual != null; actual = actual.sig) {
            Cliente c = actual.cliente;
            if (coincideEstado(estado, c.isActivo()) && coincideBusquedaCliente(c, filtro)) {
                modelo.addRow(c.Registro(++numero));
            }
        }
        tabla.setModel(modelo);
    }

    private static boolean coincideBusquedaCliente(Cliente c, String filtro) {
        if (filtro.isEmpty()) {
            return true;
        }
        String nombreCompleto = (textoSeguro(c.getNombres()) + " " + textoSeguro(c.getApellidos())).toLowerCase();
        return textoSeguro(c.getCodCliente()).toLowerCase().contains(filtro)
                || textoSeguro(c.getDni()).toLowerCase().contains(filtro)
                || nombreCompleto.contains(filtro);
    }

    public static void DatosPersonal(JTable tabla, ListaPersonal lista, String estado, String textoBusqueda) {
        DefaultTableModel modelo = modeloSoloLectura(TITULOS_PERSONAL);
        String filtro = normalizarFiltro(textoBusqueda);

        int numero = 0;
        for (NodoPersonal actual = lista.ini; actual != null; actual = actual.sig) {
            Personal p = actual.personal;
            if (coincideEstado(estado, p.isActivo()) && coincideBusquedaPersonal(p, filtro)) {
                modelo.addRow(p.Registro(++numero));
            }
        }
        tabla.setModel(modelo);
    }

    private static boolean coincideBusquedaPersonal(Personal p, String filtro) {
        if (filtro.isEmpty()) {
            return true;
        }
        String nombreCompleto = (textoSeguro(p.getNombres()) + " " + textoSeguro(p.getApellidos())).toLowerCase();
        return textoSeguro(p.getCodPersonal()).toLowerCase().contains(filtro)
                || textoSeguro(p.getDni()).toLowerCase().contains(filtro)
                || nombreCompleto.contains(filtro)
                || textoSeguro(p.getCargo()).toLowerCase().contains(filtro);
    }

    public static void DatosMascotas(JTable tabla, ListaMascotas lista, String estado, String textoBusqueda) {
        DefaultTableModel modelo = modeloSoloLectura(TITULOS_MASCOTAS);
        String filtro = normalizarFiltro(textoBusqueda);

        int numero = 0;
        for (NodoMascota actual = lista.ini; actual != null; actual = actual.sig) {
            Mascota m = actual.mascota;
            if (coincideEstado(estado, m.isActivo()) && coincideBusquedaMascota(m, filtro)) {
                modelo.addRow(m.Registro(++numero));
            }
        }
        tabla.setModel(modelo);
    }

    private static boolean coincideBusquedaMascota(Mascota m, String filtro) {
        if (filtro.isEmpty()) {
            return true;
        }
        return textoSeguro(m.getCodMascota()).toLowerCase().contains(filtro)
                || textoSeguro(m.getNombre()).toLowerCase().contains(filtro)
                || textoSeguro(m.getCodCliente()).toLowerCase().contains(filtro);
    }

    public static void DatosServicios(JTable tabla, ListaServicios lista, String estado, String textoBusqueda) {
        DefaultTableModel modelo = modeloSoloLectura(TITULOS_SERVICIOS);
        String filtro = normalizarFiltro(textoBusqueda);

        int numero = 0;
        for (Servicio s : lista.getServicios()) {
            if (coincideEstado(estado, s.isActivo()) && coincideBusquedaServicio(s, filtro)) {
                modelo.addRow(s.Registro(++numero));
            }
        }
        tabla.setModel(modelo);
    }

    private static boolean coincideBusquedaServicio(Servicio s, String filtro) {
        if (filtro.isEmpty()) {
            return true;
        }
        return textoSeguro(s.getCodServicio()).toLowerCase().contains(filtro)
                || textoSeguro(s.getNombre()).toLowerCase().contains(filtro)
                || textoSeguro(s.getDescripcion()).toLowerCase().contains(filtro);
    }

    public static void DatosCitas(JTable tabla, ColaCitas pendientes, PilaCitas historial,
            String filtroEstado, String textoBusqueda, boolean ordenAscendente) {
        DefaultTableModel modelo = modeloSoloLectura(TITULOS_CITAS);
        String filtro = normalizarFiltro(textoBusqueda);
        List<Cita> visibles = new ArrayList<>();

        if ("TODOS".equalsIgnoreCase(filtroEstado) || "PENDIENTES".equalsIgnoreCase(filtroEstado)) {
            for (Cita c : pendientes.getCitas()) {
                if (coincideBusquedaCita(c, filtro)) {
                    visibles.add(c);
                }
            }
        }

        for (Cita c : historial.getCitas()) {
            if (coincideEstadoCita(filtroEstado, c) && coincideBusquedaCita(c, filtro)) {
                visibles.add(c);
            }
        }

        OrdenarFechas.porFechaHora(visibles, ordenAscendente);

        for (Cita c : visibles) {
            modelo.addRow(filaCita(c));
        }
        tabla.setModel(modelo);
    }

    private static boolean coincideEstadoCita(String filtroEstado, Cita c) {
        String estadoCita = textoSeguro(c.getEstado());
        return "TODOS".equalsIgnoreCase(filtroEstado)
                || ("ATENDIDAS".equalsIgnoreCase(filtroEstado) && estadoCita.equalsIgnoreCase("Atendida"))
                || ("CANCELADAS".equalsIgnoreCase(filtroEstado) && estadoCita.equalsIgnoreCase("Cancelada"));
    }

    private static boolean coincideBusquedaCita(Cita c, String filtro) {
        if (filtro.isEmpty()) {
            return true;
        }
        return textoSeguro(c.getCodCita()).toLowerCase().contains(filtro)
                || textoSeguro(c.getCodMascota()).toLowerCase().contains(filtro);
    }

    private static Object[] filaCita(Cita c) {
        return new Object[]{c.getCodCita(), c.getFecha(), c.getHora(),
            c.getCodMascota(), c.getEstado(), String.format("%.2f", c.getTotal())};
    }

    private static String normalizarFiltro(String texto) {
        return texto == null ? "" : texto.trim().toLowerCase();
    }

    private static String textoSeguro(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private static boolean coincideEstado(String estado, boolean activo) {
        return "TODOS".equalsIgnoreCase(estado)
                || ("ACTIVOS".equalsIgnoreCase(estado) && activo)
                || ("RETIRADOS".equalsIgnoreCase(estado) && !activo);
    }

    private static DefaultTableModel modeloSoloLectura(String[] titulos) {
        return new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}