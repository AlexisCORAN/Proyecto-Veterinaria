package Arreglos;

import Entidades.Usuario;
import java.io.Serializable;


public class ArregloUsuarios implements Serializable {


    private Usuario[] usuarios;

    public ArregloUsuarios() {
        usuarios = new Usuario[3];

        usuarios[0] = new Usuario("admin", "1234", "Administrador", "P001");
        usuarios[1] = new Usuario("recepcion", "recep123", "Recepcionista", "P002");
        usuarios[2] = new Usuario("medico", "med123", "Veterinario", "P003");
    }

    public Usuario ValidarAcceso(String user, String pass) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUsername().equals(user) && usuario.getPassword().equals(pass)) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario[] getUsuarios() {
        return usuarios;
    }
}
