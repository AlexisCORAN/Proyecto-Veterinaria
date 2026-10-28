
package Entidades;


public class Personal extends Persona {

    private static final long serialVersionUID = 1L;

    private String codPersonal;
    private String apellidos;
    private String fechaNacimiento;
    private String cargo;
    private Boolean activo = true;

    public Personal() {
        super();
    }

    public Personal(String codPersonal, String dni, String nombres, String telefono, String cargo) {
        this(codPersonal, dni, nombres, "", telefono, "", cargo);
    }

    public Personal(String codPersonal, String dni, String nombres, String apellidos,
            String telefono, String fechaNacimiento, String cargo) {
        super(dni, nombres, telefono);
        this.codPersonal = codPersonal;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.cargo = cargo;
    }

    public Object[] Registro(int num) {
        return new Object[]{num, codPersonal, dni, nombres, apellidos, telefono, fechaNacimiento, cargo,
            isActivo() ? "Activo" : "Retirado"};
    }

    public String getCodPersonal() {
        return codPersonal;
    }

    public void setCodPersonal(String codPersonal) {
        this.codPersonal = codPersonal;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isActivo() {
        return activo == null || activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
