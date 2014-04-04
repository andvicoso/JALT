package org.emast.model.test.erg;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.erg.generic.GenericERGProblemFactory;
import org.emast.view.ui.cli.ProblemsCLI;

@SuppressWarnings("rawtypes")
public class AlgorithmTest {

	private Class<? extends ReinforcementLearning> learningClass;

	public AlgorithmTest(Class<? extends ReinforcementLearning> learning) {
		this.learningClass = learning;
	}

	public Problem createFromCLI(int agents) {
		ProblemFactory factory = GenericERGProblemFactory.createDefaultFactory();
		return new ProblemsCLI(factory).run();
	}

	public AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return createAlgorithm();
			}
		};
	}

	public ReinforcementLearning createAlgorithm() {
		try {
			return learningClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		}
		return null;
	}

}