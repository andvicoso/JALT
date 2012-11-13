package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class VotePropsChooser implements Chooser<Proposition> {

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        return getVotes(pReps).keySet();
    }

    protected Map<Proposition, Integer> getVotes(Collection<Map<Proposition, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<Proposition, Integer> map = new HashMap<Proposition, Integer>();
        for (Map<Proposition, Double> rep : pReps) {
            for (Proposition prop : rep.keySet()) {
                int count = map.containsKey(prop) ? map.get(prop) : 0;
                count++;
                map.put(prop, count);
            }
        }
        return map;
    }
}
