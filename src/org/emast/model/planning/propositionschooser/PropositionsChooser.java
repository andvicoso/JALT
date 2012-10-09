package org.emast.model.planning.propositionschooser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public interface PropositionsChooser {

    Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps);
}
