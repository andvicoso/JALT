package org.emast.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.emast.model.agent.behavior.individual.reward.PropReward;
import org.emast.model.Combinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class BehaviorUtil {

    public static Map<Proposition, Double> getPropositionsRewards(Combinator pRewardCombinator,
            List<PropReward> pBehaviors) {
        Collection<Map<Proposition, Double>> list = new ArrayList<Map<Proposition, Double>>();

        for (PropReward beh : pBehaviors) {
            Map<Proposition, Double> map = (Map<Proposition, Double>) beh.getResult();
            list.add(map);
        }

        return pRewardCombinator.combine(list);
    }
}
