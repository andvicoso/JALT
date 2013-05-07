package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.impl.ERGModel;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public abstract class AbstractERGQLearningController extends DefaultAlgorithm<ERG, Policy> {

    protected QLearning learning;

    @Override
    public String printResults() {
        return learning.printResults();
    }

    protected void runQLearning(Problem<ERG> p, ERGQTable q) {
        //create q learning algorithm with high error
        learning = createQLearning(q);
        //really run
        initTime();
        learning.run(p);
        endTime();

//        Log.info("ERGQLearning frequency table: ");
//        Log.info("\n" + new GridPrinter().printTable((ERGGridModel) p.getModel(),
//                learning.getQTable().getFrequencyTableModel()));
    }

    protected ERGQTable updateQTable(ERG model, ERGQTable q, Set<Expression> avoid) {
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
        model.setPreservationGoal(createNewPreservationGoal(avoid));
        //CREATE NEW TRANSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        TransitionFunction tf = ERGFactory.createTransitionFunctionFrequency(q);
        model.setTransitionFunction(tf);
        //CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        PropositionFunction pf = ERGFactory.createPropositionFunction(q);
        model.setPropositionFunction(pf);
        //CREATE NEW REWARD FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
        RewardFunction rf = ERGFactory.createRewardFunction(q);
        model.setRewardFunction(rf);

        //Log.info("\nTransition Function\n" + new GridPrinter().print(tf, model));

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

    protected QLearning createQLearning(ERGQTable q) {
        return new QLearning(q);
    }

    private Expression createNewPreservationGoal(Set<Expression> avoid) {
        Expression badExp = new Expression(BinaryOperator.OR, avoid.toArray(new Expression[avoid.size()]));
        return badExp.parenthesize().negate();
    }
}
