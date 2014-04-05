package org.emast.model.test.erg.hunterprey;

import java.util.Set;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author andvicoso
 */
public class HunterPreyModel extends ERGGridModel {

    public HunterPreyModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set props
        String[] propss = {"hole", "wall", "prey"};
        Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propss);
        setPropositions(props);

        setGoal(new Expression("prey"));
        setPreservationGoal(new Expression("!hole & !wall"));
    }
}
