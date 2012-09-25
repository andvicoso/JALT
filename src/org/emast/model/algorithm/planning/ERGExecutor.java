package org.emast.model.algorithm.planning;

import java.util.*;
import org.emast.model.BadRewarder;
import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
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
public class ERGExecutor implements PolicyGenerator<ERG> {

    protected RewardCombinator rewardCombinator;
    protected int maxIterations;
    private Planner<ERG, PropReputationAgentIterator> planner;

    public ERGExecutor(PolicyGenerator<ERG> pPolicyGenerator, List<PropReputationAgentIterator> pAgents,
            RewardCombinator pRewardCombinator, int pMaxIterations) {
        rewardCombinator = pRewardCombinator;
        maxIterations = pMaxIterations;
        planner = new Planner<ERG, PropReputationAgentIterator>(pPolicyGenerator, pAgents);
    }

    @Override
    public String printResults() {
        return "";
    }

    @Override
    public Policy run(Problem<ERG> pProblem) {
        ERG model = pProblem.getModel();
        int iterations = 0;

        do {
            System.out.println("\nITERATION " + iterations + ":\n");
            //Policy policy = 
            Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //run problem
            planner.run(pProblem);
            //iterators
            List<PropReputationAgentIterator> as = planner.getIterators();
            //get results for each agent iterator
            for (PropReputationAgentIterator agentIt : as) {
                Map<Proposition, Double> propsRep = agentIt.getPropositionsReputation();
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
}
