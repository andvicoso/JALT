package org.emast.model.algorithm.executor;

import java.util.*;
import org.emast.model.BadRewarder;
import org.emast.model.algorithm.executor.rewardcombinator.RewardCombinator;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
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
public class ERGExecutor<R> extends Executor<ERG, PropReputationAgentIterator, R> {

    private static final int MAX_ITERATIONS = 10;

    public ERGExecutor(List<PropReputationAgentIterator> pAgents, RewardCombinator pRewardCombinator) {
        super(pAgents, pRewardCombinator);
    }

    @Override
    public String printResults() {
        return "";
    }

    @Override
    public R run(Problem<ERG> pProblem) {
        ERG model = pProblem.getModel();
        int count = 0;
        do {
            final Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //run problem
            getPlanner().run(pProblem);
            //iterators
            List<PropReputationAgentIterator> as = getPlanner().getIterators();
            //get results for each agent iterator
            for (final PropReputationAgentIterator agentIt : as) {
                Map<Proposition, Double> propsRep = agentIt.getPropositionsReputation();
                reps.add(propsRep);
            }
            //combine reputations for propositions from agents
            Map<Proposition, Double> combined = getRewardCombinator().combine(reps);
            //get "bad" propositions
            Collection<Proposition> props = getBadPropositions(model, combined);
            //verify the need to change the preservation goal
            if (!props.isEmpty()) {
                changePreservationGoal(pProblem, props);
            }
        } while (count++ < MAX_ITERATIONS);
        //run problem again with the final combined preserv. goals
        getPlanner().run(pProblem);

        return null;
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
            final Policy p = getPlanner().run(newProblem);
            //if there isn`t a path to reach the final goal,
            if (canReachFinalGoal(p, newProblem)) {
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

    private boolean canReachFinalGoal(final Policy pPolicy, final Problem<ERG> pProblem) {
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
