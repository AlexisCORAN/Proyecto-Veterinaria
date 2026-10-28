
package Persistencia;

import ListasDoblesEnlazadas.ListaPersonal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class AlmacenarPersonal {

    private static final String ARCHIVO = "Personal.bin";
    private static final String ARCHIVO_TMP = "Personal_tmp.bin";

    public static void Guardar(ListaPersonal lista) throws IOException {
        File fileTmp = new File(ARCHIVO_TMP);
        File fileOriginal = new File(ARCHIVO);

        try (FileOutputStream fos = new FileOutputStream(fileTmp);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
             
            oos.writeObject(lista);
        }
        if (fileOriginal.exists()) {
            fileOriginal.delete();
        }
        
        fileTmp.renameTo(fileOriginal); 
    }

    public static ListaPersonal Recuperar() throws IOException, ClassNotFoundException {
        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return new ListaPersonal();
        }

        try (FileInputStream fis = new FileInputStream(archivo);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
             
            return (ListaPersonal) ois.readObject();
        }
    }
}