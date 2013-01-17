package org.emast.model.algorithm.iteration.rl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.emast.model.Chooser;
import org.emast.model.Combinator;
import org.emast.model.action.Action;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.planning.propositionschooser.MinValueChooser;
import org.emast.model.planning.rewardcombinator.MeanPropValueCombinator;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGFactory {

    public static ERG create(ERG model, QLearning q) {
        return create(model, q, model.getPropositionFunction(),
                model.getPropositions(), model.getPreservationGoal(), model.getGoal());
    }

    public static ERG create(MDP model, QLearning q,
            PropositionFunction pf, Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        return create(model, q.getQTable(), q.getFrequencyTable(), q.getRewardTable(),
                pf, props, preservGoal, finalGoal);
    }

    public static ERG create(MDP model, QTable qt, FrequencyTable ft, NTable rt,
            PropositionFunction pf, Set<Proposition> props, Expression preservGoal, Expression finalGoal) {
        RewardFunction rf = createRewardFunction(rt);
        TransitionFunction tf = createTransitionFunction(ft);
        Expression newPreservGoal = createPreservationGoal(ft, rt, pf);
        Expression finalPreservGoal = new Expression(BinaryOperator.AND, preservGoal, newPreservGoal);

        ERG erg = new ERGModel();
        erg.setActions(model.getActions());
        erg.setStates(model.getStates());
        erg.setRewardFunction(rf);
        erg.setTransitionFunction(tf);
        erg.setPropositionFunction(pf);
        erg.setPreservationGoal(finalPreservGoal);
        erg.setGoal(finalGoal);
        erg.setPropositions(props);

        return erg;
    }

    private static TransitionFunction createTransitionFunction(final FrequencyTable f) {
        TransitionFunction tf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                double total = f.getTotal(pState);
                return total != 0 ? f.get(pState, pAction) / total : 0;
            }
        };

        return tf;
    }

    private static Expression createPreservationGoal(FrequencyTable ft, NTable rt, PropositionFunction pf) {
        Map<Proposition, Double> propSum = new HashMap<Proposition, Double>();
        Map<Proposition, Integer> propCount = new HashMap<Proposition, Integer>();

        for (State state : ft.getStates()) {
            Set<Proposition> props = pf.getPropositionsForState(state);
            if (props != null && props.size() > 0) {
                for (Action action : ft.getActions()) {
                    double reward = rt.get(state, action);

                    if (reward != 0) {
                        double value = reward / props.size();
                        double sum = 0;
                        int count = 0;

                        for (Proposition p : props) {
                            if (propSum.containsKey(p)) {
                                sum = propSum.get(p);
                            }
                            if (propCount.containsKey(p)) {
                                count = propCount.get(p);
                            }

                            propSum.put(p, sum + value);
                            propCount.put(p, count + 1);
                        }
                    }
                }
            }
        }

        Combinator comb = new MeanPropValueCombinator();
        Chooser chooser = new MinValueChooser(comb);
        Map<Proposition, Double> values = getPropsValues(propSum, propCount);
        Set<Proposition> finalProps = chooser.choose(Collections.singleton(values));

        return new Expression("!water");//new Expression(BinaryOperator.AND, finalProps);
    }

    private static Map<Proposition, Double> getPropsValues(Map<Proposition, Double> propSum, Map<Proposition, Integer> propCount) {
        Map<Proposition, Double> values = new HashMap<Proposition, Double>();

        for (Proposition p : propSum.keySet()) {
            double value = 0;
            Double sum = propSum.get(p);
            Integer count = propCount.get(p);
            if (sum != null && count != null) {
                value = sum / count;
            }
            values.put(p, value);
        }

        return values;
    }

    private static RewardFunction createRewardFunction(final NTable rt) {
        return new RewardFunction() {
            @Override
            public double getValue(State pState, Action pAction) {
                return rt.get(pState, pAction);
            }
        };
    }
}
