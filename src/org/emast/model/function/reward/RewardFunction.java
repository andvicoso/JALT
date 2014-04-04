package org.emast.model.function.reward;

import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 * 
 * @author Anderson
 */
public interface RewardFunction {

	public double getValue(final State pState, final Action pAction);

}
