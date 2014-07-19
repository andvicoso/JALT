package org.jalt.model.algorithm.reachability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.iteration.IterationAlgorithm;
import org.jalt.model.exception.InvalidExpressionException;
import org.jalt.model.model.MDP;
import org.jalt.model.model.SRG;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.model.transition.Transition;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.ModelUtils;

/**
 * Strong probabilistic planning algorithm
 * 
 * @author andvicoso
 * @param <P>
 *            Simple reachability problem to be resolved
 */
public class PPF<M extends MDP & SRG> extends IterationAlgorithm<M, Policy> {

	protected static final Double INITIAL_VALUE = 1d;

	/**
	 * if is true, then the algorithm will stop when it finds a valid path to
	 * some agent's initial position. Else, it will find all the paths for all
	 * valid states.
	 */
	protected final boolean stopWhenOneAgentFindPath;

	/**
	 * 
	 * @param pStopWhenOneAgentFindPath
	 *            if is true, then the algorithm will stop when it finds a valid
	 *            path to some agent's initial position. Else, it will find all
	 *            the paths for all valid states.
	 */
	public PPF(final boolean pStopWhenOneAgentFindPath) {
		stopWhenOneAgentFindPath = pStopWhenOneAgentFindPath;
	}

	public PPF() {
		this(false);
	}

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		model = pProblem.getModel();
		final Map<State, Double> values = new TreeMap<State, Double>();
		Policy pi = new Policy();
		Policy pi2;
		// get all the states that satisfies the goal
		final Collection<State> intension = intension(model.getGoal());
		// initialize pi and values
		for (final State state : intension) {
			values.put(state, INITIAL_VALUE);
			pi.put(state, Action.TRIVIAL_ACTION);
		}

		do {
			final Collection<State> c = pi.getStates();
			if (isStop(pProblem, pParameters, c)) {
				break;
			}

			pi2 = pi;
			final Set<Transition> prunedStrongImage = prune(getStrongImage(c), c);
			pi = choose(values, prunedStrongImage);
			pi.putAll(pi2);
			episodes++;
		} while (!pi.equals(pi2));

		return pi;
	}

	protected boolean isStop(Problem<M> pProblem, Map<String, Object> pParameters,
			Collection<State> c) {
		Collection<State> initialStates = pProblem.getInitialStates().values();
		// If the flag "stopWhenOneAgentFindPath" is true, then the algorithm
		// will stop when it finds a valid path to some agent's initial
		// position.
		// Else, it will find all paths for all states.
		if (stopWhenOneAgentFindPath)
			return !Collections.disjoint(c, initialStates);
		return c.containsAll(initialStates);
	}

	protected Policy choose(final Map<State, Double> pValues, final Collection<Transition> pPrune) {
		final Policy pi = new Policy();

		for (final State state : ModelUtils.getStates(pPrune)) {
			Map<Action, Double> q = getQValue(pPrune, state, pValues);
			// if found something
			if (q.size() > 0) {
				// get the max value for q
				final Double max = Collections.max(q.values());
				pValues.put(state, max);
				pi.put(state, CollectionsUtils.getKeysForValue(q, max).iterator().next());
			}
		}

		return pi;
	}

	protected Collection<State> intension(final Expression pExpression) {
		try {
			return model.getPropositionFunction().intension(model.getStates(), pExpression);
		} catch (InvalidExpressionException ex) {
			ex.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 * Para todas as transicoes da imagem forte, corta todas que nao pertencam
	 * ao conjunto da cobertura
	 * 
	 * @param pStrongImage
	 * @param pC
	 * @return
	 */
	protected Set<Transition> prune(final Set<Transition> pStrongImage, final Collection<State> pC) {
		final Set<Transition> result = new HashSet<Transition>();
		for (final Transition transition : pStrongImage) {
			if (!pC.contains(transition.getState())) {
				result.add(transition);
			}
		}

		return result;
	}

	protected Set<Transition> getWeakImage(final Collection<State> pC) {
		final Set<Transition> result = new HashSet<Transition>();

		for (final State state : model.getStates()) {
			for (final Action actions : model.getActions()) {
				final Collection<State> reachableStates = model.getTransitionFunction()
						.getReachableStates(model.getStates(), state, actions);
				if (!Collections.disjoint(pC, reachableStates)) {
					final Transition t = new Transition(state, actions);
					if (!result.contains(t)) {
						result.add(t);
					} else {
						result.remove(t);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Retorna as transicoes que alcancam o conjunto da cobertura
	 * 
	 * @param pC
	 * @return
	 */
	protected Set<Transition> getStrongImage(final Collection<State> pC) {
		final Set<Transition> result = new HashSet<Transition>();

		for (final State state : model.getStates()) {
			for (final Action action : model.getActions()) {// tf.getActionsFrom
				final Collection<State> reachableStates = model.getTransitionFunction()
						.getReachableStates(model.getStates(), state, action);
				if (!reachableStates.isEmpty() && pC.containsAll(reachableStates)) {
					final Transition t = new Transition(state, action);
					if (!result.contains(t)) {
						result.add(t);
					} else {
						result.remove(t);
					}
				}
			}
		}

		return result;
	}

	private Collection<Action> getActions(Collection<Transition> pPrune, State state) {
		Collection<Action> actions = new ArrayList<Action>();
		for (Transition t : pPrune) {
			if (t.getState().equals(state)) {
				actions.add(t.getAction());
			}
		}

		return actions;
	}

	protected Map<Action, Double> getQValue(final Collection<Transition> pPrune, final State state,
			final Map<State, Double> pValues) {
		final Map<Action, Double> q = new TreeMap<Action, Double>();
		// search for the Qs values for state
		for (final Action action : getActions(pPrune, state)) {
			double sum = 0;
			for (final State reachableState : model.getTransitionFunction().getReachableStates(
					model.getStates(), state, action)) {
				final double trans = model.getTransitionFunction().getValue(state, reachableState,
						action);
				if (trans > 0 && pValues.get(reachableState) != null) {
					sum += trans * pValues.get(reachableState);
				}
			}
			double v = model.getRewardFunction().getValue(state, action) + gama * sum;
			q.put(action, v);
		}
		return q;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
}
