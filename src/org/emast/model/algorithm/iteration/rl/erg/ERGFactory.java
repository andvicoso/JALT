package org.emast.model.algorithm.iteration.rl.erg;

import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.action.Action;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.chooser.MinValueChooser;
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

    public static ERG create(ERG model, ERGQLearning q,
            PropositionFunction pf, Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        return create(model, q.getQTable(), pf, props, preservGoal, finalGoal);
    }

    public static ERG create(ERG model, ERGQTable qt, PropositionFunction pf,
            Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        Map<Expression, Double> expsValues = qt.getExpsValues();
        if (!expsValues.isEmpty()) {
            RewardFunction rf = model.getRewardFunction();//createRewardFunction(qt);
            TransitionFunction tf = createTransitionFunctionFrequency(qt);
            Expression newPreservGoal = createPresevationGoal(model, expsValues, null);

            if (newPreservGoal != null) {
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

    public static TransitionFunction createTransitionFunctionFrequency(final ERGQTable q) {
        TransitionFunction tf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                ERGQTableItem item = q.get(pState, pAction);
                State fstate = item.getFinalState();
                if (State.isValid(pFinalState, fstate)) {
                    double total = q.getTotal(pState);
                    return total != 0 ? item.getFrequency() / total : 0;
                }
                return 0d;
            }
        };

        return tf;
    }

    private static RewardFunction createRewardFunction(final ERGQTable q) {
        return new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                return q.get(pState, pAction).getReward();
            }
        };
    }

    private static boolean existValidFinalState(PropositionFunction pf, Expression newPreservGoal,
            Iterable<State> finalStates) {
        try {


            for (State state : finalStates) {
                if (pf.satisfies(state, newPreservGoal)) {
                    return true;
                }
            }
        } catch (InvalidExpressionException ex) {
        }

        return false;
    }

    public static Expression createPreservationGoal(Map<Expression, Double> expsValues, Expression preservGoal) {
        Set<Expression> exps = getBadExpressions(expsValues);
        Expression newPreservGoal = new PreservationGoalFactory().createPreservationGoalExp(preservGoal, exps);

        return newPreservGoal;
    }

    public static Expression createPresevationGoal(ERG model, Map<Expression, Double> expsValues, Set<Expression> avoid) {
        //PropositionFunction pf = model.getPropositionFunction();
        //Expression pg = model.getPreservationGoal();
        Expression finalNewPg = null;

        try {
            //Collection<State> finalStates = pf.intension(model.getStates(), model.getPropositions(), model.getGoal());
            //createPreservationGoal(expsValues, pg);

            while (true) {
                Expression exp = getBadExpressions(expsValues).iterator().next();
                if (!avoid.contains(exp)) {
                    finalNewPg = exp;
                    break;
                } else if (expsValues.isEmpty()) {
                    break;
                }
                expsValues.remove(exp);
            }

            // if (canChangePreservGoal(pf, pg, newPg, finalStates)) {
            //finalNewPg = newPg;
//                Log.info("Changed preservation goal from {"+ pg + "} to {" + finalNewPg + "}");
            //  }
        } catch (Exception ex) {
        }

        return finalNewPg;
    }

    public static boolean canChangePreservGoal(PropositionFunction pf,
            Expression preservGoal, Expression newPreservGoal, Iterable<State> finalStates) {

        //compare previous goal with the newly created
        return !newPreservGoal.equals(preservGoal)
                && !preservGoal.contains(newPreservGoal)
                && !preservGoal.contains(newPreservGoal.negate())
                && existValidFinalState(pf, newPreservGoal, finalStates);
    }

    public static Set<Expression> getBadExpressions(Map<Expression, Double> expsValues) {
        MultiChooser<Expression> chooser = new MinValueChooser<Expression>();
        return chooser.choose(expsValues);
    }
}
