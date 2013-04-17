package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import static org.emast.util.DefaultTestProperties.*;

/**
 * 2 - QLearning + PPFERG + pegando a pior expressão de cada vez (com iteração): - Parece estranho ter que
 * pegar uma expressão de cada vez, quando já temos mais de uma abaixo do limiar. - Parar o QLearning após um
 * número fixo de iterações (difícil de definir um número ideal) - E depois que achar todas as expressões
 * ruins, quando parar?
 */
public class ERGQLearningController2 extends AbstractERGQLearningController {

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        ThresholdChooser<Expression> badChooser =
                new ThresholdChooser<Expression>(BAD_EXP_VALUE, true);
        Set<Expression> avoid = new HashSet<Expression>();
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();
        int iterations = 0;
        Expression badExp;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");
            //create new q table for each itraction
            ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
            //1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING) 
            runQLearning(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = choose(q, badChooser, avoid);
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (badExp != null) {
                avoid.add(badExp);
                System.out.println("Avoid: " + avoid);
                // update q to lower q values for states that contains one badexp
                updateQ(model, q, avoid);
                System.out.println("QTable: \n" + q.toString());
            }
            //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
            model = createModel(model, q, avoid);
            p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
        } while (badExp != null);
        //5. RUN PPFERG FOR THE NEW MODEL
        final PPFERG ppferg = new PPFERG();
        //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
        return ppferg.run(p);
    }

    private Expression choose(ERGQTable q, ThresholdChooser<Expression> badChooser, Set<Expression> avoid) {
        Set<Expression> chosen = badChooser.choose(q.getExpsValues());

        for (Expression exp : chosen) {
            if (!avoid.contains(exp)) {
                return exp;
            }
        }

        return null;
    }
}
