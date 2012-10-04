package org.emast.model.algorithm.planning.propositionschooser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.algorithm.planning.rewardcombinator.RewardCombinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class VoteBadRewardPropsChooser implements PropositionsChooser {

    private final double badRewardThreshold;

    public VoteBadRewardPropsChooser(RewardCombinator pCombinator, double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        //get "bad" propositions
        final Map<Proposition, Integer> map = new HashMap<Proposition, Integer>();
        //combine reputations for propositions from agents
        for (Map<Proposition, Double> rep : pReps) {
            for (Proposition prop : rep.keySet()) {
                if (rep.get(prop) <= badRewardThreshold) {
                    int count = map.containsKey(prop) ? map.get(prop) : 0;
                    count++;
                    map.put(prop, count);
                }
            }
        }
        return map.keySet();
    }
}
