package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.Combinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class CombinePropsRewardChooser implements Chooser<Proposition> {

    private final double badRewardThreshold;
    private final Combinator<Proposition> combinator;

    public CombinePropsRewardChooser(Combinator<Proposition> pCombinator, double pBadRewardThreshold) {
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
