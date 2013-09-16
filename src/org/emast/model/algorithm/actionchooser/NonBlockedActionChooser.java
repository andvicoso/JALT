package org.emast.model.algorithm.actionchooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;

/**
 *
 * @author Anderson
 */
public class NonBlockedActionChooser implements ActionChooser {

    private final Set<Transition> blocked;
    private final ActionChooser delegate;

    public NonBlockedActionChooser(Set<Transition> blocked) {
        this.blocked = blocked;
        this.delegate = new RandomActionChooser();
    }

    @Override
    public Action choose(Map<Action, Double> values, State state) {
        Action action = null;
        Collection<Transition> valid = new HashSet<Transition>();

        for (Action act : values.keySet()) {
            if (values.get(act) != 0) {
                valid.add(new Transition(state, act));
            }
        }

        if (!valid.isEmpty() && !blocked.containsAll(valid)) {
            do {
                action = delegate.choose(values, state);
            } while (blocked.contains(new Transition(state, action)));
        }

        return action;
    }
}
