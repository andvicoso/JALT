package org.emast.mdp;

import java.util.HashMap;
import java.util.Map;
import org.emast.model.algorithm.reinforcement.ValueIterationAlgorithm;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.reward.RewardFunctionState;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.problem.Problem;
import org.emast.model.state.State;
import org.emast.model.test.Test;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public class GridTest extends Test {

    public GridTest() {
        super(createProblem(), new ValueIterationAlgorithm());
    }

    private static RewardFunction createRewardFunction(MDP model) {
        final Map<State, Double> map = new HashMap<State, Double>();
        map.put(GridUtils.createGridState(0, 2), 1d);
        map.put(GridUtils.createGridState(1, 2), -1d);

        return new RewardFunctionState(model, map, -0.04d);
    }

    private static Map<Integer, State> createInitialStates() {
        Map<Integer, State> map = new HashMap<Integer, State>();
        map.put(0, GridUtils.createGridState(2, 1));

        return map;
    }

    private static Problem createProblem() {
        return new Problem(createModel(), createInitialStates());
    }

    private static MDP createModel() {
        GridModel model = new GridModel(3, 3);
        model.setRewardFunction(createRewardFunction(model));

        return model;
    }

    public static void main(String[] args) {
        new GridTest().run();
    }
}
