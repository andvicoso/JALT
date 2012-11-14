package org.emast.model.algorithm.ensemble;

import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.Chooser;
import org.emast.model.Combinator;
import org.emast.model.agent.ERGQLearning;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.ValidPathFinder;
import org.emast.model.planning.propositionschooser.MinValueChooser;
import org.emast.model.planning.rewardcombinator.MeanRewardCombinator;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class AgentEnsemble extends DefaultAlgorithm<ERG, Policy> implements PolicyGenerator<ERG> {

    private final PolicyGenerator<ERG> policyGenerator;
    private List<ERGQLearning> agentIterators;

    public AgentEnsemble(PolicyGenerator<ERG> pPolicyGenerator) {
        policyGenerator = pPolicyGenerator;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (ERGQLearning agent : agentIterators) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        Policy policy;
        int iterations = 0;
        Problem<ERG> problem = pProblem;
        ERG model = problem.getModel();
        //start main loop
        do {
            agentIterators = new ArrayList<ERGQLearning>(model.getAgents());
            Log.info("\nITERATION " + iterations++ + ":\n");
            //create initial policy
            policy = policyGenerator.run(pProblem);

            for (int i = 0; i < model.getAgents(); i++) {
                final ERGQLearning agentIterator = new ERGQLearning();
                agentIterators.add(agentIterator);
                agentIterator.run(pProblem, policy);
            }
        } while (changePreservGoal(pProblem));

        return policy;
    }

    protected boolean changePreservationGoal(Problem<ERG> pProblem, Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and bad reward props
        Expression newPreservGoal = new PreservationGoalFactory().createPreservationGoal(originalPreservGoal, pProps);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal.negate())
                && existValidFinalState(model, newPreservGoal)) {
            //create a new cloned problem
            ERG newModel = cloneModel(model, newPreservGoal);
            Problem newProblem = new Problem(newModel, pProblem.getInitialStates());
            //Execute the base algorithm (PPFERG) over the new model (with new preservation goal)
            //if there are paths for all to reach the goal
            Log.info("Trying to find a valid plan for preserv: " + model.getPreservationGoal());
            if (ValidPathFinder.exist(newProblem, policyGenerator, false)) {
                //set the preservation goal to the current problem
                model.setPreservationGoal(newPreservGoal);
                //confirm the goal modification
                Log.info("Changed preservation goal from {"
                        + originalPreservGoal + "} to {" + newPreservGoal + "}");
                return true;
            }
        }
        return false;
    }

    protected ERG cloneModel(ERG pModel, Expression pNewPreservGoal) {
        ERG newModel = (ERG) pModel.copy();
        //set new preservation goal
        newModel.setPreservationGoal(pNewPreservGoal);

        return newModel;
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

    private boolean changePreservGoal(Problem<ERG> pProblem) {
        Combinator comb = new MeanRewardCombinator();
        Chooser chooser = new MinValueChooser(comb);//new CombinePropsRewardChooser(comb, -10);
        Collection<Map<Proposition, Double>> values = new ArrayList<Map<Proposition, Double>>(agentIterators.size());
        //get results for each agent
        for (ERGQLearning agent : agentIterators) {
            values.add(agent.getPropsValues());
        }
        //choose "bad" propositions
        Collection<Proposition> props = chooser.choose(values);
        //verify the need to change the preservation goal
        return !props.isEmpty() && changePreservationGoal(pProblem, props);
    }
}
