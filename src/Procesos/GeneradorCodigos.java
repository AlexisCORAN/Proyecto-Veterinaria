
package Procesos;

import java.util.List;

public class GeneradorCodigos {

    public static String Siguiente(String prefijo, List<String> codigos) {
        int mayor = 0;
        
        for (String codigo : codigos) {
            if (codigo != null && codigo.startsWith(prefijo)) {
                String numeroStr = codigo.substring(prefijo.length());
                try {
                    int numero = Integer.parseInt(numeroStr);
                    if (numero > mayor) {
                        mayor = numero;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        int consecutivo = mayor + 1;
        
        return prefijo + String.format("%04d", consecutivo);
    }
}