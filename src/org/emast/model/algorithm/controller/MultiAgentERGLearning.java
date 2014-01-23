package org.emast.model.algorithm.controller;

import static org.emast.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.solution.Policy;
import org.emast.util.CalcUtils;

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

		sb.append(printMeanResults());

		return sb.toString();
	}

	private String printMeanResults() {
		double steps = 0;
		double episodes = 0;
		Collection<Integer> epis = new ArrayList<>();
		
		for (ReinforcementLearning<ERG> learning : learnings) {
			steps += learning.getMeanSteps();
			episodes += learning.getIterations();
			epis.add(learning.getIterations());
		}
		

		steps = steps / learnings.size();
		episodes = episodes / learnings.size();
		double episodes_std_dev =  CalcUtils.getStandardDeviation(episodes, epis);

		StringBuilder sb = new StringBuilder();
		sb.append("\nMulti Means: ");
		sb.append("\nEpisodes: ").append(episodes);
		sb.append("\nEpisodes (std dev): ").append(episodes_std_dev);
		sb.append("\nSteps (mean): ").append(steps);

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + " (" + learnings.get(0).getName() + ")";
	}
}
