
package Entidades;

import java.io.Serializable;


public class Servicio implements Serializable {

    private String codServicio;
    private String nombre;
    private String descripcion;
    private double precio;
    private Boolean activo = true;

    public Servicio() {
    }

    public Servicio(String codServicio, String descripcion, double precio) {
        this(codServicio, "", descripcion, precio);
    }

    public Servicio(String codServicio, String nombre, String descripcion, double precio) {
        this.codServicio = codServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public Object[] Registro(int num) {
        return new Object[]{num, codServicio, nombre, descripcion, precio,
            isActivo() ? "Activo" : "Retirado"};
    }

    public String getCodServicio() {
        return codServicio;
    }

    public void setCodServicio(String codServicio) {
        this.codServicio = codServicio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo == null || activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return codServicio + " - " + nombre;
    }
}
