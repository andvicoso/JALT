package org.emast.model.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.GridTransitionFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.Grid;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.GridModel;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.grid.GridUtils;

/**
 *
 * @author Anderson
 */
public class ToRL {

    public static Problem<MDP> convert(Problem<ERG> pProblem) {
        ERG erg = pProblem.getModel();
        MDP mdp;

        if (erg instanceof Grid) {
            final Grid grid = (Grid) erg;
            mdp = new GridModel(grid.getRows(), grid.getCols());
        } else {
            mdp = new MDPModel();
            mdp.setAgents(erg.getAgents());
            mdp.setStates(erg.getStates());
            mdp.setTransitionFunction(erg.getTransitionFunction());
        }

        mdp.setActions(erg.getActions());
        mdp.setRewardFunction(erg.getRewardFunction());

        Problem<MDP> p = null;
        try {
            mdp.setTransitionFunction(convertTransitionFunction(erg, mdp));

            Set<State> finalGoalStates = getStatesThatSatisfies(erg, erg.getGoal());
            finalGoalStates.addAll(pProblem.getFinalStates());
            
            p = new Problem<MDP>(mdp, pProblem.getInitialStates(), finalGoalStates);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return p;
    }

    public static RewardFunction convertRewardFunction(final ERG pErg,
            final double pBadReward, Collection<Proposition> pBadRewardStates) {
        final TransitionFunction tf = pErg.getTransitionFunction();
        final PropositionFunction pf = pErg.getPropositionFunction();
        final RewardFunction rf = pErg.getRewardFunction();
        final Set<State> allObstacles = new HashSet<State>();

        for (Proposition obstacle : pBadRewardStates) {
            Set<State> obsStates = pf.getStatesWithProposition(obstacle);
            allObstacles.addAll(obsStates);
        }

        final RewardFunction nrf = new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                Set<State> reachable = tf.getReachableStates(pErg.getStates(), pState, pAction);
                boolean canReachObstacle = !Collections.disjoint(reachable, allObstacles);
                return canReachObstacle
                        ? pBadReward
                        : rf.getValue(pState, pAction);
            }
        };

        return nrf;
    }

    private static TransitionFunction convertTransitionFunction(final ERG erg, MDP mdp)
            throws InvalidExpressionException {
        final TransitionFunction tf = mdp.getTransitionFunction();
        //get preserved states
        final Collection<State> preservationStates =
                getStatesThatSatisfies(erg, erg.getPreservationGoal());
        //get final states
        final Collection<State> finalStates =
                getStatesThatSatisfies(erg, erg.getGoal());


        final TransitionFunction ntf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                //remove states leaving from final or preserved/blocked states
                if (finalStates.contains(pState)
                        || preservationStates.contains(pFinalState)
                        || preservationStates.contains(pState)) {
                    return 0;
                }

                if (!preservationStates.isEmpty()) {
                    //verify if all reachable states from action and states are preserved/blocked
                    if (pFinalState.equals(State.ANY)) {
                        Collection<State> reachable = tf.getReachableStates(erg.getStates(), pState, pAction);
                        if (!Collections.disjoint(reachable, preservationStates)) {
                            return 0.0;
                        }
                    }
                    //if it is a grid, remove probabilities that reach preserved/blocked states
                    if (tf instanceof GridTransitionFunction) {
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
                }

                return tf.getValue(pState, pFinalState, pAction);
            }
        };

        return ntf;
    }

    private static Set<State> getStatesThatSatisfies(ERG pErg, Expression pExp) throws InvalidExpressionException {
        return pExp.isEmpty()
                ? Collections.EMPTY_SET
                : pErg.getPropositionFunction().intension(pErg.getStates(), pErg.getPropositions(), pExp);
    }
}
