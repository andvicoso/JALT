package org.emast.model.algorithm.iteration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.grid.GridPrinter;

public class ValueIteration<M extends MDP> extends IterationAlgorithm<M, Policy> implements
		PolicyGenerator<M>, IterationValues {

	private Map<State, Double> lastv = Collections.emptyMap();
	private Map<State, Double> v = Collections.emptyMap();
	private StoppingCriterium stoppingCriterium;

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		Policy pi;
		model = pProblem.getModel();
		// Start the main loop
		// When the maximmum error is greater than the defined error,
		// the best policy is found
		do {
			lastv = v;
			episodes++;
			// set initial v
			v = new HashMap<State, Double>();
			// create the policy
			pi = new Policy();
			// for each state
			for (State state : model.getStates()) {
				Map<Action, Double> q = getQ(model, state);
				// if found some action and value
				if (!q.isEmpty()) {
					// get the max value for q
					Double max = Collections.max(q.values());
					// save the max value
					v.put(state, max);
					// add to the policy
					pi.put(state, q);
				}
			}
			// Log.info("\n"+printResults());
			Log.info("\n" + new GridPrinter().toTable(v, 10, 10));
			// Log.info("\n"+pProblem.toString(pi));
			Log.info(episodes);
		} while (stoppingCriterium.isStop(null));

		// Log.info("\n"+printResults());

		return pi;
	}

	private Map<Action, Double> getQ(MDP pModel, State pState) {
		Map<Action, Double> q = new HashMap<Action, Double>();
		Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(
				pModel.getActions(), pState);
		// search for the Q v for each state
		for (Action action : actions) {
			double reward = pModel.getRewardFunction().getValue(pState, action);
			double value = reward + getGama() * getSum(pModel, pState, action);
			q.put(action, value);
		}
		return q;
	}

	private double getSum(MDP pModel, State pState, Action pAction) {
		double sum = 0;

		if (!lastv.isEmpty()) {
			Map<State, Double> rv = pModel.getTransitionFunction().getReachableStatesValues(
					pModel.getStates(), pState, pAction);

			for (Map.Entry<State, Double> entry : rv.entrySet()) {
				State state = entry.getKey();
				Double value = entry.getValue();
				if (lastv.containsKey(state)) {
					sum += value * lastv.get(state);
				}
			}
		}

		return sum;
	}

	@Override
	public String printResults() {
		String lvs;

		if (model instanceof Grid) {
			int rows = ((Grid) model).getRows();
			int cols = ((Grid) model).getCols();
			lvs = new GridPrinter().toTable(lastv, rows, cols);
		} else {
			lvs = lastv.toString();
		}

		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("\nLast values:\n").append(lvs);

		return sb.toString();
	}

	@Override
	public Map<State, Double> getCurrentValues() {
		return v;
	}

	@Override
	public Map<State, Double> getLastValues() {
		return lastv;
	}
}
