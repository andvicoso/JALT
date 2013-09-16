package org.emast.model.algorithm.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.actionchooser.NonBlockedActionChooser;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.chooser.NotInChooser;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.stoppingcriteria.StopOnBadExpression;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SinglePolicy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;

/**
 * Learning + PPFERG + bloqueando a pior expressão de cada vez (com iteração)
 */
public class ERGLearningBlockEachBadExp_ppferg_in extends AbstractERGLearning {

    private final Set<Expression> avoid = new HashSet<Expression>();
    private final Map<State, Action> blocked = new HashMap<State, Action>();
    private final NotInChooser<Expression> expFinder =
            new NotInChooser<Expression>(new ThresholdChooser<Expression>(BAD_EXP_VALUE, true), avoid);

    public ERGLearningBlockEachBadExp_ppferg_in(AbstractRLearning<ERG> learning) {
        super(learning);
        learning.setStoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE, avoid));
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        avoid.clear();
        avoid.add(pProblem.getModel().getPreservationGoal().negate());

        int iteration = 0;
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();
        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
        Expression badExp;
        Policy policy;
        //start main loop
        do {
            iteration++;
            //Log.info("\nITERATION " + iteration + ":");
            //1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND
            policy = learning.run(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = expFinder.chooseOne(q.getExpsValues());
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (isValid(badExp)) {
                Log.info("Found bad expression: " + badExp);

                avoid.add(badExp);
                //Log.info("Avoid: " + avoid);

                //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
                model = createModel(model, q, avoid);
                //create problem
                p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());

                //5. CREATE PPFERG ALGORITHM
                final PPFERG ppferg = new PPFERG();
                //6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW MODEL
                policy = ppferg.run(p);
            }
        } while (isValid(badExp));

        return policy;
    }

    private void populateBlocked(ERGQTable q) {
        //mark as blocked all visited states that contains one of the "avoid" expressions
        for (Action action : q.getActions()) {
            for (State state : q.getStates()) {
                Expression exp = q.get(state, action).getExpression();
                if (avoid.contains(exp)) {
                    blocked.put(state, action);
                    //Log.info("Blocked state:" + state + " and action: " + action);
                }
            }
        }
    }

    private boolean isValid(Expression exp) {
        return exp != null && !exp.isEmpty();
    }

    private SinglePolicy optmize(Policy policy, ERGQTable q) {
        SinglePolicy single = new SinglePolicy();
        for (Map.Entry<State, Map<Action, Double>> entry : policy.entrySet()) {
            State state = entry.getKey();
            Action bestAction = getBestAction(entry.getValue(), q.getDoubleValues(state));
            single.put(state, bestAction);
        }

        return single;
    }

    private Action getBestAction(Map<Action, Double> policy, Map<Action, Double> q) {
        Collection<Action> best = getBestAction(policy, policy.keySet());
        if (policy.size() > 1) {
            Collection<Action> bestq = getBestAction(q, policy.keySet());
            if (bestq.size() > 1) {
                best = bestq;
            }
        }

        return best.iterator().next();
    }

    private Collection<Action> getBestAction(Map<Action, Double> map, Set<Action> keySet) {
        Map<Action, Double> temp = new HashMap<Action, Double>(map);
        for (Action action : map.keySet()) {
            if (!keySet.contains(action)) {
                temp.remove(action);
            }
        }

        Double max = Collections.max(temp.values());
        return CollectionsUtils.getKeysForValue(temp, max);
    }
}
