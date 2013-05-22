package org.emast.model.algorithm.controller;

import java.util.HashSet;
import java.util.Set;
import org.emast.infra.log.Log;
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
import static org.emast.util.DefaultTestProperties.*;

/**
 * Learning + baixando o valor de q para a pior expressão de cada vez
 */
public class ERGLearningLowEachBadExp extends AbstractERGLearning {

    private final Set<Expression> avoid = new HashSet<Expression>();
    private final NotInChooser<Expression> expFinder =
            new NotInChooser<Expression>(new ThresholdChooser<Expression>(BAD_EXP_VALUE, true), avoid);

    public ERGLearningLowEachBadExp(AbstractRLearning<ERG> learning) {
        super(learning);
        learning.setStoppingCriteria(new StopOnBadExpression(BAD_EXP_VALUE, avoid));
    }

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
            learning.run(p, q);
            //2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
            badExp = expFinder.chooseOne(q.getExpsValues());
            //3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN QLEARNING EXPLORATION
            // WHICH HAVE THE FOUND EXPRESSION
            if (badExp != null) {
                avoid.add(badExp);
                Log.info("Avoid: " + avoid);
                // update q to lower q values for states that contains one badexp
                updateQTable(model, q, avoid);
                //Log.info("\nQTable: \n" + q.toString());
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
}
