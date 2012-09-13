package org.emast.model.function;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public abstract class TransitionFunction {

    public abstract double getValue(final State pState, final State pFinalState,
            final Action pActions);

    public Map<State, Double> getReachableStatesValues(final Collection<State> pModelStates,
            final State pState, final Action pAction) {
        final Map<State, Double> map = new HashMap<State, Double>();
        for (final State state : pModelStates) {
            final Double value = getValue(pState, state, pAction);
            if (value != null && value > 0) {
                map.put(state, value);
            }
        }
        return map;
    }

    public Map<State, Double> getStatesValuesThatReach(final Collection<State> pModelStates,
            final State pState, final Action pActions) {
        final Map<State, Double> map = new HashMap<State, Double>();
        for (final State state : pModelStates) {
            final Double value = getValue(state, pState, pActions);
            if (value > 0) {
                map.put(state, value);
            }
        }
        return map;
    }

    public Set<State> getReachableStates(final Collection<State> pModelStates,
            final State pState, final Action pActions) {
        return getReachableStatesValues(pModelStates, pState, pActions).keySet();
    }

    public Collection<State> getFinalStates(final Collection<State> pModelStates,
            final State pState, final Action pActions) {
        Map<State, Double> stsv = getReachableStatesValues(pModelStates, pState, pActions);
        if (stsv != null && !stsv.isEmpty()) {
            Double max = Collections.max(stsv.values());
            return CollectionsUtils.getKeysForValue(stsv, max);
        }
        return null;
    }

    public Collection<Action> getActionsFrom(final Collection<Action> pModelActions, final State pState) {
        final Collection<Action> list = new HashSet<Action>();

        for (Action action : pModelActions) {
            double value = getValue(pState, State.ANY, action);
            if (value > 0) {
                list.add(action);
            }
        }

        return list;
    }
}
