
package Entidades;

import java.io.Serializable;


public abstract class Persona implements Serializable {

    protected String dni;
    protected String nombres;
    protected String telefono;

    public Persona() {
    }

    public Persona(String dni, String nombres, String telefono) {
        this.dni = dni;
        this.nombres = nombres;
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
