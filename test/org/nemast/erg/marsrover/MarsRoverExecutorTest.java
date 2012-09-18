package org.nemast.erg.marsrover;

import java.util.List;
import org.emast.model.algorithm.planning.ERGExecutor;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.algorithm.planning.rewardcombinator.impl.MeanRewardCombinator;
import org.emast.model.algorithm.planning.agent.factory.AgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.factory.PropReputationAgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.iterator.PropReputationAgentIterator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class MarsRoverExecutorTest extends Test {

    final static int rows = 9;
    final static int cols = 9;
    final static int size = rows * cols;
    final static int agents = (int) (0.15 * size);
    final static int obstacles = (int) (0.3 * size);

    public MarsRoverExecutorTest() {
        super(createProblem(), createExecutor());
    }

    private static Problem createProblem() {
        final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();
        return factory.createProblem(rows, cols, agents, obstacles);
    }

    private static ERGExecutor createExecutor() {
        PolicyGenerator<ERG> pg = new PPFERG<ERG>();
        int maxIterations = 100;
        AgentIteratorFactory factory = new PropReputationAgentIteratorFactory(-20);
        List<PropReputationAgentIterator> agentIts = factory.createAgentIterators(agents);

        return new ERGExecutor(pg, agentIts, new MeanRewardCombinator(), maxIterations);
    }

    public static void main(String[] args) {
        new MarsRoverExecutorTest().run();
    }
}
