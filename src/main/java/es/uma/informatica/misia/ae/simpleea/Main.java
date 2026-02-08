package es.uma.informatica.misia.ae.simpleea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		// Configuración de la experimentación
		String instancesDir = "SimpleEvolutionaryAlgorithm/instances";
		String solutionsFile = "SimpleEvolutionaryAlgorithm/solutions.txt";

		// Parámetros de la experimentación
		int populationSize = 50;
		int maxEvaluations = 50000;
		int runsPerConfig = 30;

		// Valores de mutación y cruce con los que probar
		double[] crossoverProbs = {0, 0.2, 0.4, 0.6, 0.8, 1.0};
		double[] mutationProbs  = {0.01, 0.05, 0.1};

		try {
			// Cargar diccionario con los óptimos de phub3.txt
			Map<String, Double> optMap = OptimalSolutions.loadOptimalObjectives(solutionsFile);

			// Abrir ficheros de salida
			try (PrintWriter outMaxEval = new PrintWriter(new FileWriter("SimpleEvolutionaryAlgorithm/results/results_maxevals.csv"));
				 PrintWriter outOpt     = new PrintWriter(new FileWriter("SimpleEvolutionaryAlgorithm/results/results_optimum.csv"))) {

				// Cabeceras
				outMaxEval.println("instance;n;p;pc;pm;run;bestObjective;evaluations;timeMs");
				outOpt.println("instance;n;p;pc;pm;run;bestObjective;evaluations;timeMs;reachedOptimum");

				// Recorrer instancias de los problemas
				File dir = new File(instancesDir);
				File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
				if (files == null) {
					System.err.println("No se han encontrado instancias en " + instancesDir);
					return;
				}

				// Por cada problema que tenemos (combinaciones de n y p diferentes)
				for (File f : files) {
					String instancePath = f.getPath();
					String instanceName = f.getName();

					System.out.println("=== Instancia: " + instanceName + " ===");

					PHubProblem problem = new PHubProblem(instancePath);
					int n = problem.getN();
					int p = problem.getP();

					Double opt = optMap.get(OptimalSolutions.key(n, p));
					if (opt == null) {
						System.out.println("  [AVISO] No se ha encontrado óptimo para n=" + n + ", p=" + p);
					}

					// Recorrer combinaciones de parámetros
					for (double pc : crossoverProbs) {
						for (double pm : mutationProbs) {
							System.out.printf("  pc=%.3f, pm=%.3f%n", pc, pm);

							for (int run = 1; run <= runsPerConfig; run++) {
								long seed = System.currentTimeMillis() + run;

								// Experimento 1: parada por máximo nº de evaluaciones
								Map<String, Double> params1 = new HashMap<>();
								params1.put(EvolutionaryAlgorithm.POPULATION_SIZE_PARAM, (double) populationSize);
								params1.put(EvolutionaryAlgorithm.MAX_FUNCTION_EVALUATIONS_PARAM, (double) maxEvaluations);
								params1.put(PHubMutation.MUTATION_PROBABILITY_PARAM, pm);
								params1.put(EvolutionaryAlgorithm.CROSSOVER_PROBABILITY_PARAM, pc);
								params1.put(EvolutionaryAlgorithm.RANDOM_SEED_PARAM, (double) seed);

								EvolutionaryAlgorithm ea1 = new EvolutionaryAlgorithm(params1, problem);
								ea1.setStopAtOptimum(false);

								long start1 = System.currentTimeMillis();
								Individual best1 = ea1.run();
								long time1 = System.currentTimeMillis() - start1;

								double bestObj1 = -best1.getFitness();
								int evals1 = ea1.getFunctionEvaluations();

								outMaxEval.printf("%s;%d;%d;%.3f;%.3f;%d;%.6f;%d;%d%n",
										instanceName, n, p, pc, pm, run, bestObj1, evals1, time1);

								// Experimento 2: parada por óptimo (si se conoce)
								if (opt != null) {
									Map<String, Double> params2 = new HashMap<>();
									params2.put(EvolutionaryAlgorithm.POPULATION_SIZE_PARAM, (double) populationSize);
									params2.put(EvolutionaryAlgorithm.MAX_FUNCTION_EVALUATIONS_PARAM, (double) maxEvaluations);
									params2.put(PHubMutation.MUTATION_PROBABILITY_PARAM, pm);
									params2.put(EvolutionaryAlgorithm.CROSSOVER_PROBABILITY_PARAM, pc);
									params2.put(EvolutionaryAlgorithm.RANDOM_SEED_PARAM, (double) (seed + 100000)); // otra semilla

									EvolutionaryAlgorithm ea2 = new EvolutionaryAlgorithm(params2, problem);
									ea2.setKnownOptimum(opt);
									ea2.setStopAtOptimum(true);

									long start2 = System.currentTimeMillis();
									Individual best2 = ea2.run();
									long time2 = System.currentTimeMillis() - start2;

									double bestObj2 = -best2.getFitness();
									int evals2 = ea2.getFunctionEvaluations();
									boolean reachedOpt = bestObj2 <= opt + 1e-6;

									outOpt.printf("%s;%d;%d;%.3f;%.3f;%d;%.6f;%d;%d;%b%n",
											instanceName, n, p, pc, pm, run, bestObj2, evals2, time2, reachedOpt);
								}
							}
						}
					}
				}
			}

			System.out.println("Experimentos completados. Resultados en results_maxevals.csv y results_optimum.csv");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
