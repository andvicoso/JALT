package org.jalt.model.test.erg.hunterprey;

import java.util.Set;

import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.util.CollectionsUtils;

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
