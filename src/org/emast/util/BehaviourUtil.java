package org.emast.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.emast.model.agent.behaviour.individual.reward.PropRewardBehaviour;
import org.emast.model.planning.rewardcombinator.RewardCombinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class BehaviourUtil {

    public static Map<Proposition, Double> getPropositionsRewards(RewardCombinator pRewardCombinator,
            List<PropRewardBehaviour> pBehaviours) {
        Collection<Map<Proposition, Double>> list = new ArrayList<Map<Proposition, Double>>();

        for (PropRewardBehaviour beh : pBehaviours) {
            Map<Proposition, Double> map = (Map<Proposition, Double>) beh.getResult();
            list.add(map);
        }

        return pRewardCombinator.combine(list);
    }
}
