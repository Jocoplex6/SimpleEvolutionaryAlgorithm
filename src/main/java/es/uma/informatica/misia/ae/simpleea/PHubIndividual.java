package es.uma.informatica.misia.ae.simpleea;

import java.util.Arrays;
import java.util.Random;

public class PHubIndividual extends Individual {

    private int[] hubs;        // size p
    private int[] allocation;  // size n

    public PHubIndividual(PHubIndividual other) {
        this.hubs = other.hubs.clone();
        this.allocation = other.allocation.clone();
        this.fitness = other.fitness;
    }

    public PHubIndividual(int n, int p, Random rnd) {
        this.hubs = new int[p];
        this.allocation = new int[n];

        for (int k = 0; k < p; k++) {
            int newHub;
            do {
                newHub = rnd.nextInt(n); // node number
            } while (contains(hubs, newHub, k));
            hubs[k] = newHub;
        }

        for (int i = 0; i < n; i++) {
            allocation[i] = rnd.nextInt(p);
        }
    }

    private boolean contains(int[] arr, int value, int uptoIndex) {
        for (int i = 0; i < uptoIndex; i++) {
            if (arr[i] == value) return true;
        }
        return false;
    }

    public int[] getHubs() {
        return hubs;
    }

    public int[] getAllocation() {
        return allocation;
    }

    @Override
    public String toString() {
        return "PHubIndividual{" +
                "fitness=" + fitness +
                ", hubs=" + Arrays.toString(hubs) +
                ", allocation=" + Arrays.toString(allocation) +
                '}';
    }
}
