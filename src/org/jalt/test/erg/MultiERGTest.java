package org.jalt.test.erg;

import java.util.ArrayList;
import java.util.List;

import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.AlgorithmFactory;
import org.jalt.model.algorithm.rl.ReinforcementLearning;
import org.jalt.model.algorithm.rl.archs.erg.MultiERGLearningBlockBadExp;
import org.jalt.model.model.ERG;

/**
 * 
 * @author andvicoso
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
