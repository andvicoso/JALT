package org.emast.model.agent.behaviour.reward;

import java.util.Map;
import org.emast.model.model.MDP;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface PropRewardBehaviour<M extends MDP>
        extends RewardBehaviour<M, Map<Proposition, Double>> {
}
