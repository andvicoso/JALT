package org.emast.model.algorithm.actionchooser;

import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public interface ActionChooser {

    Action choose(Map<Action, Double> pActionsValues, State state);
}
