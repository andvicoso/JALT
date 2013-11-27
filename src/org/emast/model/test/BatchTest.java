package org.emast.model.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.controller.AbstractERGLearning;
import org.emast.model.algorithm.controller.MultiAgentERGLearning;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.util.CalcUtils;
import org.emast.util.Utils;

/**
 * 
 * @author Anderson
 */
public class BatchTest extends Test {

	private static final int MAX_ITERATIONS = 30;

	public BatchTest(Problem pProblem, AlgorithmFactory pFactory) {
		super(pProblem, pFactory);
	}

	public BatchTest(Problem pProblem, Algorithm pAlgorithm) {
		super(pProblem, pAlgorithm);
	}

	@Override
	protected void createAndRun(Map<String, Object> pParameters) {
		long timeSum = 0;
		Collection<Integer> episodes = new ArrayList<Integer>();
		Collection<Double> steps = new ArrayList<Double>();
		Object result = null;

		for (int i = 0; i < MAX_ITERATIONS; i++) {
			print("Repetition: " + i);
			algorithm = getAlgorithm();
			// execute
			long initMsecs = System.currentTimeMillis();
			result = runAlgorithm(problem, algorithm, pParameters);
			long diff = System.currentTimeMillis() - initMsecs;
			timeSum += diff;

			if (algorithm instanceof AbstractERGLearning) {
				algorithm = ((AbstractERGLearning) algorithm).getLearning();
			}

			if (algorithm instanceof ReinforcementLearning) {
				storeExecutionMeans((ReinforcementLearning) algorithm, episodes, steps);
			} else if (algorithm instanceof MultiAgentERGLearning) {
				List<ReinforcementLearning<ERG>> algs = ((MultiAgentERGLearning) algorithm)
						.getLearnings();
				for (ReinforcementLearning<ERG> rl : algs) {
					storeExecutionMeans(rl, episodes, steps);
				}
			}
		}

		double meanEps = CalcUtils.getMean(episodes);
		double meanSteps = CalcUtils.getMean(steps);

		print("Repetitions: " + MAX_ITERATIONS);
		print("Means: ");
		print("-Time: " + Utils.toTimeString(timeSum / MAX_ITERATIONS));
		print("-Episodes: " + meanEps);
		print("-Episodes (std deviation): " + CalcUtils.getStandardDeviation(meanEps, episodes));
		print("-Steps per episode: " + meanSteps);
		print("-Steps per episode (std deviation): "
				+ CalcUtils.getStandardDeviation(meanSteps, steps));
		// //print results
		// printNoInitialBreak(algorithm.printResults());
		// if (result != null) {
		// print("Result:" + problem.toString(result));
		// }
	}

	protected void storeExecutionMeans(ReinforcementLearning rl, Collection<Integer> episodes,
			Collection<Double> steps) {
		episodes.add(rl.getIterations());
		steps.add(rl.getMeanSteps());
	}
}
