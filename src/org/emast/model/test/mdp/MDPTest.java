package org.emast.model.test.mdp;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.model.test.mdp.hunterprey.HunterPreyProblemFactory;
import org.emast.util.CollectionsUtils;

/**
 * 
 * @author anderson
 */
public class MDPTest {

	private static AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return new QLearning();
			}
		};
	}

	public static void main(final String[] pArgs) {
		final Problem p = new HunterPreyProblemFactory(10, 10, 1, 1).create();
		Test test = new Test(p, createAlgorithmFactory());
		test.run(CollectionsUtils.asMap(ReinforcementLearning.AGENT_NAME, 0));
	}
}
