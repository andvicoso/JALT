package org.emast.model.algorithm.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.actionchooser.NonBlockedActionChooser;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearningStopBadExpression;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

/**
 * 4 - QLearning + PPFERG + bloqueando a pior expressão de cada vez (com iteração): - Parar o QLearning quando
 * achar uma expressão com recompensa abaixo do limiar e que não seja conhecida. E depois que achar todas as
 * expressões ruins, quando parar? - Como já foi bloqueado (removeu a transição) diretamente após encontrar a
 * expressão ruim, sempre terá um caminho obrigatório até o objetivo final no PPFERG.
 */
public class ERGQLearningController4 extends AbstractERGQLearningController {

    private final Set<Expression> avoid = new HashSet<Expression>();
    private final Map<State, Action> blocked = new HashMap<State, Action>();

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        int iteration = 0;
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();
        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
        Expression badExp;
        Policy policy;
        //start main loop
        do {
            Log.info("\nITERATION " + iteration++ + ":\n");
            //1. RUN QLEARNING UNTIL A LOW REWARD EXPRESSION IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = ((ERGQLearningStopBadExpression) learning).getBadExpression();
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (isValid(badExp)) {
                Log.info("\nFound bad expression: " + badExp);

                avoid.add(badExp);
                Log.info("\nAvoid: " + avoid);

                populateBlocked(q);
            }
        } while (isValid(badExp));

        if (!avoid.isEmpty()) {
            //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
            model = createModel(model, q, avoid);
            //create problem
            p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
            //5. RUN PPFERG FOR THE NEW MODEL
            final PPFERG ppferg = new PPFERG();
            //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
            policy = ppferg.run(p);
        } else {
            policy = learning.getQTable().getPolicy();
        }
        return policy;
    }

    @Override
    protected QLearning createQLearning(ERGQTable q) {
        QLearning erg = new ERGQLearningStopBadExpression(q, BAD_EXP_VALUE, avoid);
        erg.setActionChooser(new NonBlockedActionChooser(blocked));
        return erg;
    }

    private void populateBlocked(ERGQTable q) {
        //mark as blocked all visited states that contains one of the "avoid" expressions
        for (Action action : q.getActions()) {
            for (State state : q.getStates()) {
                Expression exp = q.get(state, action).getExpression();
                if (avoid.contains(exp)) {
                    blocked.put(state, action);
                }
            }
        }
    }

    private boolean isValid(Expression exp) {
        return exp != null && !exp.isEmpty();
    }
}
