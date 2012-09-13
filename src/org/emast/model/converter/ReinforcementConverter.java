package org.emast.model.converter;

import java.util.Collection;
import java.util.List;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.RewardFunction;
import org.emast.model.function.TransitionFunction;
import org.emast.model.model.impl.ERGGridModel;
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

//    public Problem<MDPModel> convert(final Problem<? extends ERGGridModel> pProblem,
//            double pGoodReward, double pBadReward, final Proposition... pBadRewardPropositions) {
//        final MDPModel model = new MDPModel(pProblem.getModel());
//
//        final Problem<MDPModel> problem = new Problem<MDPModel>(model, pProblem.getInitialStates());
//        problem.setError(pProblem.getError());
//
//        try {
//            convertTransitionFunction(problem, pProblem);
//        } catch (EvaluationException ex) {
//            ex.printStackTrace();
//        }
//
//        try {
//            convertRewardFunction(problem, pProblem, pGoodReward, pBadReward, pBadRewardPropositions);
//        } catch (EvaluationException ex) {
//            ex.printStackTrace();
//        }
//
//        return problem;
//    }
//
//    private void convertRewardFunction(final Problem<? extends MDPModel> pMdp, final Problem<? extends ERGGridModel> pErg,
//            double pGoodReward, double pBadReward, final Proposition[] pBadRewardStates)
//            throws EvaluationException {
//        PropositionFunction pf = pErg.getModel().getPropositionFunction();
//        TransitionFunction tf = pErg.getModel().getTransitionFunction();
//        RewardFunction rf = pMdp.getModel().getRewardFunction();
//        Collection<State> finalGoalStates = getStatesThatSatisfies(pErg, pErg.getGoal());
//
//        for (Proposition obstacle : pBadRewardStates) {
//            List<State> obsStates = pf.getStatesWithProposition(obstacle);
//            //bad reward for states that reach obstacles
//            setRewards(obsStates, rf, tf, pBadReward);
//        }
//        //good reward for states that reach final goal
//        setRewards(finalGoalStates, rf, tf, pGoodReward);
//    }
//
//    private void convertTransitionFunction(final Problem<? extends MDPModel> pMdp, final Problem<? extends ERGGridModel> pErg)
//            throws EvaluationException {
//        Collection<State> blockingStates = getStatesThatSatisfies(pErg, pErg.getPreservationGoal().negate());
//        //get final states
//        Collection<State> noTransitionStates = getStatesThatSatisfies(pErg, pErg.getGoal());
//        //add preserved states
//        noTransitionStates.addAll(blockingStates);
//
//        TransitionFunction tf = pMdp.getModel().getTransitionFunction();
//        //remove transitions from final and preserved goals
//        for (State state : noTransitionStates) {
//            List<TransitionFunctionItem> toRemove = tf.getTransitionsFrom(state);
//            tf.getTable().removeAll(toRemove);
//        }
//
//        //remove transitions that reaches blocking states
//        for (State state : blockingStates) {
//            List<TransitionFunctionItem> toRemove = tf.getTransitionsThatReach(state);
//            tf.getTable().removeAll(toRemove);
//        }
//    }
//
//    private Collection<State> getStatesThatSatisfies(Problem<? extends ERGGridModel> pErg, Expression pExp) throws EvaluationException {
//        return pErg.getModel().getPropositionFunction().intension(pExp, pErg.getModel().getPropositions());
//    }
//
//    private void setRewards(Collection<State> finalGoals, RewardFunction rf, TransitionFunction tf, double reward) {
//        for (State state : finalGoals) {
//            List<TransitionFunctionItem> trans = tf.getTransitionsThatReach(state);
//
//            for (TransitionFunctionItem t : trans) {
//                rf.add(new RewardFunctionItem(reward, t.getState(), t.getActions()));//State.ANY
//            }
//        }
//    }
}
