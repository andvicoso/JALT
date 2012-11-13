package org.emast.util;

import java.util.Random;
import org.emast.model.action.Action;
import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class PolicyUtils {

    private PolicyUtils() {
    }

    /**
     * Create random policy
     *
     * @param pModel
     * @return
     */
    public static Policy createRandom(final MDP pModel) {
        final Action[] actions = pModel.getActions().toArray(new Action[0]);
        final Random rand = new Random();
        final Policy policy = new Policy();

        for (final State state : pModel.getStates()) {
            final int randPosition = rand.nextInt(pModel.getActions().size());
            Action action = actions[randPosition];
            policy.put(state, action, 0d);
        }

        return policy;
    }
}
