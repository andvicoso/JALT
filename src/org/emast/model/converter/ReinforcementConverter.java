package org.emast.model.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ReinforcementConverter {

    public Problem<MDP> convert(final Problem<? extends ERG> pProblem,
            double pGoodReward, double pBadReward, final Proposition... pBadRewardPropositions) {
        final ERG erg = (ERG) pProblem.getModel();
        final MDPModel mdp = (MDPModel) erg.copy();

        final Problem<MDP> problem = new Problem<MDP>(mdp, pProblem.getInitialStates());
        problem.setError(pProblem.getError());

        try {
            convertTransitionFunction(mdp, erg);
            convertRewardFunction(mdp, erg, pGoodReward, pBadReward, pBadRewardPropositions);
        } catch (Exception ex) {
            Log.error(ex.getMessage());
        }

        return problem;
    }

    private void convertRewardFunction(final MDPModel pMdp, final ERG pErg,
            final double pGoodReward, final double pBadReward, final Proposition[] pBadRewardStates)
            throws EvaluationException, InvalidExpressionException {
        PropositionFunction pf = pErg.getPropositionFunction();
        final RewardFunction rf = pMdp.getRewardFunction();
        final Set<State> allObstacles = new HashSet<State>();
        final Collection<State> finalGoalStates = getStatesThatSatisfies(pErg, pErg.getGoal());

        for (Proposition obstacle : pBadRewardStates) {
            Set<State> obsStates = pf.getStatesWithProposition(obstacle);
            allObstacles.addAll(obsStates);
        }

        final RewardFunction nrf = new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                if (allObstacles.contains(pState)) {
                    return pBadReward;
                } else if (finalGoalStates.contains(pState)) {
                    return pGoodReward;
                } else {
                    return rf.getValue(pState, pAction);
                }
            }
        };

        pMdp.setRewardFunction(nrf);
    }

    private void convertTransitionFunction(final MDPModel pMdp, final ERG pErg) throws InvalidExpressionException {
        //get preserved states
        final Collection<State> blockingStates = getStatesThatSatisfies(pErg, pErg.getPreservationGoal().negate());
        //get final states
        final Collection<State> noTransitionStates = getStatesThatSatisfies(pErg, pErg.getGoal());
        //add preserved states
        noTransitionStates.addAll(blockingStates);

        final TransitionFunction tf = pErg.getTransitionFunction();
        //remove transitions from final and preserved goals
        //remove transitions that reaches blocking states
        final TransitionFunction ntf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pActions) {
                if (noTransitionStates.contains(pState) || blockingStates.contains(pFinalState)) {
                    return 0;
                }
                return tf.getValue(pState, pFinalState, pActions);
            }
        };

        pMdp.setTransitionFunction(ntf);
    }

    private Collection<State> getStatesThatSatisfies(ERG pErg, Expression pExp) throws InvalidExpressionException {
        return pErg.getPropositionFunction().intension(pErg.getStates(), pErg.getPropositions(), pExp);
    }
}
