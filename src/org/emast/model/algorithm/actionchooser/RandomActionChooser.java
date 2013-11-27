package org.emast.model.algorithm.actionchooser;

import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * 
 * @author Anderson
 */
public class RandomActionChooser implements ActionChooser {

	@Override
	public Action choose(Map<Action, Double> pActionsValues, State pState) {
		return CollectionsUtils.draw(pActionsValues);
	}
}
