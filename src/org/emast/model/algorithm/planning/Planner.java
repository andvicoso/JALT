package org.emast.model.algorithm.planning;

import java.util.ArrayList;
import java.util.List;
import org.emast.model.algorithm.planning.agent.AgentIterator;
import org.emast.model.algorithm.planning.agent.AgentIteratorsFactory;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class Planner {

    private Policy initialPolicy;
    private final MDP model;
    private final Problem<MDP> problem;
    private List<AgentIterator> iterators;
    private final AgentIteratorsFactory factory;

    public Planner(Problem pProblem, Policy pInitialPolicy, AgentIteratorsFactory pFactory) {
        initialPolicy = pInitialPolicy;
        factory = pFactory;
        problem = pProblem;
        model = pProblem.getModel();
    }

    public Policy getInitialPolicy() {
        return initialPolicy;
    }

    public void run() {
        createIterators(initialPolicy);
        //execute all
        for (final AgentIterator agentPlanner : iterators) {
            final String name = agentPlanner.getClass().getSimpleName()
                    + ": " + agentPlanner.getAgent()
                    + " - " + problem.getClass().getSimpleName();
            new Thread(agentPlanner, name).start();
        }
    }

    protected void createIterators(final Policy pPolicy) {
        iterators = factory.createAgentIterators(problem, pPolicy);
    }
}
