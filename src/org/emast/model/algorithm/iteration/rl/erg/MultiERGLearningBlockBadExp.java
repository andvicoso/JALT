package org.emast.model.algorithm.iteration.rl.erg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.util.CalcUtils;

/**
 * Learning + PPFERG + bloqueando a pior expressão de cada vez (com iteração)
 */
public class MultiERGLearningBlockBadExp extends AbstractERGLearningBlockBadExp implements MultiERGLearning {

	private static final int MAX_IT = 600;
	private List<ReinforcementLearning<ERG>> learnings;

	public MultiERGLearningBlockBadExp(List<ReinforcementLearning<ERG>> learnings) {
		this.learnings = learnings;
		// replace the default stopping criterium for all learning algorithms
		for (ReinforcementLearning<ERG> learning : learnings) {
			learning.setStoppingCriterium(getStopCriteria());
		}
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + " (" + learnings.get(0).getName() + ")";
	}

	protected Policy runLearning(Problem<ERG> pProblem, Map<String, Object> pParameters) {
		int count = 0;
		List<Policy> policies = new ArrayList<Policy>(learnings.size());
		for (ReinforcementLearning<ERG> learning : learnings) {
			runThread(pProblem, pParameters, learning, policies, count++);
		}

		int it = 0;
		while (policies.size() < learnings.size()) {
			if (it++ > MAX_IT)
				throw new RuntimeException("Tired of waiting thread/agent " + it + " to finish.");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return policies.get(0);// TODO: ??
	}

	protected void runThread(final Problem<ERG> pProblem, final Map<String, Object> pParameters,
			final ReinforcementLearning<ERG> learning, final List<Policy> policies, final int i) {

		new Thread() {
			@Override
			public void run() {
				// Log.info("Started thread " + i);
				Map<String, Object> map = new HashMap<String, Object>(pParameters);
				map.put(ReinforcementLearning.AGENT_NAME, i);
				Policy p = learning.run(pProblem, map);
				synchronized (policies) {
					policies.add(p);
				}
				// Log.info("Finished thread " + i);
			}
		}.start();
	}

	@Override
	public List<ReinforcementLearning<ERG>> getLearnings() {
		return learnings;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder(super.printResults());

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
		Collection<Integer> epis = new ArrayList<Integer>();

		for (ReinforcementLearning<ERG> learning : learnings) {
			steps += learning.getMeanSteps();
			episodes += learning.getIterations();
			epis.add(learning.getIterations());
		}

		steps = steps / learnings.size();
		episodes = episodes / learnings.size();
		double episodes_std_dev = CalcUtils.getStandardDeviation(episodes, epis);

		StringBuilder sb = new StringBuilder();
		sb.append("\nMulti Means: ");
		sb.append("\nEpisodes: ").append(episodes);
		sb.append("\nEpisodes (std dev): ").append(episodes_std_dev);
		sb.append("\nSteps (mean): ").append(steps);

		return sb.toString();
	}

}
