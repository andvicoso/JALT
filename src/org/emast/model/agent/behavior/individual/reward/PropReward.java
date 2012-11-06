package org.emast.model.agent.behavior.individual.reward;

import java.util.Map;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface PropReward
        extends RewardBehavior<ERG, Map<Proposition, Double>> {
}
