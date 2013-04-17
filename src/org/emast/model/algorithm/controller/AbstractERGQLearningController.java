package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public abstract class AbstractERGQLearningController extends DefaultAlgorithm<ERG, Policy> {

    protected ERGQLearning learning;

    @Override
    public String printResults() {
        return learning.printResults();
    }

    private void findBestPlan(Problem<ERG> pProblem) {
        Log.info("\nValue Iteration");
        //ERG model = pProblem.getModel();
        ValueIteration vi = new ValueIteration();
        initTime();
        Policy pi = vi.run(pProblem, (Object) null);
        endTime();
//        State st = pProblem.getInitialStates().get(0);
//        double sum = 0;
//        do {
//            Action a = pi.getBestAction(st);
//            sum += model.getRewardFunction().getValue(st, a);
//            st = model.getTransitionFunction().getBestReachableState(model.getStates(), st, a);
//        } while (st != null && !pProblem.getFinalStates().contains(st));

        Log.info("\nIterations: " + vi.getIterations());
        //Log.info("Best plan reward value: " + sum);
        Log.info("Best policy: " + pProblem.toString(pi));
    }

    protected void runQLearning(Problem<ERG> p, ERGQTable q) {
        //create q learning algorithm with high error
        learning = createERGQLearning(q);
        //really run
        initTime();
        learning.run(p);
        endTime();

        Log.info("ERGQLearning frequency table: ");
        Log.info("\n" + new GridPrinter().printTable((ERGGridModel) p.getModel(),
                learning.getQTable().getFrequencyTableModel()));

        //print policy found by qlearning
//        Policy policy_ql = learning.getQTable().getPolicy();
//        Log.info("ERGQLearning final policy: " + p.toString(policy_ql));
    }

    protected ERGQTable updateQ(ERG model, ERGQTable q, Set<Expression> avoid) {
        for (State state : model.getStates()) {
            for (Action action : model.getActions()) {
                ERGQTableItem item = q.get(state, action);
                Expression exp = item.getExpression();
                double value = 0;
                try {
                    if (exp != null && !exp.getPropositions().isEmpty()) {
                        if (matchExpression(exp, avoid)) {
                            value = BAD_Q_VALUE;
                        }
                    }
                } catch (Exception e) {
                }

                q.put(state, action, newItem(item, value));
            }
        }

        return q;
    }

    private ERGQTableItem newItem(ERGQTableItem item, double value) {
        ERGQTableItem nitem = new ERGQTableItem(item);
        nitem.setValue(value);
        return nitem;
    }

    private boolean matchExpression(Expression stateExp, Set<Expression> exps)
            throws InvalidExpressionException {
        for (Expression exp : exps) {
            if (exp.equals(stateExp)) {
                return true;
            }
        }

        return false;
    }

    protected ERG createModel(ERG oldModel, ERGQTable q, Set<Expression> avoid) {
        ERGModel model = new ERGModel();
        //COPY MAIN PROPERTIES
        model.setStates(q.getStates());
        model.setActions(q.getActions());
        model.setGoal(oldModel.getGoal());
        model.setAgents(oldModel.getAgents());
        //GET THE SET OF PROPOSITIONS FROM EXPLORATED STATES
        model.setPropositions(getPropositions(q.getExpsValues()));
        //CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
        Expression badExp = new Expression(BinaryOperator.OR, avoid.toArray(new Expression[avoid.size()]));
        model.setPreservationGoal(badExp.parenthesize().negate());
        //CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        TransitionFunction tf = ERGFactory.createTransitionFunctionFrequency(q);
        model.setTransitionFunction(tf);
        //CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        PropositionFunction pf = ERGFactory.createPropositionFunction(q);
        model.setPropositionFunction(pf);
        //CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        RewardFunction rf = ERGFactory.createRewardFunction(q);
        model.setRewardFunction(rf);

        Log.info("\nTransition Function\n" + new GridPrinter().print(tf, model));

        return model;
    }

    private Set<Proposition> getPropositions(Map<Expression, Double> expsValues) {
        Set<Proposition> props = new HashSet<Proposition>();
        for (Expression exp : expsValues.keySet()) {
            Set<Proposition> expProps = exp.getPropositions();
            props.addAll(expProps);
        }

        return props;
    }

    protected ERGQLearning createERGQLearning(ERGQTable q) {
        return new ERGQLearning(q);
    }
}
