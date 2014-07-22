package org.jalt.model.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.AlgorithmFactory;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.iteration.rl.erg.MultiERGLearning;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.solution.Policy;
import org.jalt.model.solution.SinglePolicy;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.ImageUtils;
import org.jalt.util.Utils;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings("rawtypes")
public class Test {

	protected static final boolean DEBUG = true;
	protected Algorithm algorithm;
	protected AlgorithmFactory algFactory;
	protected Problem problem;
	protected ProblemFactory probFactory;
	protected Writer out;

	public Test(Problem pProblem, Algorithm pAlgorithm) {
		problem = pProblem;
		algorithm = pAlgorithm;
	}

	public Test(Problem pProblem, AlgorithmFactory pFactory) {
		problem = pProblem;
		algFactory = pFactory;
	}

	public Test(Problem pProblem, AlgorithmFactory pFactory, String filename) throws IOException {
		problem = pProblem;
		algFactory = pFactory;
		out = new BufferedWriter(new FileWriter(filename));
	}

	public Test(ProblemFactory pProblemFactory, AlgorithmFactory pFactory, String filename)
			throws IOException {
		probFactory = pProblemFactory;
		algFactory = pFactory;
		out = new BufferedWriter(new FileWriter(filename));
	}

	public Test(ProblemFactory pProblemFactory, AlgorithmFactory pFactory) {
		probFactory = pProblemFactory;
		algFactory = pFactory;
	}

	public Test(ProblemFactory pProblemFactory, Algorithm pAlgorithm) {
		probFactory = pProblemFactory;
		algorithm = pAlgorithm;
	}

	public void run(Map<String, Object> pParameters) {
		createAndRun(pParameters);
		print("\nEnd: " + Utils.now());
	}

	protected void printHeader() {
		print("################################");
		print("\nStart: " + Utils.now());
		print("\nModel:");
		print(problem.getModel().toString());
		print("\nError: " + DefaultTestProperties.ERROR);
		print("\nProblem:");
		print(problem.toString());
		print("\nExecution:");
		flush();
	}

	protected void createAndRun(Map<String, Object> pParameters) {
		algorithm = getAlgorithm();
		problem = getProblem();
		
		printHeader();

		print("\n------------------------------");
		print("Algorithm: " + algorithm.getName());
		// run
		runAlgorithm(problem, algorithm, pParameters);
		flush();
	}

	protected Problem getProblem() {
		if (probFactory != null) {
			return probFactory.create();
		} else if (problem != null) {
			return problem;
		}
		throw new RuntimeException("Algorithm and factory are not defined for test!");
	}

	protected Algorithm getAlgorithm() {
		if (algFactory != null) {
			return algFactory.create();
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
			if (problem.getModel().getStates().size() < Problem.MAX_SIZE_PRINT) {
				// print("Result:" + problem.toString(result));
				print("Result:" + problem.toString(((Policy) result).getBestPolicy()));//TODO: uncomment to see final policy
				// print("Result:" + problem.toString((Policy) result));
			}
			// save heat map and final plans
			// saveResultImages(problem, algorithm, result);
		}

		return result;
	}

	private void saveResultImages(Problem problem, Algorithm algorithm, Object result) {
		if (algorithm instanceof ReinforcementLearning) {
			saveResultImages(problem, (ReinforcementLearning) algorithm, result);
		} else if (algorithm instanceof MultiERGLearning) {
			saveResultImages(problem, ((MultiERGLearning) algorithm).getLearnings().get(0), result);
		}
	}

	private void saveResultImages(Problem problem, ReinforcementLearning algorithm, Object result) {
		ImageUtils.save(ImageUtils.createHeat(problem, algorithm.getQTable().getFrequencyValues()),
				"final_heat.png");

		ImageUtils.save(ImageUtils.create(problem, (Policy) result), "result.png");
	}

	protected void print(String txt) {
		if (out != null) {
			try {
				out.write(txt);
			} catch (IOException e) {
			}
		}
		if (DEBUG) {
			Log.info(txt);
		}
	}

	protected void println() {
		print("\n");
	}

	protected void printNoInitialBreak(String str) {
		print(str.startsWith("\n") ? str.substring(1) : str);
	}

	protected void flush() {
		if (out != null) {
			try {
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
