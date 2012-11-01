package org.emast.model.converter;

import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class ERGConverter {

    public Problem<ERG> convert(final Problem<MDP> pProblem,
            final Set<Proposition> pPropositions, final PropositionFunction pPropositionFunction,
            final Expression pGoal, final Expression pPreservGoal)
            throws EvaluationException {
        final MDP pModel = pProblem.getModel();
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

        final Problem<ERG> problem = new Problem<ERG>(model, pProblem.getInitialStates());
        problem.setError(pProblem.getError());

        return problem;
    }
}
