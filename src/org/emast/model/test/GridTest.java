package org.emast.model.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.reward.RewardFunctionState;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.problem.Problem;
import org.emast.model.state.State;
import org.emast.model.test.Test;
import org.emast.util.grid.GridUtils;

/**
 *
 * @author Anderson
 */
public class GridTest extends Test {

    public GridTest(Problem pProblem) {
        super(pProblem);
    }

    private static RewardFunction createRewardFunction(MDP model) {
        final Map<State, Double> map = new HashMap<State, Double>();
        map.put(GridUtils.createGridState(0, 0), -1d);
        map.put(GridUtils.createGridState(2, 2), 1d);

        return new RewardFunctionState(model, map, 0.0d);
    }

    private static Map<Integer, State> createInitialStates() {
        Map<Integer, State> map = new HashMap<Integer, State>();
        map.put(0, GridUtils.createGridState(0, 0));

        return map;
    }

    private static Set<State> createFinalStates() {
        Set<State> set = new HashSet<State>();
        set.add(GridUtils.createGridState(2, 2));

        return set;
    }

    public static Problem createProblem() {
        return new Problem(createModel(), createInitialStates(), createFinalStates());
    }

    private static MDP createModel() {
        GridModel model = new GridModel(3, 3);
        model.setRewardFunction(createRewardFunction(model));

        return model;
    }
}