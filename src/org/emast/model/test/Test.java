package org.emast.model.test;

import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SinglePolicy;
import org.emast.util.DefaultTestProperties;
import org.emast.util.Utils;

/**
 * 
 * @author Anderson
 */
public class Test {

	protected static final boolean DEBUG = true;
	protected Algorithm algorithm;
	protected AlgorithmFactory factory;
	protected Problem problem;

	public Test(Problem pProblem, Algorithm pAlgorithm) {
		problem = pProblem;
		algorithm = pAlgorithm;
	}

	public Test(Problem pProblem, AlgorithmFactory pFactory) {
		problem = pProblem;
		factory = pFactory;
	}

	public void run(Map<String, Object> pParameters) {
		printHeader();
		createAndRun(pParameters);
	}

	protected void printHeader() {
		print("\n################################");
		print("\nModel:");
		print(problem.getModel().toString());
		print("\nError: " + DefaultTestProperties.ERROR);
		print("\nProblem:");
		print(problem.toString());
		print("\nExecution:");
	}

	protected void createAndRun(Map<String, Object> pParameters) {
		algorithm = getAlgorithm();

		print("\n------------------------------");
		print("Algorithm: " + algorithm.getName());
		// run
		runAlgorithm(problem, algorithm, pParameters);
	}

	protected Algorithm getAlgorithm() {
		if (factory != null) {
			return factory.create();
		} else if (algorithm != null) {
			return algorithm;
		}
		throw new RuntimeException("Algorithm and factory are not defined for test!");
	}

	protected Object runAlgorithm(Problem problem, Algorithm algorithm,
			Map<String, Object> pParameters) {
		// execute
		long initMsecs = System.currentTimeMillis();
		Object result = algorithm.run(problem, pParameters);
		long diff = System.currentTimeMillis() - initMsecs;
		// print time
		print("Time: " + Utils.toTimeString(diff));
		// print results
		printNoInitialBreak(algorithm.printResults());
		// if a solution was found...
		if (result != null) {
			print("Result:" + problem.toString(result));
			// SinglePolicy sp = ((Policy) result).getBestPolicy();
			// print("Single Result:" + problem.toString(sp));
		}

		return result;
	}

	protected void print(String pMsg) {
		if (DEBUG) {
			Log.info(pMsg);
		}
	}

	protected void println() {
		print("\n");
	}

	protected void printNoInitialBreak(String str) {
		print(str.startsWith("\n") ? str.substring(1) : str);
	}
}
