package org.jalt.model.test;

import java.awt.Toolkit;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.iteration.ValueIteration;
import org.jalt.model.algorithm.iteration.rl.QLearning;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.test.erg.AlgorithmTest;
import org.jalt.model.test.erg.ERGTest;
import org.jalt.model.test.erg.antenna.AntennaExamples;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.PolicyUtils;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class MainTest {

	private static final String DEFAULT_PATH = "D:\\Dev\\Workspaces\\Projects\\Private\\JALT\\problems\\GenericERGGridModel\\";
	private static final String RESULT_EXT = ".txt";
	private static long timevi = 0;
	private static int count;

	public static void main(final String[] pArgs) throws IOException {
		String file;
		Problem prob;
		AlgorithmTest algTest;
		// int count = 0;

		// file = "big\\50_problem";
		// file = "nov13\\five\\0_problem";

		// prob = FileUtils.fromFile(getProblemPath(file));

		// prob = new
		// ProblemsCLI(GenericERGProblemFactory.createDefaultFactory(10,
		// 100)).run();
		prob = AntennaExamples.getSMC13();

		// for (Problem<ERG> prob :
		// ProblemsCLI.getAllFromDir("GenericERGGridModel\\big")) {

		// ImageUtils.save(ImageUtils.create(prob, null), "50.png");

		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		// Log.info("\n################################");
		// Log.info("TEST RUN " + count++);

		// algTest = new AlgorithmTest(QLearning.class);
		// algTest = new MultiERGTest(10, QLearning.class);
		algTest = new ERGTest(QLearning.class);

		Test test = new BatchTest(prob, algTest.createAlgorithmFactory(), getResultPath(
				algTest.getLearningClass(), "antenna"));
		test.run(params);

		if (count > 0)
			Log.info("Time VI " + (timevi / (count - 1)));
		Toolkit.getDefaultToolkit().beep();
		// }
	}

	private static String getProblemPath(String str) {
		return DEFAULT_PATH + str + Problem.PROB_EXT;
	}

	private static String getResultPath(Class clazz, String str) {
		return DEFAULT_PATH + str + clazz.getSimpleName() + RESULT_EXT;
	}

	public static void runVI(Problem prob, Map<String, Object> params) {
		long ini = System.currentTimeMillis();
		ValueIteration vi = new ValueIteration();
		Policy pi = vi.run(prob, Collections.emptyMap());
		long end = System.currentTimeMillis();

		long t = (end - ini);
		Log.info("vi:" + t + " - it:" + vi.getIterations());
		timevi += t;
		count++;

		params.put(PolicyUtils.BEST_VALUES_STR, pi.getBestPolicyValue());

		// ImageUtils.save(ImageUtils.create(prob, pi), "final_vi.png");

		// Log.info("\nV-Values VI: \n" + new
		// GridPrinter().toGrid(prob.getModel(), pi.getBestPolicyValue()));
	}

	private static Map<String, Object> createParamsMap() {
		return CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0);
	}
}
