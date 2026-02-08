package es.uma.informatica.misia.ae.simpleea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Esta clase auxiliar la utilizamos para leer las soluciones óptimas de los distintos problemas,
// que hemos extraído del fichero phub3.txt.
public class OptimalSolutions {

    public static Map<String, Double> loadOptimalObjectives(String filename) throws IOException {
        // Inicializamos el diccionario vacío
        Map<String, Double> opt = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Integer n = null;
            Integer p = null;
            Double objective = null;

            while ((line = br.readLine()) !=null) {
                line = line.trim();
                // Línea en blanco
                if (line.isEmpty()) continue;

                // Ejemplo: "Solution for n=10, p=2 :"
                if (line.startsWith("Solution for n=")) {
                    n = null;
                    p = null;
                    objective = null;

                    String[] parts = line.split("[ =,]+");
                    for (int i = 0; i < parts.length; i++) {
                        if (parts[i].equals("n")) {
                            n = Integer.parseInt(parts[i + 1]); // Cogemos la n
                        } else if (parts[i].equals("p")) {
                            p = Integer.parseInt(parts[i + 1]); // Cogemos la p
                        }
                    }
                    // Ejemplo: "Objective  : 136008.13"
                } else if (line.startsWith("Objective")) {
                    String[] parts = line.split(":");
                    objective = Double.parseDouble(parts[1].trim());

                    if (n != null && p != null && objective != null) {
                        String key = key(n, p); // Construimos la llave
                        opt.put(key, objective); // Guardamos la llave + valor objetivo
                    }
                }
            }
        }
        return opt;
    }

    public static String key(int n, int p) {
        return n + "_" + p;
    }
}
