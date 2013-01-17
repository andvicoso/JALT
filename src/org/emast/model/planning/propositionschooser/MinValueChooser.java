package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.Combinator;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class MinValueChooser implements Chooser<Proposition> {

    private final Combinator<Proposition> combinator;

    public MinValueChooser(Combinator<Proposition> pCombinator) {
        combinator = pCombinator;
    }

    @Override
    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        Proposition ret = null;
        //combine values for propositions from collection
        Map<Proposition, Double> map = combinator.combine(pReps);
        Double min = Collections.min(map.values());

        for (Map.Entry<Proposition, Double> entry : map.entrySet()) {
            Proposition prop = entry.getKey();
            Double d = entry.getValue();

            if (min.equals(d)) {
                ret = prop;
                break;
            }
        }
        return Collections.singleton(ret);
    }
}
