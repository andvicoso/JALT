package org.emast.model.test.mdp.ai;

import java.util.HashMap;
import java.util.Map;

import org.emast.model.function.reward.RewardFunctionState;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.state.GridState;
import org.emast.model.state.State;

public class ModelProblem9_26 extends GridModel {

	public ModelProblem9_26() {
		super(3, 3);

		Map<State, Double> rew = new HashMap<>();
		rew.put(new GridState(2, 0), -1d);
		rew.put(new GridState(2, 1), -1d);
		rew.put(new GridState(2, 2), -1d);
		rew.put(new GridState(1, 1), 10d);

		setRewardFunction(new RewardFunctionState<MDP>(this, rew, 0));
	}
}
