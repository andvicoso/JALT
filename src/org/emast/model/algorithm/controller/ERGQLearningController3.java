package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearningStopBadExpression;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import static org.emast.util.DefaultTestProperties.*;

/**
 * 3 - QLearning + PPFERG + pegando a pior expressão de cada vez (com iteração): - Parar o QLearning quando
 * achar uma expressão com recompensa abaixo do limiar e que não seja conhecida. - E depois que achar todas as
 * expressões ruins, quando parar? - Por que não bloquear logo após descobrir as expressões ruins ao invés de
 * baixar o Q? - Porque o caminho ainda pode ser bom e não bloqueia o agente para não ficar preso. - Como
 * bloqueia só no PPFERG pode ser que não seja mais possível alcançar o objetivo final dependendo do número de
 * iterações do QLearning.
 */
public class ERGQLearningController3 extends AbstractERGQLearningController {

    private final Set<Expression> avoid = new HashSet<Expression>();

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();
        int episodes = 0;
        Expression badExp;
        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
        //start main loop
        do {
            Log.info("\nEPISODE " + episodes++ + ":\n");
            //1. RUN QLEARNING UNTIL A LOW REWARD EXPRESSION IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = ((ERGQLearningStopBadExpression) learning).getBadExpression();
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (badExp != null) {
                avoid.add(badExp);
                Log.info("\nAvoid: " + avoid);
                // update q to lower q values for states that contains one badexp
                updateQTable(model, q, avoid);
                Log.info("\nQTable: \n" + q.toString());
            }
        } while (badExp != null);
        //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
        model = createModel(model, q, avoid);
        p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
        //5. RUN PPFERG FOR THE NEW MODEL
        final PPFERG ppferg = new PPFERG();
        //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
        return ppferg.run(p);
    }

    @Override
    protected QLearning createQLearning(ERGQTable q) {
        return new ERGQLearningStopBadExpression(q, BAD_EXP_VALUE, avoid);
    }
}
