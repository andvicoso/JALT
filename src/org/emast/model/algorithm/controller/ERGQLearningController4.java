package org.emast.model.algorithm.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearningBlockExpression;
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
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();
        int iterations = 0;
        Expression badExp;
        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");
            //1. RUN QLEARNING UNTIL A LOW REWARD EXPRESSION IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = ((ERGQLearningStopBadExpression) learning).getBadExpression();
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (badExp != null) {
                avoid.add(badExp);
                System.out.println("Avoid: " + avoid);

                populateBlocked(q);
            }
        } while (badExp != null);
        //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
        model = createModel(model, q, avoid);
        //create problem
        p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
        //5. RUN PPFERG FOR THE NEW MODEL
        final PPFERG ppferg = new PPFERG();
        //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
        return ppferg.run(p);
    }

    @Override
    protected ERGQLearning createERGQLearning(ERGQTable q) {
        return new ERGQLearningBlockExpression(q, BAD_EXP_VALUE, avoid, blocked);
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
}
