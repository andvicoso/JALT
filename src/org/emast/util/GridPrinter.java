package org.emast.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class GridPrinter {

    private static final int MAX_STR_SIZE = 3;

    public <M extends MDP & Grid> String print(final M pModel, final boolean pAddIndexes) {
        String[][] grid = getGrid(pModel, null, pAddIndexes);
        return toTable(grid);
    }

    public <M extends MDP & Grid> String print(final Problem<M> pProblem, final Policy pPolicy,
            final boolean pAddIndexes) {
        String[][] grid = getGrid(pProblem, pAddIndexes);
        grid = fillWithActions(pProblem, grid, pPolicy, pAddIndexes);
        return toTable(grid);
    }

    private <M extends MDP & Grid> String[][] getGrid(final M pModel, Map<Integer, State> pInitialStates,
            final boolean pAddIndexes) {
        String[][] grid = createGrid(pModel);

        if (pModel instanceof ERG) {
            addPropositions((ERG) pModel, pModel.getStates(), grid);
        }

        if (pInitialStates != null && !pInitialStates.isEmpty()) {
            addInitialStates(pInitialStates, grid);
        }

        if (pAddIndexes) {
            grid = addIndexes(grid, pModel.getRows(), pModel.getCols());
        }

        return grid;
    }

    private <M extends MDP & Grid> String[][] getGrid(final Problem<M> pProblem, final boolean pAddIndexes) {
        final M model = pProblem.getModel();

        return getGrid(model, pProblem.getInitialStates(), pAddIndexes);
    }

    private void addInitialStates(Map<Integer, State> initialStates, String[][] grid) {
        int agent = 0;
        for (final State initState : initialStates.values()) {
            final int row = GridModel.getRow(initState);
            final int col = GridModel.getCol(initState);
            grid[row][col] = agent + " " + grid[row][col];
            agent++;
        }
    }

    public String toTable(final String[][] pGrid) {
        // Find out what the maximum number of columns is in any row
        int maxColumns = 0;
        for (int i = 0; i < pGrid.length; i++) {
            maxColumns = Math.max(pGrid[i].length, maxColumns);
        }
        // Find the maximum length of a string in each column
        final int[] lengths = new int[maxColumns];
        for (int i = 0; i < pGrid.length; i++) {
            for (int j = 0; j < pGrid[i].length; j++) {
                Object object = pGrid[i][j];
                String str = object == null ? " " : object.toString();
                pGrid[i][j] = str;
                lengths[j] = Math.max(str.length(), lengths[j]);
            }
        }
        // Generate a format string for each column
        final String[] formats = new String[lengths.length];
        for (int i = 0; i < lengths.length; i++) {
            formats[i] = "%1$" + lengths[i] + "s"
                    + (i + 1 == lengths.length ? "|\n" : "");
        }
        // format 'em
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pGrid.length; i++) {
            for (int j = 0; j < pGrid[i].length; j++) {
                sb.append("|");
                sb.append(String.format(formats[j], pGrid[i][j].toString()));
            }
        }

        return sb.toString();
    }

    public <M extends MDP & Grid> String[][] fillWithActions(final Problem<M> pProblem, final String[][] pGrid,
            final Policy pPolicy, final boolean pAddIndexes) {

        for (final State state : pPolicy.getStates()) {
            final Action action = pPolicy.get(state);
            int row = GridModel.getRow(state);
            int col = GridModel.getCol(state);
            row = pAddIndexes ? row + 1 : row;
            col = pAddIndexes ? col + 1 : col;

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

        return pGrid;
    }

    private String[][] addIndexes(String[][] grid, int rows, int cols) {
        final String[][] grid2 = new String[rows + 1][cols + 1];
        grid2[0][0] = " ";

        for (int j = 1; j < cols + 1; j++) {
            grid2[0][j] = j - 1 + "";
        }

        for (int i = 1; i < rows + 1; i++) {
            grid2[i][0] = i - 1 + "";
            System.arraycopy(grid[i - 1], 0, grid2[i], 1, cols);
        }

        return grid2;
    }

    private void addPropositions(ERG pModel, Collection<State> pStates, String[][] grid) {
        for (State state : pStates) {
            final int row = GridModel.getRow(state);
            final int col = GridModel.getCol(state);
            final Set<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(state);
            if (props != null) {
                final StringBuilder sb = new StringBuilder();

                for (final Proposition proposition : props) {
                    if (proposition != null) {
                        String str = proposition.getName();
                        //truncate
                        str = str.length() < MAX_STR_SIZE
                                ? str : str.substring(0, MAX_STR_SIZE);
                        sb.append(str);
                        sb.append(" ");
                    }
                }

                grid[row][col] = sb.toString().trim();//list.toTable().replace("[", "").replace("]", "");
            }
        }
    }

    private <M extends MDP & Grid> String[][] createGrid(M pModel) {
        String[][] grid = new String[pModel.getRows()][pModel.getCols()];

        for (final State state : pModel.getStates()) {
            final int row = GridModel.getRow(state);
            final int col = GridModel.getCol(state);
            grid[row][col] = " ";
        }

        return grid;
    }
}
