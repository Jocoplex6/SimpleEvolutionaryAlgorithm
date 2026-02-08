package es.uma.informatica.misia.ae.simpleea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

public class PHubProblem implements Problem {

    private int n;                // Número de nodos
    private int p;                // Número de hubs
    private double[][] dist;      // Distancia entre nodos
    private double[][] flow;      // Matriz de flujo w_ij
    private double c;             // Coste de recogida
    private double t;             // Coste de transporte entre hubs
    private double d;             // Coste de distribución


    public PHubProblem(String filename) throws IOException {
        readInstance(filename);
    }

    private void readInstance(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // Tomamos el número de nodos e inicializamos las coordenadas (x,y)
        n = Integer.parseInt(br.readLine().trim());

        double[] x = new double[n];
        double[] y = new double[n];

        // Leemos las coordenadas del fichero
        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = br.readLine();
            }
            StringTokenizer st = new StringTokenizer(line);
            x[i] = Double.parseDouble(st.nextToken());
            y[i] = Double.parseDouble(st.nextToken());
        }

        // Leemos la matriz de flujos w_ij
        flow = new double[n][n];
        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = br.readLine();
            }
            StringTokenizer st = new StringTokenizer(line);
            for (int j = 0; j < n; j++) {
                if (!st.hasMoreTokens()) {
                    line = br.readLine();
                    st = new StringTokenizer(line);
                }
                flow[i][j] = Double.parseDouble(st.nextToken());
            }
        }

        // Leer p, c, t, d
        p = Integer.parseInt(skipEmpty(br));
        c = Double.parseDouble(skipEmpty(br));
        t = Double.parseDouble(skipEmpty(br));
        d = Double.parseDouble(skipEmpty(br));

        br.close();

        // Calcular matriz de distancias
        dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            dist[i][i] = 0.0;
            for (int j = i + 1; j < n; j++) {
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];
                double dij = Math.sqrt(dx * dx + dy * dy) / 1000;
                // Hemos comprobado empíricamente que las distancias estaban dado en metro y que hay que
                // dividir entre 1000 para ajustarlo a las soluciones óptimas del fichero
                dist[i][j] = dij;
                dist[j][i] = dij;
            }
        }
    }

    // Función auxiliar para saltar líneas vacías
    private String skipEmpty(BufferedReader br) throws IOException {
        String line = br.readLine();
        while (line != null && line.trim().isEmpty()) {
            line = br.readLine();
        }
        return line.trim();
    }

    // Individuo aleatorio del problema pHub
    public Individual generateRandomIndividual(Random rnd) {
        return new PHubIndividual(n, p, rnd);
    }

    // Fitness = -coste, ya que el GA maximiza.
    @Override
    public double evaluate(Individual ind) {
        PHubIndividual ph = (PHubIndividual) ind;
        double cost = computeCost(ph);
        double fitness = -cost;
        ind.setFitness(fitness);
        return fitness;
    }

    /**
     * Función objetivo del USApHMP:
     *
     * cost = sum_{i,j} w_ij * ( c * d(i,hub_i) + t * d(hub_i,hub_j) + d * d(hub_j,j) )
     *
     * donde hub_i = hubs[ allocation[i] - 1 ]  (porque allocation ∈ {1..p}).
     */
    private double computeCost(PHubIndividual ind) {
        int[] hubs = ind.getHubs();
        int[] allocation = ind.getAllocation();

        double totalCost = 0.0;

        for (int i = 0; i < n; i++) {
            int hubNodeI = hubs[allocation[i]];

            for (int j = 0; j < n; j++) {
                double wij = flow[i][j];
                if (wij == 0.0) continue;

                int hubNodeJ = hubs[allocation[j]];

                double collection   = c * dist[i][hubNodeI];
                double transferCost = t * dist[hubNodeI][hubNodeJ];
                double distribution = d * dist[hubNodeJ][j];

                double pathCost = collection + transferCost + distribution;

                totalCost += wij * pathCost;
            }
        }

        return totalCost;
    }

    // Getters opcionales, por si los quieres usar en análisis o debug
    public int getN() { return n; }
    public int getP() { return p; }
    public double[][] getFlow() { return flow; }
    public double[][] getDist() { return dist; }
    public double getC() { return c; }
    public double getT() { return t; }
    public double getD() { return d; }

}
