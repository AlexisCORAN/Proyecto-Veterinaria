package Entidades;

import java.io.Serializable;

public class Cliente extends Persona implements Serializable {


    private String codCliente;
    private String apellidos = "";
    private String fechaNacimiento = "";
    private Boolean activo = true;

    public Cliente() {
        super();
    }

    public Cliente(String codCliente, String dni, String nombres, String telefono) {
        super(dni, nombres, telefono);
        this.codCliente = codCliente;
    }

    public Cliente(String codCliente, String dni, String nombres, String apellidos,
            String telefono, String fechaNacimiento) {
        super(dni, nombres, telefono);
        this.codCliente = codCliente;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.activo = true;
    }

    public Object[] Registro(int num) {
        return new Object[]{num, codCliente, dni, nombres, apellidos, telefono, fechaNacimiento,
            isActivo() ? "Activo" : "Retirado"};
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isActivo() {
        return activo == null || activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}