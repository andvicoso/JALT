package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.algorithm.erg.ERGFactory;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.table.erg.ERGQTableItem;
import org.emast.model.model.ERG;
import org.emast.model.function.PropositionFunction;
import org.emast.model.function.reward.RewardFunction;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.impl.ERGModel;
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
public abstract class AbstractERGLearning implements Algorithm<ERG, Policy> {

    protected AbstractRLearning<ERG> learning;

    public AbstractERGLearning(AbstractRLearning<ERG> learning) {
        this.learning = learning;
    }

    public AbstractRLearning<ERG> getLearning() {
        return learning;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nLearning Algorithm: ").append(learning.getClass().getSimpleName());
        sb.append("\nBad exp reward: ").append(BAD_EXP_VALUE);
        sb.append(learning.printResults());

        return sb.toString();
    }

    protected ERGQTable updateQTable(ERG model, ERGQTable q, Set<Expression> avoid) {
        for (State state : model.getStates()) {
            for (Action action : model.getActions()) {
                ERGQTableItem item = q.get(state, action);
                Expression exp = item.getExpression();
                double value = item.getValue();
                if (exp != null && !exp.getPropositions().isEmpty() && matchExpression(exp, avoid)) {
                    value += value * BAD_Q_PERCENT;//BAD_Q_VALUE;
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

    private boolean matchExpression(Expression stateExp, Set<Expression> exps) {
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
        model.setPreservationGoal(createNewPreservationGoal(oldModel.getPreservationGoal(), avoid));
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

    private Expression createNewPreservationGoal(Expression current, Set<Expression> avoid) {
        Expression badExp = new Expression(BinaryOperator.OR, avoid.toArray(new Expression[avoid.size()]));
        return current.and(badExp.parenthesize().negate());
    }

    @Override
    public String getName() {
        return getClass().getSimpleName() + "(" + learning.getName() + ")";
    }
}
