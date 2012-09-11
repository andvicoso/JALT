package org.emast.model;

import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface BadReward {

    Proposition getBadReward();

    double getBadRewardValue();
}
