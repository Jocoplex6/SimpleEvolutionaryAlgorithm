package es.uma.informatica.misia.ae.simpleea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

public class PHubProblemTest {

    public static void main(String[] args) throws Exception {
        String filename = "SimpleEvolutionaryAlgorithm/instances/ap_10_2.txt";

        PHubProblem problem = new PHubProblem(filename);

        System.out.println("=== Resumen de la instancia ===");
        System.out.println("n = " + problem.getN());
        System.out.println("p = " + problem.getP());
        System.out.println("c = " + problem.getC());
        System.out.println("t = " + problem.getT());
        System.out.println("d = " + problem.getD());

        double[][] dist = problem.getDist();
        double[][] flow = problem.getFlow();

        // Comprobaciones básicas
        System.out.println("dist[0][0] = " + dist[0][0]);
        if (problem.getN() > 1) {
            System.out.println("dist[0][1] = " + dist[0][1]);
            System.out.println("dist[1][0] = " + dist[1][0]);
        }

        System.out.println("flow[0][0] = " + flow[0][0]);
        if (problem.getN() > 1) {
            System.out.println("flow[0][1] = " + flow[0][1]);
            System.out.println("flow[1][0] = " + flow[1][0]);
        }

        // Creamos un individuo cualquiera y luego lo sobreescribimos
        PHubIndividual opt = new PHubIndividual(problem.getN(), problem.getP(), new Random(0));

        // ---- Hubs del óptimo (0-based) ----
        int[] hubs = opt.getHubs();
        hubs[0] = 2; // corresponde al nodo 3 (1-based)
        hubs[1] = 6; // corresponde al nodo 7 (1-based)

        // ---- Allocation del óptimo (0-based, índices de hubs) ----
        int[] alloc = opt.getAllocation();
        alloc[0] = 0; // nodo 0 -> hub en nodo 2
        alloc[1] = 0; // nodo 1 -> hub en nodo 2
        alloc[2] = 0; // nodo 2 -> hub en nodo 2
        alloc[3] = 0; // nodo 3 -> hub en nodo 2
        alloc[4] = 1; // nodo 4 -> hub en nodo 6
        alloc[5] = 1; // nodo 5 -> hub en nodo 6
        alloc[6] = 1; // nodo 6 -> hub en nodo 6
        alloc[7] = 1; // nodo 7 -> hub en nodo 6
        alloc[8] = 1; // nodo 8 -> hub en nodo 6
        alloc[9] = 1; // nodo 9 -> hub en nodo 6

        // Evaluar
        double fitness = problem.evaluate(opt);
        double cost = -fitness;

        System.out.printf("Coste del óptimo reconstruido = %.6f%n", cost);

    }
}

