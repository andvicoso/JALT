package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import static org.emast.util.DefaultTestProperties.*;

/**
 * QLearning + PPFERG + baixando o valor de todas as expressões de uma vez
 */
public class ERGLearningLowAllBadExps extends AbstractERGLearning {

    public ERGLearningLowAllBadExps(AbstractRLearning<ERG> learning) {
        super(learning);
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        ThresholdChooser<Expression> badChooser = new ThresholdChooser<Expression>(BAD_EXP_VALUE, true);
        Set<Expression> avoid = new HashSet<Expression>();
        Problem<ERG> p = pProblem;
        ERG model = p.getModel();

        ERGQTable q = new ERGQTable(model.getStates(), model.getActions());
        //1. RUN QLEARNING UNTIL A HIGH ERROR IS FOUND (QUICK STOP LEARNING) 
        learning.run(p, q);
        //2. GET BAD EXPRESSIONS FROM QLEARNING ITERATIONS
        Set<Expression> badExps = badChooser.choose(q.getExpsValues());
        //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
        // WHICH HAVE THE FOUND EXPRESSIONS
        if (!badExps.isEmpty()) {
            avoid.addAll(badExps);
            Log.info("\nAvoid: " + avoid);

            updateQTable(model, q, avoid);
            Log.info("\nQTable: \n" + q.toString());
        }
        //4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
        model = createModel(model, q, avoid);
        p = new Problem<ERG>(model, p.getInitialStates(), p.getFinalStates());
        //5. RUN PPFERG FOR THE NEW MODEL
        final PPFERG ppferg = new PPFERG();
        //6. GET THE FINAL POLICY FROM PPFERG EXECUTED OVER THE NEW MODEL
        return ppferg.run(p);
    }
}
