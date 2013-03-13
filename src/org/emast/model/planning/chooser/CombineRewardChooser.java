package org.emast.model.planning.chooser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.Combinator;

/**
 *
 * @author Anderson
 */
public class CombineRewardChooser<T> implements Chooser<T> {

    private final double badRewardThreshold;
    private final Combinator<T> combinator;

    public CombineRewardChooser(Combinator<T> pCombinator, double pBadRewardThreshold) {
        combinator = pCombinator;
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public Set<T> choose(Collection<Map<T, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<T, Double> combined = combinator.combine(pReps);
        //get "bad" propositions
        final Set<T> set = new HashSet<T>();
        for (T prop : combined.keySet()) {
            if (combined.get(prop) <= badRewardThreshold) {
                set.add(prop);
            }
        }
        return set;
    }
}
