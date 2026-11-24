
package Entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cita implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codCita;
    private String fecha;
    private String hora;
    private String estado;
    private String detallesAtencion;
    private String codMascota;
    private String codPersonal;
    private String codServicio;
    private List<String> codigosServicios;
    private double total;

    public Cita() {
        this.estado = "Pendiente";
        this.detallesAtencion = "";
    }

    public Cita(String codCita, String fecha, String hora, String codMascota, String codPersonal, String codServicio) {
        this.codCita = codCita;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Pendiente";
        this.detallesAtencion = "";
        this.codMascota = codMascota;
        this.codPersonal = codPersonal;
        this.codServicio = codServicio;
        this.codigosServicios = new ArrayList<>();
        if (codServicio != null && !codServicio.isBlank()) {
            this.codigosServicios.add(codServicio);
        }
    }

    public Object[] Registro(int num) {
        return new Object[]{num, codCita, fecha, hora, estado, codMascota, codPersonal,
            String.join(", ", getCodigosServicios()), total, detallesAtencion};
    }


    public String getCodCita() {
        return codCita;
    }

    public void setCodCita(String codCita) {
        this.codCita = codCita;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetallesAtencion() {
        return detallesAtencion;
    }

    public void setDetallesAtencion(String detallesAtencion) {
        this.detallesAtencion = detallesAtencion;
    }

    public String getCodMascota() {
        return codMascota;
    }

    public void setCodMascota(String codMascota) {
        this.codMascota = codMascota;
    }

    public String getCodPersonal() {
        return codPersonal;
    }

    public void setCodPersonal(String codPersonal) {
        this.codPersonal = codPersonal;
    }

    public String getCodServicio() {
        return codServicio;
    }

    public void setCodServicio(String codServicio) {
        this.codServicio = codServicio;
        if (codigosServicios == null) {
            codigosServicios = new ArrayList<>();
        }
        if (codigosServicios.isEmpty() && codServicio != null && !codServicio.isBlank()) {
            codigosServicios.add(codServicio);
        }
    }

    public List<String> getCodigosServicios() {
        if (codigosServicios == null || codigosServicios.isEmpty()) {
            return codServicio == null || codServicio.isBlank()
                    ? Collections.emptyList() : Collections.singletonList(codServicio);
        }
        return Collections.unmodifiableList(codigosServicios);
    }

    public void setCodigosServicios(List<String> codigosServicios) {
        this.codigosServicios = new ArrayList<>(codigosServicios);
        this.codServicio = this.codigosServicios.isEmpty() ? null : this.codigosServicios.get(0);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
