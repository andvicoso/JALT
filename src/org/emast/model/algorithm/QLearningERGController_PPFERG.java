package org.emast.model.algorithm;

import java.util.Collection;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.ValueIteration;
import org.emast.model.algorithm.iteration.rl.erg.ERGFactory;
import org.emast.model.algorithm.iteration.rl.erg.ERGQLearning;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.Utils;
import org.emast.util.grid.GridPrinter;

/**
 *
 * @author Anderson
 */
public class QLearningERGController_PPFERG extends DefaultAlgorithm<ERG, Policy> {

    private static final int MAX_IT = 2;
    private ERGQLearning learning;

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        PPFERG ppferg = new PPFERG();
        int iterations = 0;
        Problem<ERG> p = pProblem;
        ERG model = pProblem.getModel();

        //findBestPlan(pProblem);
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");

            learning = new ERGQLearning();

            long init = System.currentTimeMillis();
            learning.run(p);
            long end = System.currentTimeMillis();
            long diff = end - init;

            Policy policy_ql = learning.getQTable().getPolicy();

            //Log.info("\nQTable: \n" + learning.getQTable());
            //Log.info("\nPolicy QLearning: \n" + policy_ql.toString());
            Log.info("\nQLearning time: " + Utils.toTimeString(diff));
            Log.info("QLearning" + pProblem.toString(policy_ql));
            //Log.info("\n" + new GridPrinter().toTable(learning.getQTable().getFrequencyTableStr()));

            ERG newModel = ERGFactory.create(model, learning);
            //break if model could not be created
            if (newModel != null) {
                //Log.info("\n" + new GridPrinter().print(model.getTransitionFunction(), model));
                Problem newp = new Problem<ERG>(newModel, pProblem.getInitialStates());

                init = System.currentTimeMillis();
                Policy policy_ppferg = ppferg.run(newp);
                end = System.currentTimeMillis();
                diff = end - init;

//                TransitionFunction tf = policy_ppferg.createTransitionFunction(learning.getQTable(),
//                        newModel.getTransitionFunction(), newModel);
//                newModel.setTransitionFunction(tf);

                TransitionFunction tf = createTransitionFunction(model, newModel);
                model.setTransitionFunction(tf);

                Log.info("\n" + new GridPrinter().print(tf, model));

                //Log.info("\nPolicy PPFERG: \n" + policy_ppferg.getBestPolicy().toString());
                Log.info("PPFERG time: " + Utils.toTimeString(diff));
                Log.info(pProblem.toString(policy_ppferg));
                //findBestPlan(p);
            }
        } while (model != null && (iterations < MAX_IT));

        return new Policy();//learning.getQTable().getPolicy(false);//TODO
    }

    @Override
    public String printResults() {
        return learning.printResults();
    }

    private void findBestPlan(Problem<ERG> pProblem) {
        Log.info("\nValue Iteration");
        //ERG model = pProblem.getModel();
        ValueIteration vi = new ValueIteration();
        long init = System.currentTimeMillis();
        Policy pi = vi.run(pProblem, (Object) null);
        long end = System.currentTimeMillis();
        long diff = end - init;
//        State st = pProblem.getInitialStates().get(0);
//        double sum = 0;
//        do {
//            Action a = pi.getBestAction(st);
//            sum += model.getRewardFunction().getValue(st, a);
//            st = model.getTransitionFunction().getBestReachableState(model.getStates(), st, a);
//        } while (st != null && !pProblem.getFinalStates().contains(st));

        Log.info("\nIterations: " + vi.getIterations());
        Log.info("Time: " + Utils.toTimeString(diff));
        //Log.info("Best plan reward value: " + sum);
        Log.info("Best policy: " + pProblem.toString(pi));
    }

    private TransitionFunction createTransitionFunction(final ERG model, final ERG newModel) {
        final Expression newPG = newModel.getPreservationGoal();
        final TransitionFunction tf = model.getTransitionFunction();

        final TransitionFunction ntf = new TransitionFunction() {
            @Override
            public double getValue(State pState, State pFinalState, Action pAction) {
                Collection<State> sts = tf.getReachableStates(model.getStates(), pState, pAction);
                for (State state : sts) {
                    try {
                        if (!newModel.getPropositionFunction().satisfies(state, newPG)) {
                            return 0;
                        }
                    } catch (InvalidExpressionException ex) {
                    }
                }
                return tf.getValue(pState, pFinalState, pAction);
            }
        };

        return ntf;
    }
    
    //    public TransitionFunction createTransitionFunction(final ERGQTable q, final TransitionFunction oldTf, final MDP mdp) {
//        TransitionFunction tf = new TransitionFunction() {
//            @Override
//            public double getValue(State pState, State pFinalState, Action pAction) {
//                State fstate = q.getFinalState(pState, pAction);
//                if (State.isValid(pFinalState, fstate) && containsKey(pState) && get(pState).containsKey(pAction)) {
//                    double oldValue = oldTf.getValue(pState, pFinalState, pAction);
//                    double sum = 0;
//                    int count = 0;
//                    for (Action action : mdp.getActions()) {
//                        if (get(pState).containsKey(action)) {
//                            double value = oldTf.getValue(pState, State.ANY, action);
//                            count = value > 0 ? count + 1 : count;
//                            sum += value;
//                        }
//                    }
//                    double diff = 1.0 - sum;
//                    double d = diff > 0 ? diff / count : 0;
//                    return oldValue + d;
//                }
//                return 0d;
//            }
//        };
//
//        return tf;
//    }
}
