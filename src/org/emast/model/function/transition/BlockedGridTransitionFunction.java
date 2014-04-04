package org.emast.model.function.transition;

import static org.emast.util.grid.GridUtils.STATES_CACHE;
import static org.emast.util.grid.GridUtils.east;
import static org.emast.util.grid.GridUtils.north;
import static org.emast.util.grid.GridUtils.south;
import static org.emast.util.grid.GridUtils.west;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.state.State;

public class BlockedGridTransitionFunction extends GridTransitionFunction {

	protected final Set<State> blocked;

	public BlockedGridTransitionFunction(int pRows, int pCols, Set<State> blocked) {
		super(pRows, pCols);
		this.blocked = blocked;
	}

	@Override
	public double getValue(State pState, State pFinalState, Action pAction) {
		return isBlocked(pState, pFinalState, pAction) ? 0.0 : super.getValue(pState, pFinalState,
				pAction);
	}

	@Override
	public Map<State, Action> getTransitions(int pRow, int pCol) {
		Map<State, Action> possibleMovs = new HashMap<State, Action>(4);

		if (pRow + 1 < getRows() && !blocked.contains(STATES_CACHE[(pRow + 1)][pCol])) {
			possibleMovs.put(STATES_CACHE[(pRow + 1)][pCol], south);
		}

		if (pCol + 1 < getCols() && !blocked.contains(STATES_CACHE[pRow][pCol + 1])) {
			possibleMovs.put(STATES_CACHE[pRow][pCol + 1], east);
		}
		
		if (pRow - 1 >= 0 && !blocked.contains(STATES_CACHE[pRow - 1][pCol])) {
			possibleMovs.put(STATES_CACHE[pRow - 1][pCol], north);
		}

		if (pCol - 1 >= 0 && !blocked.contains(STATES_CACHE[pRow][pCol - 1])) {
			possibleMovs.put(STATES_CACHE[pRow][pCol - 1], west);
		}

		return possibleMovs;
	}

	private boolean isBlocked(State pState, State pFinalState, Action pAction) {
		return blocked.contains(pFinalState);// || blocked.contains(pState);
	}
}
