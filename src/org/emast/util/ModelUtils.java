package org.emast.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.model.impl.GridModel;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;

/**
 * 
 * @author andvicoso
 */
public class ModelUtils {

	private ModelUtils() {
	}

	public static Set<State> getStates(final Collection<Transition> pPi) {
		final Set<State> list = new HashSet<State>();
		for (final Transition trans : pPi) {
			list.add(trans.getState());
		}

		return list;
	}

	public static Set<Action> getActions(final Collection<Transition> pPi) {
		final Set<Action> result = new HashSet<Action>();
		for (final Transition trans : pPi) {
			result.add(trans.getAction());
		}

		return result;
	}

	public static <QT extends QTable<? extends QTableItem>> TransitionFunction createTransitionFunctionFrequency(
			final QT q) {
		TransitionFunction tf = new TransitionFunction() {
			@Override
			public double getValue(State pState, State pFinalState, Action pAction) {
				QTableItem item = q.get(pState, pAction);
				if (item != null) {
					State fstate = item.getFinalState();
					if (State.isValid(pFinalState, fstate)) {
						double total = q.getTotal(pState);
						return total != 0 ? item.getFrequency() / total : 0;
					}
				}
				return 0d;
			}
		};

		return tf;
	}

	public static <QT extends QTable<? extends QTableItem>> RewardFunction createRewardFunction(
			final QT q) {
		return new RewardFunction() {
			@Override
			public double getValue(State pState, Action pAction) {
				return q.get(pState, pAction).getReward();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <M extends MDP> M createModel(M oldModel) {
		Object ret = null;
		if (oldModel instanceof ERG) {
			if (oldModel instanceof Grid) {
				ret = new ERGGridModel(((Grid) oldModel).getRows(), ((Grid) oldModel).getCols());
			}
			ret = new ERGModel();
		} else {
			if (oldModel instanceof Grid) {
				ret = new GridModel(((Grid) oldModel).getRows(), ((Grid) oldModel).getCols());
			}
			ret = new MDPModel();
		}
		return (M) ret;
	}

	@SuppressWarnings("unchecked")
	public static <M extends MDP, QT extends QTable<? extends QTableItem>> M createModel(
			M oldModel, QT q) {
		MDP model = createModel(oldModel);
		// COPY MAIN PROPERTIES
		model.setStates(q.getStates());
		model.setActions(q.getActions());
		model.setAgents(oldModel.getAgents());
		// CREATE NEW REWARD FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		RewardFunction rf = createRewardFunction(q);
		model.setRewardFunction(rf);
		// CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		TransitionFunction tf = createTransitionFunctionFrequency(q);
		model.setTransitionFunction(tf);

		// Log.info("\nTransition Function\n" + new GridPrinter().print(tf,
		// model));

		return (M) model;
	}
}
