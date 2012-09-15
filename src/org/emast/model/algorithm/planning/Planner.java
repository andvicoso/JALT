package org.emast.model.algorithm.planning;

import java.util.List;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.AlgorithmThread;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.algorithm.planning.agent.factory.AgentIteratorFactory;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP, R> implements Algorithm<M, R> {

    private Policy initialPolicy;
    private List<AgentIterator> iterators;
    private final AgentIteratorFactory factory;

    public Planner(Policy pInitialPolicy, AgentIteratorFactory pFactory) {
        initialPolicy = pInitialPolicy;
        factory = pFactory;
    }

    public Policy getInitialPolicy() {
        return initialPolicy;
    }

    @Override
    public R run(Problem<M> pProblem) {
        createIterators(pProblem, initialPolicy);
        //execute them all
        for (final AgentIterator agentPlanner : iterators) {
            new AlgorithmThread(agentPlanner, pProblem).start();
        }

        return null; //TODO:
    }

    protected void createIterators(Problem<M> problem, Policy pPolicy) {
        iterators = factory.createAgentIterators(problem, pPolicy);
    }

    public List<AgentIterator> getIterators() {
        return iterators;
    }

    @Override
    public String printResults() {
        return "";
    }
}
