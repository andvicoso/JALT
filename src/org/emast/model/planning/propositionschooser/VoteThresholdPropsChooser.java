package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class VoteThresholdPropsChooser extends VotePropsChooser {

    private final double badRewardThreshold;

    public VoteThresholdPropsChooser(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    protected Map<Proposition, Integer> getVotes(Collection<Map<Proposition, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<Proposition, Integer> map = new HashMap<Proposition, Integer>();
        for (Map<Proposition, Double> rep : pReps) {
            for (Proposition prop : rep.keySet()) {
                if (rep.get(prop) <= badRewardThreshold) {
                    int count = map.containsKey(prop) ? map.get(prop) : 0;
                    count++;
                    map.put(prop, count);
                }
            }
        }
        return map;
    }
}
