package org.emast.model.agent.behaviour.individual.reward;

import java.util.Map;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface PropRewardBehaviour
        extends RewardBehaviour<ERG, Map<Proposition, Double>> {
}
