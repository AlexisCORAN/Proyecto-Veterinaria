
package Entidades;

import java.io.Serializable;


public class Usuario implements Serializable {

    private String username;
    private String password;
    private String rol;
    private String codPersonal;

    public Usuario() {
    }

    public Usuario(String username, String password, String rol, String codPersonal) {
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.codPersonal = codPersonal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCodPersonal() {
        return codPersonal;
    }

    public void setCodPersonal(String codPersonal) {
        this.codPersonal = codPersonal;
    }
}
