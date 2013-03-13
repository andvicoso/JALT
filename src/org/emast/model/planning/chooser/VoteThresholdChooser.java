package org.emast.model.planning.chooser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anderson
 */
public class VoteThresholdChooser<T> extends VoteChooser<T> {

    private final double badRewardThreshold;

    public VoteThresholdChooser(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    protected Map<T, Integer> getVotes(Collection<Map<T, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<T, Integer> map = new HashMap<T, Integer>();
        for (Map<T, Double> rep : pReps) {
            for (T prop : rep.keySet()) {
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
