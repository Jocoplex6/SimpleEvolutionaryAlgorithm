package es.uma.informatica.misia.ae.simpleea;

import java.util.Random;

public class PHubMutation implements Mutation {

    private final Random rnd;
    private double mutationProb;

    public static final String MUTATION_PROBABILITY_PARAM = "mutationProbability";

    public PHubMutation(Random rnd, double mutationProb) {
        this.rnd = rnd;
        this.mutationProb = mutationProb;
    }

    @Override
    public Individual apply(Individual individual) {
        PHubIndividual original = (PHubIndividual) individual;
        PHubIndividual mutated = new PHubIndividual(original); // copia profunda

        int[] hubs = mutated.getHubs();
        int[] allocation = mutated.getAllocation();

        int p = hubs.length;
        int n = allocation.length;

        // 1) Mutación de asignaciones (allocation mutation)
        for (int i = 0; i < n; i++) {
            if (rnd.nextDouble() < mutationProb) {
                int oldHubIndex = allocation[i];
                int newHubIndex;
                do {
                    newHubIndex = rnd.nextInt(p);  // 0..p-1
                } while (newHubIndex == oldHubIndex);
                allocation[i] = newHubIndex;
            }
        }

        // 2) Mutación de hubs (hub relocation mutation)
        for (int k = 0; k < p; k++) {
            if (rnd.nextDouble() < mutationProb) {
                int newNode;
                do {
                    newNode = rnd.nextInt(n); // nodo entre 0..n-1
                } while (containsExceptIndex(hubs, newNode, k));
                hubs[k] = newNode;
            }
        }

        return mutated;
    }

    private boolean containsExceptIndex(int[] arr, int value, int indexToSkip) {
        for (int i = 0; i < arr.length; i++) {
            if (i == indexToSkip) continue;
            if (arr[i] == value) return true;
        }
        return false;
    }

    public double getMutationProb() {
        return mutationProb;
    }

    public void setMutationProb(double mutationProb) {
        this.mutationProb = mutationProb;
    }
}
