package org.emast.util.erg;

import java.util.Map;
import java.util.Set;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.chooser.Chooser;
import org.emast.model.chooser.MinValueChooser;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.ModelUtils;

/**
 *
 * @author andvicoso
 */
public class ERGFactory {

    public static ERG create(ERG model, QLearning<ERG> q) {
        return create(model, q, model.getPropositionFunction(),
                model.getPropositions(), model.getPreservationGoal(), model.getGoal());
    }

    public static ERG create(ERG model, QLearning<ERG> q,
            PropositionFunction pf, Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        return create(model, (ERGQTable) q.getQTable(), pf, props, preservGoal, finalGoal);
    }

    public static ERG create(ERG model, ERGQTable qt, PropositionFunction pf,
            Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        Map<Expression, Double> expsValues = qt.getExpsValues();
        if (!expsValues.isEmpty()) {
            RewardFunction rf = model.getRewardFunction();//createRewardFunction(qt);
            TransitionFunction tf = ModelUtils.createTransitionFunctionFrequency(qt);
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
        Chooser<Expression> chooser = new MinValueChooser<Expression>();
        return chooser.choose(expsValues);
    }

    public static PropositionFunction createPropositionFunction(final ERGQTable q) {
        PropositionFunction pf = new PropositionFunction();

        for (State state : q.getStates()) {
            for (Action action : q.getActions()) {
                ERGQTableItem item = q.get(state, action);
                if (item != null && item.getExpression() != null) {
                    State fState = item.getFinalState();
                    Set<Proposition> props = item.getExpression().getPropositions();
                    pf.add(fState, props);
                }
            }
        }

        return pf;
    }
}