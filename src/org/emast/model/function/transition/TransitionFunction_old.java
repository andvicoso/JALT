package org.emast.model.function.transition;

import java.io.Serializable;
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
public abstract class TransitionFunction_old implements Serializable {

    public abstract double getValue(final State pState, final State pFinalState,
            final Action pAction);

    public Map<Action, Double> getActionValues(final Collection<Action> pModelActions,
            final State pState) {
        Map<Action, Double> map = new HashMap<Action, Double>();
        Collection<Action> possibleActions = getActionsFrom(pModelActions, pState);

        for (final Action action : possibleActions) {
            final Double value = getValue(pState, State.ANY, action);
            if (value != null && value > 0) {
                map.put(action, value);
            }
        }
        return map;
    }

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
            final State pState, final Action pAction) {
        return getReachableStatesValues(pModelStates, pState, pAction).keySet();
    }

    public Action getAction(final Collection<Action> pModelActions,
            final State pState) {
        Map<Action, Double> actionsValues = getActionValues(pModelActions, pState);
        return CollectionsUtils.draw(actionsValues);
    }

    public State getNextState(final Collection<Action> pModelActions, final Collection<State> pModelStates,
            final State pState) {
        Action action = getAction(pModelActions, pState);
        return action != null ? getBestReachableState(pModelStates, pState, action) : null;
    }

    public State getBestReachableState(final Collection<State> pModelStates,
            final State pState, final Action pAction) {
        State ret = null;
        Map<State, Double> map = getReachableStatesValues(pModelStates, pState, pAction);
        if (map != null && !map.isEmpty()) {
            Double max = Collections.max(map.values());
            ret = CollectionsUtils.getKeysForValue(map, max).iterator().next();//CollectionsUtils.getRandom(ret);
        }
        return ret;
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
