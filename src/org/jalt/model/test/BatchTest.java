package org.jalt.model.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.AlgorithmFactory;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.iteration.rl.erg.MultiERGLearning;
import org.jalt.model.algorithm.iteration.rl.erg.SingleERGLearning;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.util.CalcUtils;
import org.jalt.util.Utils;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings("rawtypes")
public class BatchTest extends Test {

	private static final int MAX_ITERATIONS = 10;

	public BatchTest(ProblemFactory pProblemFactory, AlgorithmFactory pFactory, String filename)
			throws IOException {
		super(pProblemFactory, pFactory, filename);
	}

	public BatchTest(ProblemFactory pProblemFactory, AlgorithmFactory pFactory) {
		super(pProblemFactory, pFactory);
	}

	public BatchTest(ProblemFactory pProblemFactory, Algorithm pAlgorithm) {
		super(pProblemFactory, pAlgorithm);
	}

	@Override
	protected void createAndRun(Map<String, Object> pParameters) {
		List<Long> timeSum = new ArrayList<Long>(MAX_ITERATIONS);
		Collection<Integer> episodes = new ArrayList<Integer>();
		Collection<Double> steps = new ArrayList<Double>();
		Object result = null;

		for (int i = 0; i < MAX_ITERATIONS; i++) {
			algorithm = getAlgorithm();
			problem = getProblem();

			if (i == 0) {
				printHeader();
				print("Algorithm: " + algorithm.getName());
				print("------------------------------");
			}

			print("Repetition: " + i);
			// execute
			long initMsecs = System.currentTimeMillis();
			result = runAlgorithm(problem, algorithm, pParameters);
			long diff = System.currentTimeMillis() - initMsecs;
			timeSum.add(diff);

			if (algorithm instanceof SingleERGLearning) {
				algorithm = ((SingleERGLearning) algorithm).getLearning();
			}

			if (algorithm instanceof ReinforcementLearning) {
				storeExecutionMeans((ReinforcementLearning) algorithm, episodes, steps);
			} else if (algorithm instanceof MultiERGLearning) {
				List<ReinforcementLearning<ERG>> algs = ((MultiERGLearning) algorithm)
						.getLearnings();
				for (ReinforcementLearning<ERG> rl : algs) {
					storeExecutionMeans(rl, episodes, steps);
				}
			}
			print("");

			flush();
		}

		double meanEps = CalcUtils.getMean(episodes);
		double meanSteps = CalcUtils.getMean(steps);

		print("Total Repetitions: " + MAX_ITERATIONS);
		print("\nMeans: ");
		print("\n-Time: "
				+ Utils.toTimeString(CalcUtils.middleAverage(timeSum) / (MAX_ITERATIONS - 2)));
		print("\n-Episodes: " + meanEps);
		print("\n-Episodes (std deviation): " + CalcUtils.getStandardDeviation(meanEps, episodes));
		print("\n-Steps per episode: " + meanSteps);
		print("\n-Steps per episode (std deviation): "
				+ CalcUtils.getStandardDeviation(meanSteps, steps));
		// //print results
		// printNoInitialBreak(algorithm.printResults());
		// if (result != null) {
		// print("Result:" + problem.toString(result));
		// }
		flush();
	}

	protected void storeExecutionMeans(ReinforcementLearning rl, Collection<Integer> episodes,
			Collection<Double> steps) {
		episodes.add(rl.getIterations());
		steps.add(rl.getMeanSteps());
	}
}
