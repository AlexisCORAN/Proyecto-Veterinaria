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
        for (int i = 0; i < usuarios.length; i++) {
            if (usuarios[i].getUsername().equals(user)
                    && usuarios[i].getPassword().equals(pass)) {
                return usuarios[i];
            }
        }
        return null;
    }

    public Usuario[] getUsuarios() {
        return usuarios;
    }
}
