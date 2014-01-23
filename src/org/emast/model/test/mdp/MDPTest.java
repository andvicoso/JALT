package org.emast.model.test.mdp;

import java.util.List;
import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.iteration.rl.DynaQ;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.BatchTest;
import org.emast.model.test.Test;
import org.emast.util.CollectionsUtils;
import org.emast.util.ProblemsCLI;

/**
 * 
 * @author anderson
 */
public class MDPTest {

	private static AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return new DynaQ<>();
			}
		};
	}

	public static void main(final String[] pArgs) {
		int count = 0;
		List<Problem<ERG>> ps = ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\one");
		for (Problem<ERG> prob : ps) {
			Map<String, Object> params = CollectionsUtils
					.asMap(ReinforcementLearning.AGENT_NAME, 0);
			//Problem<MDP> p = ToRL.convert(prob);
			//ValueIteration<MDP> vi = new ValueIteration<>();
			//Policy best = vi.run(p, Collections.<String, Object> emptyMap());
			//params.put("policy", best.getBestPolicyValue());
			Log.info("\n################################");
			Log.info("TEST RUN " + count++);
			Test test = new BatchTest(prob, createAlgorithmFactory());
			test.run(params);
		}
	}
}
