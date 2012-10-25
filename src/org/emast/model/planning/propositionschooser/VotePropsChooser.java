package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class VotePropsChooser implements PropositionsChooser {

    private final double badRewardThreshold;

    public VotePropsChooser(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        return combine(pReps).keySet();
    }

    @Override
    public Proposition chooseOne(Collection<Map<Proposition, Double>> pReps) {
        Map<Proposition, Integer> map = combine(pReps);
        Integer max = Collections.max(map.values());

        for (Map.Entry<Proposition, Integer> entry : map.entrySet()) {
            Proposition prop = entry.getKey();
            Integer integer = entry.getValue();

            if (max.equals(integer)) {
                return prop;
            }
        }
        //never should get here!
        return map.keySet().iterator().next();
    }

    protected Map<Proposition, Integer> combine(Collection<Map<Proposition, Double>> pReps) {
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