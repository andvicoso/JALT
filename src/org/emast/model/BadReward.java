package org.emast.model;

import java.io.Serializable;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class BadReward implements Serializable{

    private Proposition prop;
    private double badReward;

    public BadReward() {
    }

    public BadReward(Proposition prop, double badReward) {
        this.prop = prop;
        this.badReward = badReward;
    }

    public double getBadReward() {
        return badReward;
    }

    public void setBadReward(double badReward) {
        this.badReward = badReward;
    }

    public Proposition getBadRewardProp() {
        return prop;
    }

    public void setBadRewardProp(Proposition badRewardProp) {
        this.prop = badRewardProp;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(prop).append("=").append(badReward);

        return sb.toString();
    }
}
