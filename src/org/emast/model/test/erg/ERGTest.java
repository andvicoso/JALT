package org.emast.model.test.erg;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.iteration.rl.erg.ERGLearningBlockBadExp;

/**
 * 
 * @author anderson
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
