package org.jalt.model.algorithm.iteration.rl.erg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.util.CalcUtils;

/**
 * Learning + PPFERG + bloqueando a pior expressao de cada vez (com iteracao)
 */
public class MultiERGLearningBlockBadExp extends AbstractERGLearningBlockBadExp implements
		MultiERGLearning {

	private static final int MAX_IT = 5;
	private static final int TIME_TO_SLEEP = 50;
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
		int it = 0;

		final List<Policy> policies = new ArrayList<Policy>(learnings.size());
		for (ReinforcementLearning<ERG> learning : learnings) {
			runThread(pProblem, pParameters, learning, policies, count++);
		}

		// while (policies.size() < learnings.size()) {
		// if (it++ > pProblem.getModel().getStates().size() * MAX_IT)
		// throw new
		// RuntimeException("Tired of waiting agent/thread to finish. Secs: "
		// + (it * TIME_TO_SLEEP / 1000));
		// try {
		// Thread.sleep(TIME_TO_SLEEP);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

		while (policies.size() < 1) {
			if (it++ > pProblem.getModel().getStates().size() * MAX_IT)
				throw new RuntimeException("Tired of waiting agent/thread to finish. Secs: "
						+ (it * TIME_TO_SLEEP / 1000));
			try {
				synchronized (policies) {
					policies.wait(TIME_TO_SLEEP);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return policies.get(0);// not used (andvicoso)
	}

	protected void runThread(final Problem<ERG> pProblem, final Map<String, Object> pParameters,
			final ReinforcementLearning<ERG> learning, final List<Policy> policies, final int agent) {

		new Thread() {
			@Override
			public void run() {
				// Log.info("Started thread " + i);
				Map<String, Object> map = new HashMap<String, Object>(pParameters);
				map.put(ReinforcementLearning.AGENT_NAME, agent);

				Policy p = learning.run(pProblem, map);
				synchronized (policies) {
					policies.add(p);
					policies.notifyAll();
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
		sb.append("\nLearning algorithm: ").append(learnings.get(0).getClass().getSimpleName());
		int count = 0;
		for (ReinforcementLearning<ERG> learning : learnings) {
			sb.append("\nAgent: ").append(count++);
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
