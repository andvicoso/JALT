package org.emast.model.algorithm.ensemble;

import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.agent.ERGQLearning;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.iteration.rl.QTable;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.ValidPathFinder;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class AgentEnsemble implements PolicyGenerator<ERG> {

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
        Problem<ERG> problem = pProblem;
        ERG model = problem.getModel();
        Policy policy;
        int iterations = 0;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");
            //create policy
            policy = policyGenerator.run(pProblem, pParameters);

            for (int i = 0; i < model.getAgents(); i++) {
                final ERGQLearning agentIterator = new ERGQLearning();
                agentIterators.add(agentIterator);
                agentIterator.run(pProblem, policy);
            }

            //wait to be awakened from the planner notification
            //(when it finished running all agents)
            //wait(planner);
        } while (changePreservGoal(pProblem));

        return policy;
    }

    public Set<Proposition> choose(QTable q, ERG model) {
        //combine reputations for propositions from agents
        Map<Proposition, Double> combined = combine(q, model);
        Log.info("Combined prop values: " + combined);
        //get "bad" propositions
        final Set<Proposition> set = new HashSet<Proposition>();
        for (Proposition prop : combined.keySet()) {
            if (combined.get(prop) <= -10) {
                set.add(prop);
            }
        }
        return set;
    }

    public Map<Proposition, Double> combine(QTable q, ERG model) {
        Map<Proposition, Integer> count = new HashMap<Proposition, Integer>(model.getPropositions().size());
        Map<Proposition, Double> result = new HashMap<Proposition, Double>(model.getPropositions().size());
        final Collection<State> states = model.getStates();
        final Collection<Action> actions = model.getActions();

        for (final State state : states) {
            for (final Action action : actions) {
                double value = q.get(state, action);
                State nextState = model.getTransitionFunction().getBestReachableState(states, state, action);
                Set<Proposition> props = model.getPropositionFunction().getPropositionsForState(nextState);
                
                if()
            }
        }

        return result;
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
        final Collection<State> states = pProblem.getModel().getStates();
        final Collection<Action> actions = pProblem.getModel().getActions();
        QTable q = new QTable(states, actions);

        for (final State state : states) {
            for (final Action action : actions) {
                double sum = 0;
                //get results for each agent
                for (ERGQLearning agent : agentIterators) {
                    sum += agent.getQTable().get(state, action);
                }
                q.put(state, action, sum / agentIterators.size());
            }
        }

        //choose "bad" propositions
        Collection<Proposition> props = choose(q, pProblem.getModel());
        //verify the need to change the preservation goal
        return !props.isEmpty() && changePreservationGoal(pProblem, props);
    }
}
