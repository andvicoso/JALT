package org.emast.model.algorithm.actionchooser;

import java.util.HashMap;
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
		Map<Action, Double> valid = new HashMap<Action, Double>();

		for (Action act : values.keySet()) {
			Transition t = new Transition(state, act);
			if (values.get(act) != 0 && !blocked.contains(t)) {
				valid.put(act, values.get(act));
			}
		}

		if (!valid.isEmpty()) {
			action = delegate.choose(valid, state);
		}

		return action;
	}
}
