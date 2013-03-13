package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.Chooser;
import org.emast.model.Combinator;
import org.emast.model.action.Action;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.chooser.MinValueChooser;
import org.emast.model.planning.rewardcombinator.MeanValueCombinator;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGFactory {

    public static ERG create(ERG model, ERGQLearning q) {
        return create(model, q, model.getPropositionFunction(),
                model.getPropositions(), model.getPreservationGoal(), model.getGoal());
    }

    public static ERG create(MDP model, ERGQLearning q,
            PropositionFunction pf, Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        return create(model, q.getQTable(), q.getExpsValues(), pf, props, preservGoal, finalGoal);
    }

    public static ERG create(MDP model, ERGQTable qt, Map<Expression, Double> expsValues, PropositionFunction pf,
            Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        if (!expsValues.isEmpty()) {
            RewardFunction rf = model.getRewardFunction();//createRewardFunction(qt);
            TransitionFunction tf = createTransitionFunctionFrequency(qt);
            Expression newPreservGoal = createPreservationGoal(expsValues, preservGoal);

            if (changePreservGoal(model, pf, props, preservGoal, finalGoal, newPreservGoal)) {
                Log.info("Changed preservation goal from {"
                        + preservGoal + "} to {" + newPreservGoal + "}");

                ERG erg = new ERGModel();
                erg.setActions(model.getActions());
                erg.setStates(model.getStates());
                erg.setRewardFunction(rf);
                erg.setTransitionFunction(tf);
                erg.setPropositionFunction(pf);
                erg.setPreservationGoal(newPreservGoal);
                erg.setGoal(finalGoal);
                erg.setPropositions(props);

                return erg;
            }
        }
        return null;
    }

    private static TransitionFunction createTransitionFunctionFrequency(final ERGQTable q) {
        TransitionFunction tf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                State fstate = q.getFinalState(pState, pAction);
                if (State.isValid(pFinalState, fstate)) {
                    double total = q.getTotalFrequency(pState);
                    return total != 0 ? q.getFrequency(pState, pAction) / total : 0;
                }
                return 0d;
            }
        };

        return tf;
    }

    private static RewardFunction createRewardFunction(final ERGQTable rt) {
        return new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                return rt.getReward(pState, pAction);
            }
        };
    }

    private static Expression createPreservationGoal(Map<Expression, Double> expsValues, Expression preservGoal) {
        Combinator comb = new MeanValueCombinator();
        Chooser chooser = new MinValueChooser(comb);
        Set<Expression> exps = chooser.choose(Collections.singleton(expsValues));
        Expression newPreservGoal = new PreservationGoalFactory().createPreservationGoalExp(preservGoal, exps);

        return newPreservGoal;
    }

    private static boolean existValidFinalState(MDP model, PropositionFunction pf, Set<Proposition> props,
            Expression newPreservGoal, Expression finalGoal) {
        try {
            Collection<State> finalStates = pf.intension(
                    model.getStates(), props, finalGoal);

            for (State state : finalStates) {
                if (pf.satisfies(state, newPreservGoal)) {
                    return true;
                }
            }
        } catch (InvalidExpressionException ex) {
        }

        return false;
    }

    private static boolean changePreservGoal(MDP model, PropositionFunction pf, Set<Proposition> props,
            Expression preservGoal, Expression finalGoal, Expression newPreservGoal) {
        //compare previous goal with the newly created
        return !newPreservGoal.equals(preservGoal)
                && !preservGoal.contains(newPreservGoal)
                && !preservGoal.contains(newPreservGoal.negate())
                && existValidFinalState(model, pf, props, newPreservGoal, finalGoal);
    }
}
