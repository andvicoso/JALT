package org.emast.erg.city;

import java.util.Set;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class CityModel extends ERGGridModel {

    public CityModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols);
        setAgents(pAgents);
        //create propositions 
        String[] propss = {"hole", "wall", "semaphore", "exit", "bridge"};
        Set<org.emast.model.propositional.Proposition> props = CollectionsUtils.createSet(Proposition.class, propss);
        setPropositions(props);
        //set expressions
        setGoal(new Expression("exit"));
        setPreservationGoal(new Expression("!semaphore & !bridge & !hole & !wall"));
    }
}
