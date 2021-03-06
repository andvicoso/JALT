package org.jalt.model.function.transition;

import static org.jalt.util.grid.GridUtils.STATES_CACHE;
import static org.jalt.util.grid.GridUtils.east;
import static org.jalt.util.grid.GridUtils.north;
import static org.jalt.util.grid.GridUtils.south;
import static org.jalt.util.grid.GridUtils.west;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;
import org.jalt.util.grid.GridUtils;

/**
 * 
 * @author andvicoso
 */
public class GridTransitionFunction extends TransitionFunction {

	private final int rows;
	private final int cols;

	public GridTransitionFunction(int pRows, int pCols) {
		rows = pRows;
		cols = pCols;
	}

	private Map<State, Action> getTransitions(State pState) {
		int row;
		int col;
		if (pState instanceof GridState) {
			GridState gs = (GridState) pState;
			row = gs.getRow();
			col = gs.getCol();
		} else {
			row = GridUtils.getRow(pState);
			col = GridUtils.getCol(pState);
		}
		return getTransitions(row, col);
	}

	public Map<State, Action> getTransitions(int pRow, int pCol) {
		final Map<State, Action> possibleMovs = new HashMap<>(4);

		if (pRow + 1 < rows) {
			possibleMovs.put(STATES_CACHE[(pRow + 1)][pCol], south);
		}
		if (pCol + 1 < cols) {
			possibleMovs.put(STATES_CACHE[pRow][pCol + 1], east);
		}
		if (pRow - 1 >= 0) {
			possibleMovs.put(STATES_CACHE[pRow - 1][pCol], north);
		}
		if (pCol - 1 >= 0) {
			possibleMovs.put(STATES_CACHE[pRow][pCol - 1], west);
		}

		return possibleMovs;
	}

	public Map<State, Double> getReachableStatesValues(final Collection<State> pModelStates,
			final State pState, final Action pAction) {
		Map<State, Action> possibleMovs = getTransitions(pState);
		Map<State, Double> ret = new HashMap<State, Double>(possibleMovs.size());

		for (State state : possibleMovs.keySet()) {
			double v = getValue(pState, state, pAction);
			if (v > 0)
				ret.put(state, v);
		}

		return ret;
	}

	public double getSum(Collection<State> states, State state, Action action, Map<State, Double> v) {
		double sum = 0;
		Map<State, Action> possibleMovs = getTransitions(state);

		if (possibleMovs.values().contains(action))
			for (State finalState : possibleMovs.keySet()) {
				Double trans = getValue(state, finalState, action);
				sum += trans * getVValue(v, finalState);
			}

		return sum;
	}

	public State getNextState(GridState state, Action action) {
		State next = state;
		int pRow = state.getRow();
		int pCol = state.getCol();

		if (action.equals(south) && pRow + 1 < rows) {
			next = STATES_CACHE[(pRow + 1)][pCol];
		} else if (action.equals(east) && pCol + 1 < cols) {
			next = STATES_CACHE[pRow][pCol + 1];
		} else if (action.equals(north) && pRow - 1 >= 0) {
			next = STATES_CACHE[pRow - 1][pCol];
		} else if (action.equals(west) && pCol - 1 >= 0) {
			next = STATES_CACHE[pRow][pCol - 1];
		}

		return next;
	}

	@Override
	public double getValue(State pState, State pFinalState, Action pAction) {
		if (pAction != null && pState != null && areNeighbours(pState, pFinalState)) {
			final Map<State, Action> targets = getTransitions(pState);

			for (Map.Entry<State, Action> entry : targets.entrySet()) {
				State state = entry.getKey();
				Action action = entry.getValue();

				if (action != null && state != null && Action.isValid(pAction, action)
						&& State.isValid(state, pFinalState)) {
					return 1d / targets.size();
				}
			}
		}

		return 0;
	}

	private boolean areNeighbours(State pState, State pFinalState) {
		if (pState instanceof GridState && pFinalState instanceof GridState) {
			GridState s = (GridState) pState;
			GridState f = (GridState) pFinalState;
			return s.isNeighbour(f);
		}
		return true;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}
}
