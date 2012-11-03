package org.emast.model.converter;

import java.util.Set;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class ERGConverter {

    public ERG convert(MDP pModel, Set<Proposition> pPropositions,
            PropositionFunction pPropositionFunction, Expression pGoal, Expression pPreservGoal) {
        ERG model;
        if (pModel instanceof Grid) {
            final Grid grid = (Grid) pModel;
            model = new ERGGridModel(grid.getRows(), grid.getCols());
        } else {
            model = new ERGModel();
        }

        model.setAgents(pModel.getAgents());
        model.setActions(pModel.getActions());
        model.setStates(pModel.getStates());
        model.setPropositions(pPropositions);
        model.setPropositionFunction(pPropositionFunction);
        model.setGoal(pGoal);
        model.setPreservationGoal(pPreservGoal);

        return model;
    }
}
