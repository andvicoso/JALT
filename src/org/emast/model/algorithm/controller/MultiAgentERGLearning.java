package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.List;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.solution.Policy;

public abstract class MultiAgentERGLearning implements Algorithm<ERG, Policy> {

	protected List<ReinforcementLearning<ERG>> learnings;

	public MultiAgentERGLearning(List<ReinforcementLearning<ERG>> learnings) {
		this.learnings = learnings;
	}

	public List<ReinforcementLearning<ERG>> getLearnings() {
		return learnings;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nBad exp reward param: ").append(BAD_EXP_VALUE);

		for (ReinforcementLearning<ERG> learning : learnings) {
			sb.append("\nLearning algorithm: ").append(learning.getClass().getSimpleName());
			sb.append(learning.printResults());
		}

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + " (" + learnings.get(0).getName() + ")";
	}
}
