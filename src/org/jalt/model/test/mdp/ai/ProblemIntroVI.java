package org.jalt.model.test.mdp.ai;

import static org.jalt.util.grid.GridUtils.east;
import static org.jalt.util.grid.GridUtils.north;
import static org.jalt.util.grid.GridUtils.south;
import static org.jalt.util.grid.GridUtils.west;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.reward.RewardFunctionState;
import org.jalt.model.function.transition.GridTransitionFunction;
import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;

public class ProblemIntroVI {

	public static Problem getProblemIntroVI() {
		MDP model = new GridModel();

		Map<State, Double> rew = new HashMap<State, Double>();
		rew.put(new GridState(0, 3), 1d);
		rew.put(new GridState(1, 3), -1d);

		model.setActions(Arrays.asList(south, north, east, west));
		model.setTransitionFunction(new TransitionFunction2());
		model.setRewardFunction(new RewardFunctionState<MDP>(model, rew, 0));

		Set<State> finalStates = new HashSet<>();
		finalStates.add(new GridState(0, 3));
		finalStates.add(new GridState(1, 3));

		return new Problem(model, Collections.singletonMap(0, new GridState(2, 0)), finalStates);
	}

	public static Problem getProblemIntroVI2() {
		MDP model = new GridModel(3, 4);
		model.setActions(Arrays.asList(south, north, east, west));
		model.setTransitionFunction(new TransitionFunction2());
		model.setRewardFunction(new RewardFunction2());

		Set<State> finalStates = new HashSet<>();
		finalStates.add(new GridState(0, 3));
		finalStates.add(new GridState(1, 3));

		return new Problem(model, Collections.singletonMap(0, new GridState(2, 0)), finalStates);
	}

	static class RewardFunction2 implements RewardFunction {
		@Override
		public double getValue(State pState, Action pAction) {
			if (pState.equals(new GridState(0, 3)))
				return 100d;
			else if (pState.equals(new GridState(1, 3)))
				return -100d;
			return -3;
		}
	}

	static class TransitionFunction2 extends GridTransitionFunction {

		public TransitionFunction2() {
			super(3, 4);
		}

		@Override
		public double getValue(State pState, State pFinalState, Action pAction) {
			GridState s = (GridState) pState;
			GridState f = (GridState) pFinalState;

			State next = getNextState(s, pAction);
			return next.equals(f) ? 0.8 : s.isNeighbour(f) && !pAction.equals(south) ? 0.1 : 0;
		}
	}
}
