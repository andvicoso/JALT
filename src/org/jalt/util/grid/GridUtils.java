package org.jalt.util.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class GridUtils {

	private static final String GRID_STATE_SEP = "x";
	public static final int GRID_MAX_SIZE = 1000;// 100;
	public static final String GRID_STATE_FORMAT_PREFFIX = "%0";
	public static final String GRID_STATE_FORMAT_SUFFIX = "d";
	public static final String ZERO = "0";
	public static final String NORTH = "north";
	public static final String SOUTH = "south";
	public static final String WEST = "west";
	public static final String EAST = "east";
	public static final Action south = new Action("south");
	public static final Action east = new Action("east");
	public static final Action west = new Action("west");
	public static final Action north = new Action("north");
	public static final List<Action> GRID_ACTIONS = new ArrayList<Action>();
	public static final GridState[][] STATES_CACHE = new GridState[GRID_MAX_SIZE][GRID_MAX_SIZE];
	private static final Map<Integer, String> FORMATS_CACHE = new HashMap<Integer, String>();

	static {
		for (int i = 0; i < GRID_MAX_SIZE; i++) {
			for (int j = 0; j < GRID_MAX_SIZE; j++) {
				STATES_CACHE[i][j] = new GridState(i, j);
			}
		}
		// set default grid actions
		GRID_ACTIONS.add(north);
		GRID_ACTIONS.add(south);
		GRID_ACTIONS.add(west);
		GRID_ACTIONS.add(east);
	}

	private GridUtils() {
	}

	public static List<State> createStates(final int pRows, final int pCols) {
		final List<State> states = new ArrayList<State>();
		for (int i = 0; i < pRows; i++) {
			for (int j = 0; j < pCols; j++) {
				states.add(STATES_CACHE[i][j]);
			}
		}
		return states;
	}

	public static String getGridStateName(final int pRow, final int pCol) {
		String srow = Integer.toString(pRow);
		String scol = Integer.toString(pCol);
		int size = Math.max(srow.length(), scol.length());
		String format = getGridStateNameFormat(size);

		return new StringBuilder(String.format(format, pRow)).append(GRID_STATE_SEP)
				.append(String.format(format, pCol)).toString();
	}

	private static String getGridStateNameFormat(int size) {
		if (!FORMATS_CACHE.containsKey(size)) {
			FORMATS_CACHE.put(size, new StringBuilder(GRID_STATE_FORMAT_PREFFIX).append(size)
					.append(GRID_STATE_FORMAT_SUFFIX).toString());
		}
		return FORMATS_CACHE.get(size);
	}

	public static int getRow(final State pState) {
		if (pState instanceof GridState)
			return ((GridState) pState).getRow();
		return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[0]);
	}

	public static int getCol(final State pState) {
		if (pState instanceof GridState)
			return ((GridState) pState).getCol();
		return Integer.parseInt(pState.getName().split(GRID_STATE_SEP)[1]);
	}
}
