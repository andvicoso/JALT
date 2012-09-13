package org.emast.model;

import java.util.Collection;

/**
 *
 * @author Anderson
 */
public interface BadRewarder {

    Collection<BadReward> getBadRewards();

    void setBadRewards(Collection<BadReward> pBadRewards);

    void setOtherwiseValue(double pOtherwiseValue);

    double getOtherwiseValue();
}
