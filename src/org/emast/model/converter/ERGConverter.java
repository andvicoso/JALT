package org.emast.model.converter;

import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;

/**
 *
 * @author Anderson
 */
public class ERGConverter {

//    public Problem<ERGGridModel> convert(final Problem<MDPModel> pProblem,
//            final Set<Proposition> pPropositions, final PropositionFunction pPropositionFunction,
//            final Expression pGoal, final Expression pPreservGoal)
//            throws EvaluationException {
//        final MDPModel pModel = pProblem.getModel();
//        final ERGGridModel model = new ERGGridModel();
//        model.setActions(pModel.getActions());
//        model.setStates(pModel.getStates());
//        model.setPropositions(pPropositions);
//        model.setPropositionFunction(pPropositionFunction);
//        model.setGoal(pGoal);
//        model.setPreservationGoal(pPreservGoal);
//
//        final Problem<ERGGridModel> problem = new Problem<ERGGridModel>(model, pProblem.getInitialStates());
//        problem.setError(pProblem.getError());
//
//        return problem;
//    }
}
