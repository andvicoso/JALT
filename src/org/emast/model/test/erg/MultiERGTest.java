package org.emast.model.test.erg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.controller.MultiERGLearningBlockEachBadExp;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.BatchTest;
import org.emast.model.test.Test;
import org.emast.model.test.erg.antenna.AntennaExamples;
import org.emast.model.test.erg.generic.GenericERGProblemFactory;
import org.emast.util.ProblemsCLI;

/**
 * 
 * @author anderson
 */
public class MultiERGTest {

	private static Problem createFromCLI() {
		ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();
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
		return new QLearning<ERG>();
	}

	public static void main(final String[] pArgs) {
		// AntennaCoverageProblemFactory.createDefaultFactory();
		Problem<ERG> p =  createFromCLI();//AntennaExamples.getSMC13Multi();//
		Test test = new Test(p, createAlgorithmFactory(p.getInitialStates().size()));
		test.run(new HashMap<String, Object>());
	}
}
