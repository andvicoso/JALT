package org.emast.model.algorithm.planning;

import java.util.*;
import org.emast.model.BadRewarder;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
import org.emast.model.algorithm.planning.rewardcombinator.RewardCombinator;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class ERGExecutor implements PolicyGenerator<ERG> {

    protected final RewardCombinator rewardCombinator;
    protected final int maxIterations;
    private final Planner<ERG, PropReputationAgentIterator> planner;

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
            //Policy policy = 
            final Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //run problem
            planner.run(pProblem);
            //iterators
            List<PropReputationAgentIterator> as = planner.getIterators();
            //get results for each agent iterator
            for (final PropReputationAgentIterator agentIt : as) {
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
        //run problem again with the final combined preserv. goals
        run(pProblem);

        return null;// TODO: 
    }

    protected boolean changePreservationGoal(final Problem<ERG> pProblem,
            final Collection<Proposition> pProps) {
        final ERG model = pProblem.getModel();
        //save the original preservation goal
        final Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and the state
        final Expression newPropsExp = createNewPreservationGoal(originalPreservGoal, pProps);
        //copy the original preservation goal
        final Expression newPreservGoal = new Expression(originalPreservGoal.toString());
        //and join them with an AND operator
        newPreservGoal.add(newPropsExp, BinaryOperator.AND);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPropsExp)
                && !originalPreservGoal.contains(newPropsExp.negate())) {
            //create a new cloned problem
            final ERG newModel = cloneModel(model, newPreservGoal);
            final Problem newProblem = new Problem(newModel, pProblem.getInitialStates());
            //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
            run(newProblem);
            //if there isn`t a path to reach the final goal,
            if (canReachFinalGoal(newProblem)) {
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

    private boolean canReachFinalGoal(final Problem<ERG> pProblem) {
        boolean ret = true;
        final ERG model = pProblem.getModel();
        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final AgentIterator iterator = new AgentIterator(i);
            //find the plan for the newly created problem
            //with the preservation goal changed
            iterator.run(pProblem);
            //get the resulting plan
            final Plan plan = iterator.getPlan();
            //save in ret if a plan was generated
            ret &= plan != null && !plan.isEmpty();
        }
        return ret;
    }

    private Expression createNewPreservationGoal(final Expression pOriginalPreservGoal,
            final Collection<Proposition> pProps) {
        Expression exp = new Expression(pProps, BinaryOperator.AND);
        //negate it
        exp = exp.negate();
        //join with the current
        exp.add(pOriginalPreservGoal, BinaryOperator.AND);

        return exp;
    }

    protected ERG cloneModel(final ERG pModel, final Expression pNewPreservGoal) {
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
}
