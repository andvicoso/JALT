package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.planning.rewardcombinator.RewardCombinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class CombinatorBadRewardPropsChooser implements PropositionsChooser {

    private final double badRewardThreshold;
    private final RewardCombinator combinator;

    public CombinatorBadRewardPropsChooser(RewardCombinator pCombinator, double pBadRewardThreshold) {
        combinator = pCombinator;
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<Proposition, Double> combined = combinator.combine(pReps);
        //get "bad" propositions
        final Set<Proposition> set = new HashSet<Proposition>();
        for (Proposition prop : combined.keySet()) {
            if (combined.get(prop) <= badRewardThreshold) {
                set.add(prop);
            }
        }
        return set;
    }
}
