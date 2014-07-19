package org.jalt.util.grid;

import static org.jalt.util.grid.GridUtils.east;
import static org.jalt.util.grid.GridUtils.north;
import static org.jalt.util.grid.GridUtils.south;
import static org.jalt.util.grid.GridUtils.west;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.Grid;
import org.jalt.model.model.MDP;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.solution.Plan;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.util.DefaultTestProperties;

/**
 * 
 * @author andvicoso
 */
public class GridPrinter {

	private static final int MAX_STR_SIZE = 3;
	public static final String NORTH_SYMBOL = " ^";
	public static final String SOUTH_SYMBOL = " v";
	public static final String WEST_SYMBOL = " <";
	public static final String EAST_SYMBOL = " >";
	public static final String VALUE_FORMAT = "%1$.2f";
	public static final String SPACE = " ";
	public static final String VALUE_FORMAT_STATE = "%1$.4f";
	public static final String LINE_BREAK = "|\n";
	public static final String FORMAT_1 = "%1$";
	public static final String S = "s";
	public static final String COLUMN_SEP = "|";

	public String toGrid(MDP model, Map<State, Double> map) {
		if (model instanceof Grid) {
			int rows = ((Grid) model).getRows();
			int cols = ((Grid) model).getCols();
			return toTable(map, rows, cols);
		}
		return map.toString();
	}

	public <M extends MDP & Grid> String print(M pModel) {
		String[][] grid = getGrid(pModel, Collections.<Integer, State> emptyMap(),
				Collections.<State> emptySet());
		return toTable(grid);
	}

	public <M extends MDP & Grid> String print(M pModel, Map<Integer, State> pInitialStates,
			Set<State> pFinalStates, Object pResult) {
		String[][] grid = getGrid(pModel, pInitialStates, pFinalStates);

		if (pResult != null) {
			if (pResult instanceof Policy) {
				fillWithActions(grid, (Policy) pResult);
			} else if (pResult instanceof Plan) {
				fillWithActions(grid, pInitialStates, (Plan) pResult);
			}
		}

		return toTable(grid);
	}

	public <M extends MDP & Grid> String[][] getGrid(M pModel, Map<Integer, State> pInitialStates,
			Set<State> pFinalStates) {
		String[][] grid = createGrid(pModel);

		if (pModel instanceof ERG) {
			addPropositions((ERG) pModel, pModel.getStates(), grid);
		}

		addInitialStates(pInitialStates, grid);
		addFinalStates(pFinalStates, grid);

		grid = addIndexes(grid, pModel.getRows(), pModel.getCols());

		return grid;
	}

	private void addInitialStates(Map<Integer, State> pInitialStates, String[][] pGrid) {
		int agent = 0;
		for (State initState : pInitialStates.values()) {
			int row = GridUtils.getRow(initState);
			int col = GridUtils.getCol(initState);
			pGrid[row][col] = agent + SPACE + pGrid[row][col];
			agent++;
		}
	}

	private void addFinalStates(Set<State> pFinalStates, String[][] pGrid) {
		for (State initState : pFinalStates) {
			int row = GridUtils.getRow(initState);
			int col = GridUtils.getCol(initState);
			if (!pGrid[row][col].contains(DefaultTestProperties.FINAL_GOAL)) {
				pGrid[row][col] = DefaultTestProperties.FINAL_GOAL + SPACE + pGrid[row][col];
			}
		}
	}

	public String toTable(String[][] pGrid) {
		// Find out what the maximum number of columns is in any row
		int maxColumns = 0;
		for (int i = 0; i < pGrid.length; i++) {
			maxColumns = Math.max(pGrid[i].length, maxColumns);
		}
		// Find the maximum length of a string in each column
		int[] lengths = new int[maxColumns];
		for (int i = 0; i < pGrid.length; i++) {
			for (int j = 0; j < pGrid[i].length; j++) {
				String value = pGrid[i][j];
				pGrid[i][j] = value == null ? SPACE : value;
				lengths[j] = Math.max(pGrid[i][j].length(), lengths[j]);
			}
		}
		// Generate a format string for each column
		String[] formats = new String[lengths.length];
		for (int i = 0; i < lengths.length; i++) {
			formats[i] = FORMAT_1 + lengths[i] + S + (i + 1 == lengths.length ? LINE_BREAK : "");
		}
		// format 'em
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pGrid.length; i++) {
			for (int j = 0; j < pGrid[i].length; j++) {
				sb.append(COLUMN_SEP);
				sb.append(String.format(formats[j], pGrid[i][j]));
			}
		}

