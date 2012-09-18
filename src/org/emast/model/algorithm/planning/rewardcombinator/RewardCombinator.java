package org.emast.model.algorithm.planning.rewardcombinator;

import java.util.Collection;
import java.util.Map;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface RewardCombinator {

    Map<Proposition, Double> combine(final Collection<Map<Proposition, Double>> pReputations);
}
