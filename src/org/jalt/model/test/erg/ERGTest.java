package org.jalt.model.test.erg;

import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.AlgorithmFactory;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.iteration.rl.erg.ERGLearningBlockBadExp;

/**
 * 
 * @author andvicoso
 */
@SuppressWarnings("rawtypes")
public class ERGTest extends AlgorithmTest {

	public ERGTest(Class<? extends ReinforcementLearning> learning) {
		super(learning);
	}

	public AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				return new ERGLearningBlockBadExp(createAlgorithm());
			}
		};
	}
}
