package org.emast.model.test.mdp.ai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.function.reward.RewardFunctionState;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.state.GridState;
import org.emast.model.state.State;
import org.emast.util.grid.GridUtils;

public class ModelProblemIntroVI extends GridModel {

	public ModelProblemIntroVI() {
		super(3, 4);

		Map<State, Double> rew = new HashMap<>();
		rew.put(new GridState(0, 3), 1d);
		rew.put(new GridState(1, 3), -1d);
		setActions(Arrays.asList(GridUtils.north, GridUtils.east, GridUtils.west));
		setTransitionFunction(new TransitionFunction2());
		setRewardFunction(new RewardFunctionState<MDP>(this, rew, 0));
	}

	class TransitionFunction2 extends TransitionFunction {

		@Override
		public double getValue(State pState, State pFinalState, Action pAction) {
			if (!pState.equals(pFinalState) && pState instanceof GridState
					&& (pFinalState instanceof GridState || pFinalState.equals(State.ANY))) {

//				if (f.equals(new GridState(1, 1)))
//					return 0;
//				else if (s.equals(new GridState(0, 3)))
//					return 0;
//				else if (s.isNeighbour(f)) {
//					if (pAction.equals(GridUtils.north) && s.getRow() > f.getRow())
//						return 0.8;
//					else if (pAction.equals(GridUtils.west) && s.getCol() > f.getCol())
//						return 0.1;
//					else if (pAction.equals(GridUtils.east) && s.getCol() < f.getCol())
//						return 0.1;
//				}
			}
			return 0;
		}
	}
}
