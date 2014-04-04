package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Map;

import org.emast.model.algorithm.iteration.rl.ReinforcementLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 * Learning + PPFERG + bloqueando a pior expressão de cada vez (com iteração)
 */
public class ERGLearningBlockBadExp extends AbstractERGLearningBlockBadExp implements SingleERGLearning {

	private ReinforcementLearning<ERG> learning;

	public ERGLearningBlockBadExp(ReinforcementLearning<ERG> learning) {
		this.learning = learning;
		// replace the default stopping criterium of the learning algorithm
		learning.setStoppingCriterium(getStopCriteria());
	}

	@Override
	public ReinforcementLearning<ERG> getLearning() {
		return learning;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder(super.printResults());
		sb.append("\nLearning algorithm: ").append(learning.getClass().getSimpleName());
		sb.append(learning.printResults());

		return sb.toString();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + "(" + learning.getName() + ")";
	}

	protected Policy runLearning(Problem<ERG> prob, Map<String, Object> pParameters) {
		return learning.run(prob, pParameters);
	}
}
