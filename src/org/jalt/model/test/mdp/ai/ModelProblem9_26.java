package org.jalt.model.test.mdp.ai;

import java.util.HashMap;
import java.util.Map;

import org.jalt.model.function.reward.RewardFunctionState;
import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;

public class ModelProblem9_26 extends GridModel {

	public ModelProblem9_26() {
		super(3, 3);

		Map<State, Double> rew = new HashMap<State, Double>();
		rew.put(new GridState(2, 0), -1d);
		rew.put(new GridState(2, 1), -1d);
		rew.put(new GridState(2, 2), -1d);
		rew.put(new GridState(1, 1), 10d);

		setRewardFunction(new RewardFunctionState<MDP>(this, rew, 0));
	}
}
