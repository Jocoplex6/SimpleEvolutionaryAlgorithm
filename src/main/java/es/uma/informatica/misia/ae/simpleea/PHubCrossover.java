package es.uma.informatica.misia.ae.simpleea;

import java.util.Random;

public class PHubCrossover implements Crossover {

    private final Random rnd;

    public PHubCrossover(Random rnd) {
        this.rnd = rnd;
    }

    @Override
    public Individual apply(Individual p1, Individual p2) {
        PHubIndividual parent1 = (PHubIndividual) p1;
        PHubIndividual parent2 = (PHubIndividual) p2;

        int[] hubs1 = parent1.getHubs();
        int[] hubs2 = parent2.getHubs();
        int[] alloc1 = parent1.getAllocation();
        int[] alloc2 = parent2.getAllocation();

        int p = hubs1.length;      // número de hubs
        int n = alloc1.length;     // número de nodos

        // Creamos hijo como copia del padre1 y luego sobreescribimos
        PHubIndividual child = new PHubIndividual(parent1);

        int[] childHubs = child.getHubs();
        int[] childAlloc = child.getAllocation();

        // ====== 1) Crossover en hubs (one-point + reparación) ======
        if (p > 1) {
            int cutH = rnd.nextInt(p - 1) + 1;  // punto de corte en [1, p-1]

            // copiar segmentos de padres
            for (int k = 0; k < p; k++) {
                if (k < cutH) {
                    childHubs[k] = hubs1[k];
                } else {
                    childHubs[k] = hubs2[k];
                }
            }
        } else {
            // caso p == 1, no hay mucho que cruzar
            childHubs[0] = (rnd.nextBoolean() ? hubs1[0] : hubs2[0]);
        }

        // Reparar duplicados en childHubs para garantizar p hubs distintos
        boolean[] used = new boolean[n];  // n = número de nodos
        for (int k = 0; k < p; k++) {
            int node = childHubs[k];
            if (!used[node]) {
                used[node] = true;
            } else {
                // marcar como "vacío" para rellenar luego
                childHubs[k] = -1;
            }
        }

        // Rellenar posiciones -1 con nodos no usados
        for (int k = 0; k < p; k++) {
            if (childHubs[k] == -1) {
                int newNode;
                do {
                    newNode = rnd.nextInt(n);  // 0..n-1
                } while (used[newNode]);
                childHubs[k] = newNode;
                used[newNode] = true;
            }
        }

        // ====== 2) Crossover en allocation (one-point) ======
        if (n > 1) {
            int cutA = rnd.nextInt(n - 1) + 1;  // punto de corte en [1, n-1]

            for (int i = 0; i < n; i++) {
                if (i < cutA) {
                    childAlloc[i] = alloc1[i];
                } else {
                    childAlloc[i] = alloc2[i];
                }
            }
        } else {
            // caso muy raro n == 1
            childAlloc[0] = (rnd.nextBoolean() ? alloc1[0] : alloc2[0]);
        }

        // El hijo ya es factible: p hubs distintos y asignaciones 0..p-1
        return child;
    }
}
