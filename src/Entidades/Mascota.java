
package Entidades;

import java.io.Serializable;


public class Mascota implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codMascota;
    private String nombre;
    private String especie;
    private String raza;
    private String codCliente;
    private Boolean activo = true;

    public Mascota() {
    }

    public Mascota(String codMascota, String nombre, String especie, String raza, String codCliente) {
        this.codMascota = codMascota;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.codCliente = codCliente;
    }

    public Object[] Registro(int num) {
        return new Object[]{num, codMascota, nombre, especie, raza, codCliente,
            isActivo() ? "Activo" : "Retirado"};
    }

    public String getCodMascota() {
        return codMascota;
    }

    public void setCodMascota(String codMascota) {
        this.codMascota = codMascota;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public boolean isActivo() {
        return activo == null || activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
