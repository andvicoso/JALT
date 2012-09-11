package org.emast.model;

import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface BadReward {

    Proposition getBadRewardProp();

    double getBadReward();

    void setBadRewardProp(Proposition pProposition);

    void setBadReward(double pValue);

    double getOtherwiseValue();

    void setOtherwiseValue(double pOtherwiseValue);
}
