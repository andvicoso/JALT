package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.util.*;
import org.emast.model.BadRewarder;
import org.emast.model.agent.PropReputationAgent;
import org.emast.model.algorithm.planning.rewardcombinator.RewardCombinator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGExecutor implements PolicyGenerator<ERG>, PropertyChangeListener {

    private static PrintStream DEBUG_WRITER = System.out;
    private static boolean DEBUG = true;
    private RewardCombinator rewardCombinator;
    private Planner<ERG, PropReputationAgent> planner;
    private int maxIterations;

    public ERGExecutor(PolicyGenerator<ERG> pPolicyGenerator, List<PropReputationAgent> pAgents,
            RewardCombinator pRewardCombinator, int pMaxIterations) {
        rewardCombinator = pRewardCombinator;
        maxIterations = pMaxIterations;
        planner = new Planner<ERG, PropReputationAgent>(pPolicyGenerator, pAgents);
    }

    @Override
    public String printResults() {
        return "";
    }

    @Override
    public synchronized Policy run(Problem<ERG> pProblem) {
        ERG model = pProblem.getModel();
        int iterations = 0;
        //listen to planner property changes
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);
        //start main loop
        do {
            print("\nITERATION " + iterations + ":\n");
            //Policy policy = 
            Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //run problem
            planner.run(pProblem);
            //wait to be awakened from planner notification (when it finished running all agents)
            try {
                wait();
            } catch (InterruptedException ex) {
                return null;
            }
            //get agents
            List<PropReputationAgent> agents = planner.getAgents();
            //get results for each agent
            for (PropReputationAgent agent : agents) {
                Map<Proposition, Double> propsRep = agent.getPropositionsReputation();
                reps.add(propsRep);
            }
            //combine reputations for propositions from agents
            Map<Proposition, Double> combined = rewardCombinator.combine(reps);
            //get "bad" propositions
            Collection<Proposition> props = getBadPropositions(model, combined);
            //verify the need to change the preservation goal
            if (!props.isEmpty()) {
                changePreservationGoal(pProblem, props);
            }
        } while (iterations++ < maxIterations);
        //run problem again with the combined preserv. goals
        //to get the policy
        return planner.getPolicyGenerator().run(pProblem);
    }

    protected boolean changePreservationGoal(Problem<ERG> pProblem,
            Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and bad reward props
        Expression newPreservGoal = createNewPreservationGoal(originalPreservGoal, pProps);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal.negate())
                && existValidFinalState(model, newPreservGoal)) {
            //create a new cloned problem
            ERG newModel = cloneModel(model, newPreservGoal);
            Problem newProblem = new Problem(newModel, pProblem.getInitialStates());
            //Execute the base algorithm (PPFERG) over the new model (with new preservation goal)
            //if there are paths for all to reach the goal,
            if (planner.existValidPlan(newProblem)) {
                //set the preservation goal to the current problem
                pProblem.getModel().setPreservationGoal(newPreservGoal);
                //confirm the goal modification
                System.out.println("changed preservation goal from {"
                        + originalPreservGoal + "} to {" + newPreservGoal + "}");
                return true;
            }
        }
        return false;
    }

    private Expression createNewPreservationGoal(Expression pOriginalPreservGoal,
            Collection<Proposition> pProps) {
        Expression exp = new Expression(BinaryOperator.AND, pProps);
        //negate it
        exp = exp.negate();
        //join with the current preserv goal
        return new Expression(BinaryOperator.AND, pOriginalPreservGoal, exp);
    }

    protected ERG cloneModel(ERG pModel, Expression pNewPreservGoal) {
        ERG newModel = (ERG) pModel.copy();
        //set new preservation goal
        newModel.setPreservationGoal(pNewPreservGoal);

        return newModel;
    }

    private Collection<Proposition> getBadPropositions(ERG pModel, Map<Proposition, Double> pCombined) {
        return pModel instanceof BadRewarder
                ? ((BadRewarder) pModel).getBadRewardProps()
                : Collections.EMPTY_LIST;
    }

    private boolean existValidFinalState(ERG model, Expression newPreservGoal) {
        try {
            Collection<State> finalStates = model.getPropositionFunction().intension(
                    model.getStates(), model.getPropositions(), model.getGoal());

            for (State state : finalStates) {
                if (model.getPropositionFunction().satisfies(state, newPreservGoal)) {
                    return true;
                }
            }
        } catch (InvalidExpressionException ex) {
        }

        return false;
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent pEvt) {
        if (Planner.FINISHED_ALL_PROP.equals(pEvt.getPropertyName())) {
            notifyAll();
        }
    }

    private void print(String pMsg) {
        if (DEBUG) {
            DEBUG_WRITER.println(pMsg);
        }
    }
}
