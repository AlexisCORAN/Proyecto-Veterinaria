
package Persistencia;

import Colas.ColaCitas;
import Pilas.PilaCitas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class AlmacenarCitas {

    private static final String ARCHIVO = "Citas.bin";
    private static final String ARCHIVO_TMP = "Citas_tmp.bin";

    private AlmacenarCitas() {
    }

    public static void Guardar(RegistroCitas registro) throws IOException {
        File fileTmp = new File(ARCHIVO_TMP);
        File fileOriginal = new File(ARCHIVO);

        try (FileOutputStream fos = new FileOutputStream(fileTmp);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(registro);
        }

        if (fileOriginal.exists()) {
            fileOriginal.delete();
        }
        fileTmp.renameTo(fileOriginal);
    }

    public static RegistroCitas Recuperar() throws IOException, ClassNotFoundException {
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return new RegistroCitas();
        }

        try (FileInputStream fis = new FileInputStream(archivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (RegistroCitas) ois.readObject();
        }
    }

    
    public static class RegistroCitas implements Serializable {

        private static final long serialVersionUID = 1L;
        private final ColaCitas pendientes;
        private final PilaCitas historial;

        public RegistroCitas() {
            this(new ColaCitas(), new PilaCitas());
        }

        public RegistroCitas(ColaCitas pendientes, PilaCitas historial) {
            this.pendientes = pendientes;
            this.historial = historial;
        }

        public ColaCitas getPendientes() {
            return pendientes;
        }

        public PilaCitas getHistorial() {
            return historial;
        }
    }
}