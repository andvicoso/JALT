package org.jalt.model.test;

import java.util.Collections;
import java.util.Map;

import org.jalt.model.algorithm.iteration.ValueIteration;
import org.jalt.model.algorithm.iteration.rl.DynaQ;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.test.erg.AlgorithmTest;
import org.jalt.model.test.erg.MultiERGTest;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.FileUtils;
import org.jalt.util.PolicyUtils;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class MainTest {

	private static final String DEFAULT_PATH = "D:\\Dev\\Workspaces\\Projects\\Private\\JALT\\problems\\GenericERGProblem\\";

	public static void main(final String[] pArgs) {
		Map<String, Object> params = createParamsMap();
		String path;
		Problem prob;
		AlgorithmTest algTest;
		// int count = 0;

		// path = getPath("nov13\\five\\0_problem");
		path = getPath("big\\50x25\\94_problem");

		prob = FileUtils.fromFile(path);
		// prob = new
		// ProblemsCLI(GenericERGProblemFactory.createDefaultFactory(2,
		// 10)).run();
		// prob =
		// ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one").get(0);
		// prob = ProblemIntroVI.getProblemIntroVI2();
		// prob = AntennaExamples.getSMC13();

		// for (Problem<ERG> prob :
		// ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one")) {

		runVI(prob, params);

		// Log.info("\n################################");
		// Log.info("TEST RUN " + count++);

		// algTest = new AlgorithmTest(QLearning.class);
		algTest = new MultiERGTest(5, DynaQ.class);
		Test test = new Test(prob, algTest.createAlgorithmFactory());
		test.run(params);
		// }
	}

	private static String getPath(String str) {
		return DEFAULT_PATH + str + Problem.PROB_EXT;
	}

	public static void runVI(Problem prob, Map<String, Object> params) {
		// long ini = System.currentTimeMillis();
		ValueIteration vi = new ValueIteration();
		Policy pi = vi.run(prob, Collections.emptyMap());
		// long end = System.currentTimeMillis();
		// System.out.print(end - ini);
		params.put(PolicyUtils.BEST_VALUES_STR, pi.getBestPolicyValue());

		//ImageUtils.save(ImageUtils.create(prob, pi), "final_vi.png");

		// Log.info("\nV-Values VI: \n" + new
		// GridPrinter().toGrid(prob.getModel(), pi.getBestPolicyValue()));
	}

	private static Map<String, Object> createParamsMap() {
		return CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0);
	}
}
