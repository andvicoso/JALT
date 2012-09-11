package org.emast.util;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;

/**
 *
 * @author anderson
 */
public class ModelUtils {

    private ModelUtils() {
    }

    public static Set<State> getStates(final Collection<Transition> pPi) {
        final Set<State> list = new HashSet<State>();
        for (final Transition trans : pPi) {
            list.add(trans.getState());
        }

        return list;
    }

    public static Set<Action> getActions(final Collection<Transition> pPi) {
        final Set<Action> result = new HashSet<Action>();
        for (final Transition trans : pPi) {
            result.add(trans.getAction());
        }

        return result;
    }
}
