package org.jalt.model.test.mdp;

import java.util.Arrays;

import org.jalt.model.action.Action;
import org.jalt.model.function.reward.RewardFunction;
import org.jalt.model.function.transition.TransitionFunction;
import org.jalt.model.model.impl.MDPModel;
import org.jalt.model.state.State;

/**
 *
 * @author andvicoso
 */
public class HighLowModel extends MDPModel {

    public HighLowModel() {
        State two = new State("two");
        State three = new State("three");
        State four = new State("four");
        setStates(Arrays.asList(two, three, four));

        Action high = new Action("high");
        Action low = new Action("low");
        setActions(Arrays.asList(high, low));

        final TransitionFunction tf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                return 0.33;
            }
        };

        setTransitionFunction(tf);

        final RewardFunction rf = new RewardFunction() {
            @Override
			public double getValue(State pState, Action pAction) {
                return 0.0;
            }
        };

        setRewardFunction(rf);
    }
}
