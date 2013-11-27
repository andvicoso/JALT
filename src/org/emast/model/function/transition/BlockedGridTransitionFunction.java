package org.emast.model.function.transition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.grid.GridUtils;

public class BlockedGridTransitionFunction extends GridTransitionFunction {

	protected final Map<State, Set<Action>> blocked;

	public BlockedGridTransitionFunction(int pRows, int pCols, Map<State, Set<Action>> blocked) {
		super(pRows, pCols);
		this.blocked = blocked;
	}

	@Override
	public double getValue(State pState, State pFinalState, Action pAction) {
		return isBlocked(pState, pAction) ? 0.0 : super.getValue(pState, pFinalState, pAction);
	}

	protected boolean isBlocked(State pState, Action pAction) {
		return blocked.containsKey(pState) && blocked.get(pState).contains(pAction);
	}

	@Override
	public Map<State, Action> getTransitions(int pRow, int pCol) {
		final Map<State, Action> possibleMovs = super.getTransitions(pRow, pCol);
		final Set<Action> actionsBlocked = blocked.get(GridUtils.STATES_CACHE[pRow][pCol]);
		final Map<State, Action> ret = new HashMap<>(possibleMovs);

		if (actionsBlocked != null && !actionsBlocked.isEmpty())
			for (State state : possibleMovs.keySet()) {
				Action action = possibleMovs.get(state);
				if (actionsBlocked.contains(action))
					ret.remove(state);
			}

		return ret;
	}

}
