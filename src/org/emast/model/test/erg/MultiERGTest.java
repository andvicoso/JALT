package org.emast.model.test.erg;

import java.util.ArrayList;
import java.util.List;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmFactory;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.algorithm.iteration.rl.erg.MultiERGLearningBlockBadExp;
import org.emast.model.model.ERG;

/**
 * 
 * @author anderson
 */
@SuppressWarnings("rawtypes")
public class MultiERGTest extends AlgorithmTest {

	private int agents;

	public MultiERGTest(int agents, Class<? extends ReinforcementLearning> learning) {
		super(learning);
		this.agents = agents;
	}

	public AlgorithmFactory createAlgorithmFactory() {
		return new AlgorithmFactory() {
			@Override
			public Algorithm create() {
				List<ReinforcementLearning<ERG>> learnings = new ArrayList<>();
				for (int i = 0; i < agents; i++) {
					learnings.add(createAlgorithm());
				}

				return new MultiERGLearningBlockBadExp(learnings);
			}
		};
	}
}
