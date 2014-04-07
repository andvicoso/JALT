package org.jalt.model.test;

import java.util.Collections;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.iteration.ValueIteration;
import org.jalt.model.algorithm.iteration.rl.QLearning;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.test.erg.AlgorithmTest;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.PolicyUtils;
import org.jalt.util.grid.GridPrinter;
import org.jalt.view.ui.cli.ProblemsCLI;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings(value = { "rawtypes", "unchecked" })
public class MainTest {

	public static void main(final String[] pArgs) {
		// int count = 0;
		Problem prob = ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one").get(0);// ProblemIntroVI.getProblemIntroVI2();//AntennaExamples.getSMC13();
		// List<Problem<ERG>> ps =
		// ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one");
		// for (Problem<ERG> prob : ps) {

		AlgorithmTest algTest = new AlgorithmTest(QLearning.class);
		Map<String, Object> params = createParamsMap();
		runVI(prob, params);

		// Log.info("\n################################");
		// Log.info("TEST RUN " + count++);

		Test test = new BatchTest(prob, algTest.createAlgorithmFactory());
		test.run(params);
		// }
	}

	public static void runVI(Problem prob, Map<String, Object> params) {
		Policy best = new ValueIteration().run(prob, Collections.emptyMap());
		params.put(PolicyUtils.BEST_VALUES_STR, best.getBestPolicyValue());

		 Log.info("\nV-Values VI: \n" + new
		 GridPrinter().toGrid(prob.getModel(), best.getBestPolicyValue()));
	}

	private static Map<String, Object> createParamsMap() {
		return CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0);
	}
}
