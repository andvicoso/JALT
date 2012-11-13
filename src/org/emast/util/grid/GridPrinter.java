package org.emast.util.grid;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class GridPrinter {

    private static final int MAX_STR_SIZE = 3;

    public <M extends MDP & Grid> String print(M pModel) {
        String[][] grid = getGrid(pModel, Collections.EMPTY_MAP);

        return toTable(grid);
    }

    public <M extends MDP & Grid> String print(M pModel, Map<Integer, State> pInitialStates, Object pResult) {
        String[][] grid = getGrid(pModel, pInitialStates);

        if (pResult != null && pResult instanceof Policy) {
            fillWithActions(grid, (Policy) pResult);
        }

        return toTable(grid);
    }

    private <M extends MDP & Grid> String[][] getGrid(M pModel, Map<Integer, State> pInitialStates) {
        String[][] grid = createGrid(pModel);

        if (pModel instanceof ERG) {
            addPropositions((ERG) pModel, pModel.getStates(), grid);
        }

        addInitialStates(pInitialStates, grid);

        grid = addIndexes(grid, pModel.getRows(), pModel.getCols());

        return grid;
    }

    private void addInitialStates(Map<Integer, State> pInitialStates, String[][] pGrid) {
        int agent = 0;
        for (State initState : pInitialStates.values()) {
            int row = GridUtils.getRow(initState);
            int col = GridUtils.getCol(initState);
            pGrid[row][col] = agent + " " + pGrid[row][col];
            agent++;
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
                Object object = pGrid[i][j];
                String str = object == null ? " " : object.toString();
                pGrid[i][j] = str;
                lengths[j] = Math.max(str.length(), lengths[j]);
            }
        }
        // Generate a format string for each column
        String[] formats = new String[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            formats[i] = "%1$" + lengths[i] + "s"
                    + (i + 1 == lengths.length ? "|\n" : "");
        }
        // format 'em
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pGrid.length; i++) {
            for (int j = 0; j < pGrid[i].length; j++) {
                sb.append("|");
                sb.append(String.format(formats[j], pGrid[i][j].toString()));
            }
        }

        return sb.toString();
    }

    public void fillWithActions(String[][] pGrid, Policy pPolicy) {
        for (State state : pPolicy.getStates()) {
            Action action = pPolicy.getBest(state);
            int row = GridUtils.getRow(state) + 1;
            int col = GridUtils.getCol(state) + 1;

            if (action.getName().equals("north")) {
                pGrid[row][col] = pGrid[row][col] + " ^";
            } else if (action.getName().equals("south")) {
                pGrid[row][col] = pGrid[row][col] + " v";
            } else if (action.getName().equals("west")) {
                pGrid[row][col] = pGrid[row][col] + " <";
            } else if (action.getName().equals("east")) {
                pGrid[row][col] = pGrid[row][col] + " >";
            }
        }
    }

    private String[][] addIndexes(String[][] pGrid, int pRows, int pCols) {
        String[][] grid2 = new String[pRows + 1][pCols + 1];
        grid2[0][0] = " ";

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
                        //truncate
                        str = str.length() < MAX_STR_SIZE
                                ? str : str.substring(0, MAX_STR_SIZE);
                        sb.append(str);
                        sb.append(" ");
                    }
                }

                int row = GridUtils.getRow(state);
                int col = GridUtils.getCol(state);
                pGrid[row][col] = sb.toString().trim();//list.toTable().replace("[", "").replace("]", "");
            }
        }
    }

    private <M extends MDP & Grid> String[][] createGrid(M pModel) {
        String[][] grid = new String[pModel.getRows()][pModel.getCols()];

        for (State state : pModel.getStates()) {
            int row = GridUtils.getRow(state);
            int col = GridUtils.getCol(state);
            grid[row][col] = " ";
        }

        return grid;
    }

    public String toTable(Map<State, Double> map, int pRows, int pCols) {
        String[][] grid = new String[pRows][pCols];

        for (final State state : map.keySet()) {
            int row = GridUtils.getRow(state);
            int col = GridUtils.getCol(state);
            Double value = map.get(state);
            grid[row][col] = String.format("%1$.4f", value);
        }

        final String[][] grid2 = new String[pRows + 1][pCols + 1];
        grid2[0][0] = " ";

        for (int j = 1; j < pCols + 1; j++) {
            grid2[0][j] = j - 1 + "";
        }

        for (int i = 1; i < pRows + 1; i++) {
            grid2[i][0] = i - 1 + "";
            System.arraycopy(grid[i - 1], 0, grid2[i], 1, pCols);
        }

        return toTable(grid2);
    }
}
