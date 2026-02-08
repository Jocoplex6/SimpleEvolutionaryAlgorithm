package es.uma.informatica.misia.ae.simpleea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EvolutionaryAlgorithm {
	public static final String MAX_FUNCTION_EVALUATIONS_PARAM = "maxFunctionEvaluations";
	public static final String RANDOM_SEED_PARAM = "randomSeed";
	public static final String POPULATION_SIZE_PARAM = "populationSize";
	public static final String CROSSOVER_PROBABILITY_PARAM = "crossoverProbability";

	private Problem problem;
	private int functionEvaluations;
	private int maxFunctionEvaluations;
	private List<Individual> population;
	private int populationSize;
	private Random rnd;

	private Individual bestSolution;

	private Selection selection;
	private Replacement replacement;
	private Mutation mutation;
	private Crossover recombination;

	private double crossoverProb;
	private Double knownOptimum = null;
	private boolean stopAtOptimum = false;

	public EvolutionaryAlgorithm(Map<String, Double> parameters, Problem problem) {
		configureAlgorithm(parameters, problem);
	}

	private void configureAlgorithm(Map<String, Double> parameters, Problem problem) {
		populationSize = parameters.get(POPULATION_SIZE_PARAM).intValue();
		maxFunctionEvaluations = parameters.get(MAX_FUNCTION_EVALUATIONS_PARAM).intValue();

		double mutationProb = parameters.get(PHubMutation.MUTATION_PROBABILITY_PARAM);
		crossoverProb = parameters.get(CROSSOVER_PROBABILITY_PARAM);
		long randomSeed = parameters.get(RANDOM_SEED_PARAM).longValue();

		this.problem = problem;

		rnd = new Random(randomSeed);

		selection = new BinaryTournament(rnd);
		replacement = new ElitistReplacement();
		mutation = new PHubMutation(rnd, mutationProb);
		recombination = new PHubCrossover(rnd);
	}

	public Individual run() {

		population = generateInitialPopulation();
		functionEvaluations = 0;
		bestSolution = null;

		evaluatePopulation(population);

		// Límite normal para experimento 1
		int evalLimit = maxFunctionEvaluations;

		// Límite de seguridad para experimento 2 (parada por óptimo)
		int safetyLimit = 100*maxFunctionEvaluations;     // <--- añadido

		while (true) {

			// Caso 1: estudio normal → parar por maxFunctionEvaluations
			if (!stopAtOptimum) {
				if (functionEvaluations >= evalLimit) {
					break;
				}
			}

			// Caso 2: estudio por óptimo → parar por límite de seguridad
			else {
				if (functionEvaluations >= safetyLimit) {   // <--- añadido
					break;
				}
			}

			// === Evolución normal ===
			Individual parent1 = selection.selectParent(population);
			Individual parent2 = selection.selectParent(population);

			Individual child;

			if (rnd.nextDouble() < crossoverProb)
				child = recombination.apply(parent1, parent2);
			else
				child = recombination.apply(parent1, parent1);

			child = mutation.apply(child);
			evaluateIndividual(child);
			population = replacement.replacement(population, Arrays.asList(child));

			// === Parada por óptimo ===
			if (stopAtOptimum && knownOptimum != null) {

				double bestObjective = -bestSolution.getFitness();

				long bestInt = Math.round(bestObjective);
				long optInt  = Math.round(knownOptimum);

				if (bestInt == optInt) {
					break;   // alcanzado o igualado dentro del redondeo
				}
			}
		}

		return bestSolution;
	}


	private void evaluateIndividual(Individual individual) {
		double fitness = problem.evaluate(individual);
		individual.setFitness(fitness);
		functionEvaluations++;
		checkIfBest(individual);
	}

	private void checkIfBest(Individual individual) {
		if (bestSolution == null || individual.getFitness() > bestSolution.getFitness()) {
			bestSolution = individual;
		}
	}

	private void evaluatePopulation(List<Individual> population) {
		for (Individual individual : population) {
			evaluateIndividual(individual);
		}
	}

	private List<Individual> generateInitialPopulation() {
		List<Individual> population = new ArrayList<>();
		for (int i = 0; i < populationSize; i++) {
			population.add(problem.generateRandomIndividual(rnd));
		}
		return population;
	}

	public void setKnownOptimum(Double knownOptimum) {
		this.knownOptimum = knownOptimum;
	}

	public void setStopAtOptimum(boolean stopAtOptimum) {
		this.stopAtOptimum = stopAtOptimum;
	}

	public int getFunctionEvaluations() {
		return functionEvaluations;
	}
}
