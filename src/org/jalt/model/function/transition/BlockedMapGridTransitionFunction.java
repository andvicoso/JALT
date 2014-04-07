package org.jalt.model.function.transition;

import static org.jalt.util.grid.GridUtils.STATES_CACHE;
import static org.jalt.util.grid.GridUtils.east;
import static org.jalt.util.grid.GridUtils.north;
import static org.jalt.util.grid.GridUtils.south;
import static org.jalt.util.grid.GridUtils.west;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridUtils;

public class BlockedMapGridTransitionFunction extends GridTransitionFunction {

	protected final Map<State, Set<Action>> blocked;

	public BlockedMapGridTransitionFunction(int pRows, int pCols, Map<State, Set<Action>> blocked) {
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
		Map<State, Action> possibleMovs;
		final Set<Action> actionsBlocked = blocked.get(GridUtils.STATES_CACHE[pRow][pCol]);

		if (actionsBlocked != null && !actionsBlocked.isEmpty()) {
			possibleMovs = new HashMap<State, Action>(4);
			if (pRow + 1 < getRows() && !actionsBlocked.contains(south)) {
				possibleMovs.put(STATES_CACHE[(pRow + 1)][pCol], south);
			}
			if (pCol + 1 < getCols() && !actionsBlocked.contains(east)) {
				possibleMovs.put(STATES_CACHE[pRow][pCol + 1], east);
			}
			if (pRow - 1 >= 0 && !actionsBlocked.contains(north)) {
				possibleMovs.put(STATES_CACHE[pRow - 1][pCol], north);
			}
			if (pCol - 1 >= 0 && !actionsBlocked.contains(west)) {
				possibleMovs.put(STATES_CACHE[pRow][pCol - 1], west);
			}
		} else
			possibleMovs = super.getTransitions(pRow, pCol);
		return possibleMovs;
	}
}