		return sb.toString();
	}

	public void fillWithActions(String[][] pGrid, Map<Integer, State> pInitialStates, Plan pPlan) {
		for (State state : pInitialStates.values()) {
			for (Action action : pPlan) {
				getActionSymbol(state, action, pGrid);
			}
		}
	}

	public void fillWithActions(String[][] pGrid, Policy pPolicy) {
		for (State state : pPolicy.getStates()) {
			Action action = pPolicy.get(state);
			getActionSymbol(state, action, pGrid);
		}
	}

	private String[][] addIndexes(String[][] pGrid, int pRows, int pCols) {
		String[][] grid2 = new String[pRows + 1][pCols + 1];
		grid2[0][0] = SPACE;

		for (int j = 1; j < pCols + 1; j++) {
			grid2[0][j] = j - 1 + "";
		}

		for (int i = 1; i < pRows + 1; i++) {
			grid2[i][0] = i - 1 + "";
			System.arraycopy(pGrid[i - 1], 0, grid2[i], 1, pCols);
		}

		return grid2;
	}

	private void addPropositions(ERG pModel, Collection<State> pModelStates, String[][] pGrid) {
		for (State state : pModelStates) {
			Set<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(state);
			if (props != null) {
				StringBuilder sb = new StringBuilder();

				for (Proposition proposition : props) {
					if (proposition != null) {
						String str = proposition.getName();
						// truncate
						str = str.length() < MAX_STR_SIZE ? str : str.substring(0, MAX_STR_SIZE);
						sb.append(str);
						sb.append(SPACE);
					}
				}

				int row = GridUtils.getRow(state);
				int col = GridUtils.getCol(state);
				pGrid[row][col] = sb.toString().trim();// list.toTable().replace("[",
														// "").replace("]", "");
			}
		}
	}

	private <M extends MDP & Grid> String[][] createGrid(M pModel, String[][] pValues) {
		String[][] grid = new String[pModel.getRows()][pModel.getCols()];

		for (State state : pModel.getStates()) {
			int row = GridUtils.getRow(state);
			int col = GridUtils.getCol(state);
			grid[row][col] = pValues[row][col];
		}

		return grid;
	}

	private <M extends MDP & Grid> String[][] createGrid(M pModel) {
		String[][] grid = new String[pModel.getRows()][pModel.getCols()];

		for (State state : pModel.getStates()) {
			int row = GridUtils.getRow(state);
			int col = GridUtils.getCol(state);
			grid[row][col] = SPACE;
		}

		return grid;
	}

	public String toTable(Map<State, Double> map, int pRows, int pCols) {
		String[][] grid = new String[pRows][pCols];

		for (final State state : map.keySet()) {
			int row = GridUtils.getRow(state);
			int col = GridUtils.getCol(state);
			Double value = map.get(state);
			grid[row][col] = String.format(VALUE_FORMAT_STATE, value);
		}

		final String[][] grid2 = new String[pRows + 1][pCols + 1];
		grid2[0][0] = SPACE;

		for (int j = 1; j < pCols + 1; j++) {
			grid2[0][j] = j - 1 + "";
		}

		for (int i = 1; i < pRows + 1; i++) {
			grid2[i][0] = i - 1 + "";
			System.arraycopy(grid[i - 1], 0, grid2[i], 1, pCols);
		}

		return toTable(grid2);
	}

	public String print(RewardFunction rf, MDP mdp) {
		int i = 0;
		final String[][] grid = new String[mdp.getStates().size() + 1][mdp.getActions().size() + 1];
		grid[0][0] = SPACE;

		for (State state : mdp.getStates()) {
			int j = 0;
			i++;
			grid[i][0] = state.getName();

			for (Action action : mdp.getActions()) {
				j++;
				grid[0][j] = action.getName();
				grid[i][j] = rf.getValue(state, action) + "";//maxReward(state, action, rf, mdp.getStates()) + "";
			}
		}

		return toTable(grid);
	}

//	private Double maxReward(State state, Action action, RewardFunction rf, Collection<State> states) {
//		double max = 0;
//		for (State state2 : states) {
//			Double rew = rf.getValue(state, state2, action);
//			if (rew != null && rew > max) {
//				max = rew;
//			}
//		}
//
//		return max;
//	}

	public String print(TransitionFunction tf, MDP mdp) {
		int i = 0;
		final String[][] grid = new String[mdp.getStates().size() + 1][mdp.getActions().size() + 1];
		grid[0][0] = SPACE;

		for (State state : mdp.getStates()) {
			int j = 0;
			i++;
			grid[i][0] = state.getName();

			for (Action action : mdp.getActions()) {
				j++;
				grid[0][j] = action.getName();
				State best = tf.getNextState(mdp.getStates(), state, action);
				String value = SPACE;

				if (best != null) {
					value = String.format(VALUE_FORMAT, tf.getValue(state, best, action));
					value = best.getName() + SPACE + value;
				}

				grid[i][j] = value;
			}
		}

		return toTable(grid);
	}

	public String print(PropositionFunction pf, MDP mdp) {
		int i = 0;
		final String[][] grid = new String[mdp.getStates().size() + 1][mdp.getActions().size() + 1];
		grid[0][0] = SPACE;

		for (State state : mdp.getStates()) {
			int j = 0;
			i++;
			grid[i][0] = state.getName();

			for (Action action : mdp.getActions()) {
				j++;
				grid[0][j] = action.getName();
				grid[i][j] = pf.getExpressionForState(state).toString();
			}
		}

		return toTable(grid);
	}

	private void getActionSymbol(State state, Action action, String[][] pGrid) {
		int row = GridUtils.getRow(state) + 1;
		int col = GridUtils.getCol(state) + 1;
		getActionSymbol(action, pGrid, row, col);
	}

	private void getActionSymbol(Action action, String[][] pGrid, int row, int col) {
		if (action != null) {
			if (action.equals(north)) {
				pGrid[row][col] = pGrid[row][col] + NORTH_SYMBOL;
			} else if (action.equals(south)) {
				pGrid[row][col] = pGrid[row][col] + SOUTH_SYMBOL;
			} else if (action.equals(west)) {
				pGrid[row][col] = pGrid[row][col] + WEST_SYMBOL;
			} else if (action.equals(east)) {
				pGrid[row][col] = pGrid[row][col] + EAST_SYMBOL;
			}
		}
	}

	public <M extends MDP & Grid> String printTable(M pModel, String[][] pValues) {
		String[][] grid = createGrid(pModel, pValues);
		grid = addIndexes(grid, pModel.getRows(), pModel.getCols());
		return toTable(grid);
	}
}
