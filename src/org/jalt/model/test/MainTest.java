package org.jalt.model.test;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.iteration.ValueIteration;
import org.jalt.model.algorithm.iteration.rl.DynaQ;
import org.jalt.model.algorithm.iteration.rl.QLearning;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.iteration.rl.SARSA;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.solution.Policy;
import org.jalt.model.test.erg.AlgorithmTest;
import org.jalt.model.test.erg.ERGTest;
import org.jalt.model.test.erg.MultiERGTest;
import org.jalt.model.test.erg.antenna.AntennaExamples;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.FileUtils;
import org.jalt.util.ImageUtils;
import org.jalt.util.PolicyUtils;
import org.jalt.view.ui.cli.ProblemsCLI;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class MainTest {

	private static final String DEFAULT_PATH = "D:\\Dev\\Workspaces\\Projects\\Private\\JALT\\problems\\GenericERGGridModel\\";
	private static final String RESULT_EXT = ".txt";
	private static long timevi = 0;
	private static int countvi;

	public static void main(final String[] pArgs) throws IOException {
		// runMultiple();
		// runOne();
		runBig();
		// runAntenna();
		Toolkit.getDefaultToolkit().beep();
	}

	private static void runAntenna() throws IOException {
		final String file = "antenna";

		ProblemFactory probFactory = new ProblemFactory() {
			protected Problem<?> doCreate() {
				return AntennaExamples.getSMC13();
			};
		};

		Problem prob = probFactory.create();

		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		Class<? extends ReinforcementLearning> c = DynaQ.class;

		AlgorithmTest algTest = new AlgorithmTest(c);
		// AlgorithmTest algTest = new ERGTest(c);

		Test test = new BatchTest(probFactory, algTest.createAlgorithmFactory(), getDirResultPath(
				c, file));
		test.run(params);

		if (countvi > 1)
			Log.info("Time VI " + 4 * (timevi / (countvi - 1)));
	}

	private static void runOne() throws IOException {
		// 0,2,5,7
		final String file = "thesis\\5five\\0_problem";

		ProblemFactory probFactory = new ProblemFactory() {
			protected Problem<?> doCreate() {
				// return new
				// ProblemsCLI(GenericERGProblemFactory.createDefault(10,
				// 100)).run();
				return FileUtils.fromFile(getProblemPath(file));
			};
		};

		Problem prob = probFactory.create();

		// ImageUtils.save(ImageUtils.create(prob, null), getProblemPath(file) +
		// ".png");

		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		int agents = prob.getInitialStates().size();
		Class<? extends ReinforcementLearning> c = DynaQ.class;

		AlgorithmTest algTest = agents > 1 ? new MultiERGTest(agents, c) : new AlgorithmTest(c);
		// : new ERGTest(c);

		Test test = new BatchTest(probFactory, algTest.createAlgorithmFactory(), getDirResultPath(
				c, file));
		test.run(params);

		if (countvi > 1)
			Log.info("Time VI " + 3 * (timevi / (countvi - 1)));
	}

	private static void runBig() throws IOException {
		final String file = "big\\50_problem";

		ProblemFactory probFactory = new ProblemFactory() {
			protected Problem<?> doCreate() {
				return FileUtils.fromFile(getProblemPath(file));
			};
		};

		Problem prob = probFactory.create();
		
		 //ImageUtils.save(ImageUtils.create(prob, null), getProblemPath(file) + ".png");

		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		Class<? extends ReinforcementLearning> c = DynaQ.class;

		AlgorithmTest algTest = 
		new MultiERGTest(prob.getInitialStates().size(), c);
		//new AlgorithmTest(c);

		Test test = new BatchTest(probFactory, algTest.createAlgorithmFactory(), getDirResultPath(
				c, file));
		test.run(params);

		if (countvi > 1)
			Log.info("Time VI " + 3 * (timevi / (countvi - 1)));
	}

	private static void runMultiple() throws IOException {
		int count = 0;

		for (final String file : ProblemsCLI
				.getAllFilesFromDir("GenericERGGridModel\\thesis\\1one")) {

			ProblemFactory probFactory = new ProblemFactory() {
				protected Problem<?> doCreate() {
					return FileUtils.fromFile(file);
				};
			};

			Problem prob = probFactory.create();

			Map<String, Object> params = createParamsMap();
			runVI(prob, params);

			Log.info("\n################################");
			Log.info("TEST RUN " + count++);

			int agents = prob.getInitialStates().size();
			Class<? extends ReinforcementLearning> c = QLearning.class;

			AlgorithmTest algTest = agents > 1 ? new MultiERGTest(agents, c)
			// : new AlgorithmTest(c);
					: new ERGTest(c);

			Test test = new BatchTest(probFactory, algTest.createAlgorithmFactory(), getResultPath(
					c, file));
			test.run(params);

			if (countvi > 1)
				Log.info("Time VI " + 3 * (timevi / (countvi - 1)));
		}
	}

	private static String getProblemPath(String str) {
		return DEFAULT_PATH + str + Problem.PROB_EXT;
	}

	private static String getDirResultPath(Class clazz, String str) {
		return DEFAULT_PATH + str + clazz.getSimpleName() + RESULT_EXT;
	}

	private static String getResultPath(Class clazz, String str) {
		return str + clazz.getSimpleName() + RESULT_EXT;
	}

	public static void runVI(Problem prob, Map<String, Object> params) {
		long ini = System.currentTimeMillis();
		ValueIteration vi = new ValueIteration();
		Policy pi = vi.run(prob, Collections.emptyMap());
		long end = System.currentTimeMillis();

		long t = (end - ini);
		Log.info("vi:" + t + " - it:" + vi.getIterations());
		timevi += t;
		countvi++;

		params.put(PolicyUtils.BEST_VALUES_STR, vi.getCurrentValues());

		// ImageUtils.save(ImageUtils.create(prob, pi), "final_vi.png");

		// Log.info("\nV-Values VI: \n"
		// + new GridPrinter().toGrid(prob.getModel(), vi.getCurrentValues()));
	}

	private static Map<String, Object> createParamsMap() {
		return CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0);
	}
}
