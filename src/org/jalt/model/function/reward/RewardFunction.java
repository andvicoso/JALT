package org.jalt.model.function.reward;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public interface RewardFunction {

	public double getValue(final State pState, final Action pAction);

}
