
package Entidades;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class Cliente extends Persona implements Serializable {

    private static final long serialVersionUID = 7356720273582921948L;

    private String codCliente;
    private String apellidos = "";
    private String fechaNacimiento = "";
    private boolean activo = true;

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
            activo ? "ACTIVO" : "RETIRADO"};
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
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = input.readFields();
        codCliente = (String) fields.get("codCliente", null);
        apellidos = (String) fields.get("apellidos", "");
        fechaNacimiento = (String) fields.get("fechaNacimiento", "");
        activo = fields.defaulted("activo") || fields.get("activo", true);
    }
}
