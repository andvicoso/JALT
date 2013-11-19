package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.solution.Policy;

/**
 * 
 * @author Anderson
 */
public abstract class AbstractERGLearning implements Algorithm<ERG, Policy> {

	protected ReinforcementLearning<ERG> learning;

	public AbstractERGLearning(ReinforcementLearning<ERG> learning) {
		this.learning = learning;
	}

	public ReinforcementLearning<ERG> getLearning() {
		return learning;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nLearning algorithm: ").append(learning.getClass().getSimpleName());
		sb.append("\nBad exp reward param: ").append(BAD_EXP_VALUE);
		sb.append(learning.printResults());

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + "(" + learning.getName() + ")";
	}

}
