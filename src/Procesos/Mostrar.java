
package Procesos;

import EstructuraArrayList.ListaServicios;
import Colas.ColaCitas;
import Entidades.Cita;
import ListaEnlazadas.ListaClientes;
import ListaEnlazadas.NodoCliente;
import ListasDoblesEnlazadas.ListaMascotas;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoMascota;
import ListasDoblesEnlazadas.NodoPersonal;
import Pilas.PilaCitas;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Mostrar {

    public static void DatosClientes(JTable tabla, ListaClientes lista) {
        DatosClientes(tabla, lista, "TODOS");
    }

    public static void DatosClientes(JTable tabla, ListaClientes lista, String estado) {
        String[] titulos = {"Nro", "Código", "DNI", "Nombres", "Apellidos", "Teléfono",
            "Fecha de nacimiento", "Estado"};
        DefaultTableModel mt = new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla.setModel(mt);

        int num = 0;
        NodoCliente aux = lista.ini;
        while (aux != null) {
            boolean mostrar = "TODOS".equalsIgnoreCase(estado)
                    || ("ACTIVOS".equalsIgnoreCase(estado) && aux.cliente.isActivo())
                    || ("RETIRADOS".equalsIgnoreCase(estado) && !aux.cliente.isActivo());
            if (mostrar) {
                num++;
                mt.addRow(aux.cliente.Registro(num));
            }
            aux = aux.sig;
        }
    }

    public static void DatosPersonal(JTable tabla, ListaPersonal lista) {
        DatosPersonal(tabla, lista, "TODOS");
    }

    public static void DatosPersonal(JTable tabla, ListaPersonal lista, String estado) {
        String[] titulos = {"Nro", "Código", "DNI", "Nombres", "Apellidos", "Teléfono",
            "Fecha de nacimiento", "Cargo", "Estado"};
        DefaultTableModel mt = ModeloSoloLectura(titulos);
        tabla.setModel(mt);

        int num = 0;
        NodoPersonal aux = lista.ini;
        while (aux != null) {
            boolean mostrar = MostrarEstado(estado, aux.personal.isActivo());
            if (mostrar) {
                mt.addRow(aux.personal.Registro(++num));
            }
            aux = aux.sig;
        }
    }

    public static void DatosMascotas(JTable tabla, ListaMascotas lista) {
        DatosMascotas(tabla, lista, "TODOS");
    }

    public static void DatosMascotas(JTable tabla, ListaMascotas lista, String estado) {
        String[] titulos = {"Nro", "Código", "Nombre", "Especie", "Raza", "Cod. Cliente", "Estado"};
        DefaultTableModel mt = ModeloSoloLectura(titulos);
        tabla.setModel(mt);

        int num = 0;
        NodoMascota aux = lista.ini;
        while (aux != null) {
            boolean mostrar = MostrarEstado(estado, aux.mascota.isActivo());
            if (mostrar) {
                mt.addRow(aux.mascota.Registro(++num));
            }
            aux = aux.sig;
        }
    }

    public static void DatosServicios(JTable tabla, ListaServicios lista) {
        DatosServicios(tabla, lista, "TODOS");
    }

    public static void DatosServicios(JTable tabla, ListaServicios lista, String estado) {
        String[] titulos = {"Nro", "Código", "Nombre", "Descripción", "Precio (S/)", "Estado"};
        DefaultTableModel mt = ModeloSoloLectura(titulos);
        tabla.setModel(mt);

        int num = 0;
        for (int i = 0; i < lista.Cantidad(); i++) {
            var servicio = lista.getServicios().get(i);
            if (MostrarEstado(estado, servicio.isActivo())) {
                mt.addRow(servicio.Registro(++num));
            }
        }
    }

    public static void DatosCitas(javax.swing.JTable tabla, ColaCitas pendientes, PilaCitas historial, String filtroEstado) {
        String[] columnas = {"Código", "Fecha", "Hora", "Cód. Paciente", "Estado", "Total (S/)"};

        javax.swing.table.DefaultTableModel modelo = new javax.swing.table.DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        List<Cita> listaPendientes = pendientes.getCitas();
        for (Cita c : listaPendientes) {
            if (filtroEstado.equals("TODOS") || filtroEstado.equals("PENDIENTES")) {
                modelo.addRow(new Object[]{
                    c.getCodCita(), c.getFecha(), c.getHora(),
                    c.getCodMascota(), c.getEstado(), c.getTotal()
                });
            }
        }

        List<Cita> listaHistorial = historial.getCitas();
        for (Cita c : listaHistorial) {
            String estadoCita = c.getEstado() != null ? c.getEstado().trim() : "";
            
            boolean coincideFiltro = filtroEstado.equals("TODOS") ||
                    (filtroEstado.equals("ATENDIDAS") && estadoCita.equalsIgnoreCase("Atendida")) ||
                    (filtroEstado.equals("CANCELADAS") && estadoCita.equalsIgnoreCase("Cancelada")) ||
                    estadoCita.equalsIgnoreCase(filtroEstado);

            if (coincideFiltro) {
                modelo.addRow(new Object[]{
                    c.getCodCita(), c.getFecha(), c.getHora(),
                    c.getCodMascota(), c.getEstado(), c.getTotal()
                });
            }
        }

        tabla.setModel(modelo);
    }

    private static boolean MostrarEstado(String filtro, boolean activo) {
        return "TODOS".equalsIgnoreCase(filtro)
                || "ACTIVOS".equalsIgnoreCase(filtro) && activo
                || "RETIRADOS".equalsIgnoreCase(filtro) && !activo;
    }

    private static DefaultTableModel ModeloSoloLectura(String[] titulos) {
        return new DefaultTableModel(null, titulos) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }
}