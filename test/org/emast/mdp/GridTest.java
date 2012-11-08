package org.emast.mdp;

import java.util.HashMap;
import java.util.Map;
import org.emast.model.algorithm.reinforcement.QLearning;
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
        super(createProblem(), new QLearning());
    }

    private static RewardFunction createRewardFunction(MDP model) {
        final Map<State, Double> map = new HashMap<State, Double>();
        map.put(GridUtils.createGridState(2, 7), 3d);
        map.put(GridUtils.createGridState(4, 3), -5d);
        map.put(GridUtils.createGridState(7, 3), -10d);
        map.put(GridUtils.createGridState(7, 8), 10d);

        return new RewardFunctionState(model, map, 0.0d);
    }

    private static Map<Integer, State> createInitialStates() {
        Map<Integer, State> map = new HashMap<Integer, State>();
        map.put(0, GridUtils.createGridState(2, 1));

        return map;
    }

    public static Problem createProblem() {
        return new Problem(createModel(), createInitialStates());
    }

    private static MDP createModel() {
        GridModel model = new GridModel(10, 10);
        model.setRewardFunction(createRewardFunction(model));

        return model;
    }

    public static void main(String[] args) {
        new GridTest().run();
    }
}
