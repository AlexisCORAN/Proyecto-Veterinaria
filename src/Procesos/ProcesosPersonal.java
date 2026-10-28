
package Procesos;

import Entidades.Personal;
import ListasDoblesEnlazadas.ListaPersonal;
import ListasDoblesEnlazadas.NodoPersonal;
import Vista.DialogNuevoPersonal;
import Vista.pnlPersonal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ProcesosPersonal {

    private static final String FORMATO_FECHA = "dd/MM/yyyy";

    public static void LimpiarEntradas(pnlPersonal vista) {
        vista.txtBuscar.setText("Buscar por Código");
    }

    public static Personal LeerPersonalDesdeFormulario(DialogNuevoPersonal vista) {
        String codigo = vista.txtCodigoPersonal.getText().trim();
        String dni = vista.txtDniCliente.getText().trim();
        String nombres = vista.txtNombresCliente.getText().trim();
        String apellidos = vista.txtApellidosCliente.getText().trim();
        String telefono = vista.txtTelefonoCliente.getText().trim();
        Object cargoSeleccionado = vista.cboCargo.getSelectedItem();

        if (dni.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
            throw new IllegalArgumentException("DNI, nombres y apellidos son obligatorios.");
        }
        if (cargoSeleccionado == null || cargoSeleccionado.toString().isBlank()) {
            throw new IllegalArgumentException("Seleccione el cargo del personal.");
        }

        String fechaNacimiento = new SimpleDateFormat(FORMATO_FECHA)
                .format((Date) vista.spinFechaNacimiento.getValue());
        return new Personal(codigo, dni, nombres, apellidos, telefono, fechaNacimiento,
                cargoSeleccionado.toString());
    }

    public static String GenerarCodigo(ListaPersonal lista) {
        List<String> codigos = new ArrayList<>();
        NodoPersonal actual = lista.ini;
        while (actual != null) {
            codigos.add(actual.personal.getCodPersonal());
            actual = actual.sig;
        }
        return GeneradorCodigos.Siguiente("PER-", codigos);
    }

    public static Personal RegistrarPersonal(DialogNuevoPersonal vista, ListaPersonal lista) {
        Personal datos = LeerPersonalDesdeFormulario(vista);
        String codigo = GenerarCodigo(lista);
        vista.txtCodigoPersonal.setText(codigo);
        Personal nuevo = new Personal(codigo, datos.getDni(), datos.getNombres(),
                datos.getApellidos(), datos.getTelefono(), datos.getFechaNacimiento(), datos.getCargo());
        if (lista.Buscar(nuevo.getCodPersonal()) != null) {
            throw new IllegalArgumentException("Ya existe personal con ese código.");
        }
        if (lista.BuscarPorDni(nuevo.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe personal con ese DNI.");
        }
        lista.InsertarFinal(nuevo);
        return nuevo;
    }

    public static Personal ActualizarPersonalDesdeFormulario(
            DialogNuevoPersonal vista, ListaPersonal lista, Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("Debe seleccionar personal para editar.");
        }
        Personal datos = LeerPersonalDesdeFormulario(vista);
        if (!personal.getCodPersonal().equalsIgnoreCase(datos.getCodPersonal())) {
            throw new IllegalArgumentException("El código del personal no se puede modificar.");
        }
        NodoPersonal conDni = lista.BuscarPorDni(datos.getDni());
        if (conDni != null && conDni.personal != personal) {
            throw new IllegalArgumentException("El DNI ya está registrado para otra persona.");
        }

        Personal anterior = Copiar(personal);
        Aplicar(personal, datos);
        return anterior;
    }

    public static boolean DesactivarPersonal(ListaPersonal lista, Personal personal) {
        if (lista == null || personal == null || !personal.isActivo()) {
            return false;
        }
        NodoPersonal nodo = lista.Buscar(personal.getCodPersonal());
        return nodo != null && nodo.personal == personal
                && lista.Desactivar(personal.getCodPersonal());
    }

    public static void RestaurarEstado(Personal personal, boolean activoAnterior) {
        personal.setActivo(activoAnterior);
    }

    public static void RestaurarPersonal(Personal personal, Personal anterior) {
        Aplicar(personal, anterior);
        personal.setActivo(anterior.isActivo());
    }

    public static void MostrarPersonalEnFormulario(DialogNuevoPersonal vista, Personal personal) {
        vista.txtCodigoPersonal.setEditable(false);
        vista.txtCodigoPersonal.setText(personal.getCodPersonal());
        vista.txtDniCliente.setText(personal.getDni());
        vista.txtNombresCliente.setText(personal.getNombres());
        vista.txtApellidosCliente.setText(personal.getApellidos());
        vista.txtTelefonoCliente.setText(personal.getTelefono());
        vista.cboCargo.setSelectedItem(personal.getCargo());
        if (personal.getFechaNacimiento() != null && !personal.getFechaNacimiento().isBlank()) {
            try {
                Date fecha = new SimpleDateFormat(FORMATO_FECHA).parse(personal.getFechaNacimiento());
                vista.spinFechaNacimiento.setValue(fecha);
            } catch (ParseException ex) {
                throw new IllegalStateException("La fecha de nacimiento guardada no es válida.", ex);
            }
        }
    }

    private static Personal Copiar(Personal personal) {
        Personal copia = new Personal(personal.getCodPersonal(), personal.getDni(),
                personal.getNombres(), personal.getApellidos(), personal.getTelefono(),
                personal.getFechaNacimiento(), personal.getCargo());
        copia.setActivo(personal.isActivo());
        return copia;
    }

    private static void Aplicar(Personal destino, Personal origen) {
        destino.setDni(origen.getDni());
        destino.setNombres(origen.getNombres());
        destino.setApellidos(origen.getApellidos());
        destino.setTelefono(origen.getTelefono());
        destino.setFechaNacimiento(origen.getFechaNacimiento());
        destino.setCargo(origen.getCargo());
    }
}
