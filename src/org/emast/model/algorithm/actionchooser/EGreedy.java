package org.emast.model.algorithm.actionchooser;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;
/**
 *
 * @author Anderson
 */
public class EGreedy implements ActionChooser {

    private double epsilon = EPSILON;
    private Random rand = new Random();

    public EGreedy() {
    }

    public EGreedy(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public Action choose(Map<Action, Double> pActionsValues, State state) {
        Action action;
        double rnd = rand.nextDouble();
        if (rnd < epsilon) {
            //select random
            action = CollectionsUtils.draw(pActionsValues);
        } else {
            //select max action
            double max = Collections.max(pActionsValues.values());
            Set<Action> maxActions = CollectionsUtils.getKeysForValue(pActionsValues, max);
            action = CollectionsUtils.getRandom(maxActions);
        }

        return action;
    }
}
