package org.emast.model.algorithm.actionchooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class NonBlockedActionChooser implements ActionChooser {

    private final Map<State, Action> blocked;
    private final ActionChooser delegate;

    public NonBlockedActionChooser(Map<State, Action> blocked) {
        this.blocked = blocked;
        this.delegate = new RandomActionChooser();
    }

    @Override
    public Action choose(Map<Action, Double> values, State state) {
        Action action = null;
        Collection<Action> valid = new HashSet<Action>();

        for (Action act : values.keySet()) {
            if (values.get(act) > 0) {
                valid.add(act);
            }
        }

        if (!valid.isEmpty() && !blocked.keySet().containsAll(valid)) {
            do {
                action = delegate.choose(values, state);
            } while (blocked.containsKey(state) && blocked.get(state).equals(action));
        }

        return action;
    }
}
