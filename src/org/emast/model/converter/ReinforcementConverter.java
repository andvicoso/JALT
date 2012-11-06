package org.emast.model.converter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.GridTransitionFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public class ReinforcementConverter {

    public MDP convert(ERG erg, double pGoodReward, double pBadReward,
            Proposition... pBadRewardPropositions) {
        return convert(erg, pGoodReward, pBadReward, Arrays.asList(pBadRewardPropositions));
    }

    public MDP convert(ERG erg, double pGoodReward, double pBadReward,
            Collection<Proposition> pBadRewardPropositions) {
        final MDPModel mdp = (MDPModel) erg.copy();

        try {
            convertTransitionFunction(mdp, erg);
            convertRewardFunction(mdp, erg, pGoodReward, pBadReward, pBadRewardPropositions);
        } catch (Exception ex) {
            Log.error(ex.getMessage());
        }

        return mdp;
    }

    private void convertRewardFunction(final MDPModel pMdp, final ERG pErg,
            final double pGoodReward, final double pBadReward, final Collection<Proposition> pBadRewardStates)
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
                TransitionFunction tf = pMdp.getTransitionFunction();
                Set<State> reachable = tf.getReachableStates(pMdp.getStates(), pState, pAction);

                if (!Collections.disjoint(reachable, finalGoalStates)) {
                    return pGoodReward;
                } else if (!Collections.disjoint(reachable, allObstacles)) {
                    return pBadReward;
                } else {
                    return rf.getValue(pState, pAction);
                }
            }
        };

        pMdp.setRewardFunction(nrf);
    }

    private void convertTransitionFunction(final MDPModel pMdp, final ERG pErg)
            throws InvalidExpressionException {
        //get preserved states
        final Collection<State> preservationStates =
                getStatesThatSatisfies(pErg, pErg.getPreservationGoal().negate());
        //get final states
        final Collection<State> finalStates =
                getStatesThatSatisfies(pErg, pErg.getGoal());

        final TransitionFunction tf = pErg.getTransitionFunction();

        final TransitionFunction ntf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                if (finalStates.contains(pState)
                        || preservationStates.contains(pFinalState)
                        || preservationStates.contains(pState)) {
                    return 0;
                } else if (pFinalState.equals(State.ANY)) {
                    Collection<State> reachable = tf.getReachableStates(pErg.getStates(), pState, pAction);
                    if (!Collections.disjoint(reachable, preservationStates)) {
                        return 0.0;
                    }
                } else if (tf instanceof GridTransitionFunction) {
                    final GridTransitionFunction gtf = (GridTransitionFunction) tf;
                    int row = GridUtils.getRow(pState);
                    int col = GridUtils.getCol(pState);
                    Map<State, Action> t = gtf.getTransitions(row, col);

                    for (State state : preservationStates) {
                        t.remove(state);
                    }
                    for (Map.Entry<State, Action> entry : t.entrySet()) {
                        State state = entry.getKey();
                        Action action = entry.getValue();

                        if (Action.isValid(pAction, action)
                                && State.isValid(state, pFinalState)) {
                            return 1d / t.size();
                        }
                    }
                }
                return tf.getValue(pState, pFinalState, pAction);
            }
        };

        pMdp.setTransitionFunction(ntf);
    }

    private Collection<State> getStatesThatSatisfies(ERG pErg, Expression pExp) throws InvalidExpressionException {
        return pErg.getPropositionFunction().intension(pErg.getStates(), pErg.getPropositions(), pExp);
    }
}
