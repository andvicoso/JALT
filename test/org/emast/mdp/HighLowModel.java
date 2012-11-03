package org.emast.mdp;

import java.util.Arrays;
import org.emast.model.action.Action;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
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

        final RewardFunction rf = new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                return 0.0;
            }
        };
    }
}
