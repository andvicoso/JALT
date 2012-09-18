package org.emast.model;

import java.util.Collection;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface BadRewarder {

    Collection<Proposition> getBadRewardProps();

    Collection<BadReward> getBadRewards();

    void setBadRewards(Collection<BadReward> pBadRewards);

    void setOtherwiseValue(double pOtherwiseValue);

    double getOtherwiseValue();
}
