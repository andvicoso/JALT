package org.jalt.model.algorithm.reachability;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.model.transition.Transition;

/**
 * Strong probabilistic planning algorithm for extended reachability goals. Based on
 * https://link.springer.com/chapter/10.1007/978-3-540-88636-5_61
 * 
 * @author Anderson
 *
 * @param <M>
 */
public class PPFERG<M extends ERG> extends PPF<M> {

	public PPFERG(boolean pStopWhenOneAgentFindPath) {
		super(pStopWhenOneAgentFindPath);
	}

	public PPFERG() {
		this(false);
	}

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		// get model
		model = pProblem.getModel();
		// print initial msg
		// String modelName = model.getClass().getSimpleName();
		// Log.info("\nRunning PPFERG for " + modelName);
		// get the initial state for only one agent
		final Collection<State> preserveIntension = intension(model.getPreservationGoal());
		final Collection<State> goalsIntension = intension(model.getGoal());
		final Map<State, Double> values = new HashMap<State, Double>();
		Collection<State> c;
		Policy pi = new Policy();
		Policy pi2;
		// there isn't a state which satisfies the problem goal
		if (goalsIntension.isEmpty()) {
			return pi;
		}

		for (final State state : goalsIntension) {
			values.put(state, INITIAL_VALUE);
			pi.put(state, Action.TRIVIAL_ACTION, INITIAL_VALUE);
		}

		do {
			c = pi.getStates();
			if (isStop(pProblem, pParameters, c)) {
				break;
			}

			pi2 = pi;
			final Collection<Transition> strongImage = getStrongImage(c);
			final Collection<Transition> prunedStrongImage = prune(strongImage, c, preserveIntension);
			pi = choose(values, prunedStrongImage);
			pi.putAll(pi2);
			iterations++;
		} while (!pi.equals(pi2) && !preserveIntension.equals(c));

		return pi;
	}

	protected Set<Transition> prune(final Collection<Transition> pStrongImage, final Collection<State> pS, final Collection<State> pI) {
		final Set<Transition> result = new HashSet<Transition>();

		for (final Transition t : pStrongImage) {
			if (!pS.contains(t.getState()) && pI.contains(t.getState())) {
				result.add(t);
			}
		}

		return result;
	}

	public boolean isStopWhenOneAgentFindPath() {
		return stopWhenOneAgentFindPath;
	}
}
