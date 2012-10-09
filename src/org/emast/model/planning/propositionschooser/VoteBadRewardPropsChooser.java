package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class VoteBadRewardPropsChooser implements PropositionsChooser {

    private final double badRewardThreshold;
    private Integer voteThreshold;

    public VoteBadRewardPropsChooser(double pBadRewardThreshold) {
        badRewardThreshold = pBadRewardThreshold;
    }

    public VoteBadRewardPropsChooser(double pBadRewardThreshold, int pVoteThreshold) {
        badRewardThreshold = pBadRewardThreshold;
        voteThreshold = pVoteThreshold;
    }

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        Map<Proposition, Integer> map = new HashMap<Proposition, Integer>();
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

        if (voteThreshold == null) {
            return Collections.singleton(map.keySet().iterator().next());
        }

        Set<Proposition> props = new HashSet<Proposition>();
        for (Proposition prop : map.keySet()) {
            if (map.get(prop) > voteThreshold) {
                props.add(prop);
            }
        }

        return props;
    }
}
