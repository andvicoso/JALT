package org.emast.model.algorithm.actionchooser;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * The agent always choose the action with the highest Q-value
 * 
 * @author Anderson
 * 
 */
public class Greedy implements ActionChooser {

	public Greedy() {
	}

	@Override
	public Action choose(Map<Action, Double> pActionsValues, State state) {
		// select max action
		double max = Collections.max(pActionsValues.values());
		Set<Action> maxActions = CollectionsUtils.getKeysForValue(pActionsValues, max);

		return CollectionsUtils.getRandom(maxActions);
	}
}
