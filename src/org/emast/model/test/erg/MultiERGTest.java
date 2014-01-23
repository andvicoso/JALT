package org.emast.model.test.erg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.emast.infra.log.Log;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.controller.MultiERGLearningBlockEachBadExp;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.iteration.rl.SARSA;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.BatchTest;
import org.emast.model.test.Test;
import org.emast.model.test.erg.generic.GenericERGProblemFactory;
import org.emast.util.ProblemsCLI;

/**
 * 
 * @author anderson
 */
public class MultiERGTest {
	private static int agents = 3;

	private static Problem createFromCLI() {
		ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory(agents);
		return new ProblemsCLI(factory).run();
	}

	private static AlgorithmFactory createAlgorithmFactory(final int agents) {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				List<ReinforcementLearning<ERG>> learnings = new ArrayList<>();
				for (int i = 0; i < agents; i++) {
					learnings.add(createAlgorithm());
				}

				return new MultiERGLearningBlockEachBadExp(learnings);
			}
		};
	}

	private static ReinforcementLearning<ERG> createAlgorithm() {
		return new SARSA<ERG>();
	}

	public static void main(final String[] pArgs) {
		// AntennaCoverageProblemFactory.createDefaultFactory();
		// Problem<ERG> p = createFromCLI();//AntennaExamples.getSMC13Multi();//
		List<Problem<ERG>> ps = ProblemsCLI.getAllFromDir("GenericERGProblem\\nov13\\three");
		int count = 0;
		for (Problem<ERG> p : ps) {
			//Map<String, Object> params = new HashMap<>();
			//ValueIteration<MDP> vi = new ValueIteration<>();
			//Policy best = vi.run(ToRL.convert(p), Collections.<String, Object> emptyMap());
			//params.put("policy", best);
			Log.info("\n################################");
			Log.info("TEST RUN " + (count++));
			Test test = new BatchTest(p, createAlgorithmFactory(agents));
			test.run(new HashMap<String, Object>());
		}
	}
}
