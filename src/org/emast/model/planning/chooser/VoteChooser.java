package org.emast.model.planning.chooser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;

/**
 *
 * @author Anderson
 */
public class VoteChooser<T> implements Chooser<T> {

    @Override
    public Set<T> choose(Collection<Map<T, Double>> pReps) {
        return getVotes(pReps).keySet();
    }

    protected Map<T, Integer> getVotes(Collection<Map<T, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<T, Integer> map = new HashMap<T, Integer>();
        for (Map<T, Double> rep : pReps) {
            for (T prop : rep.keySet()) {
                int count = map.containsKey(prop) ? map.get(prop) : 0;
                count++;
                map.put(prop, count);
            }
        }
        return map;
    }
}
