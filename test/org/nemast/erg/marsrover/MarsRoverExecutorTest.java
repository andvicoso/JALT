package org.nemast.erg.marsrover;

import org.emast.model.algorithm.executor.ERGExecutor;
import org.emast.model.algorithm.executor.Executor;
import org.emast.model.algorithm.executor.rewardcombinator.MeanRewardCombinator;
import org.emast.model.algorithm.planning.agent.factory.AgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.factory.PropReputationAgentIteratorFactory;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class MarsRoverExecutorTest extends Test {

    public MarsRoverExecutorTest() {
        super(createProblem(), createExecutor());
    }

    private static Problem createProblem() {
        final MarsRoverProblemFactory factory = new MarsRoverProblemFactory();
        final int rows = 9;
        final int cols = 9;
        final int size = rows * cols;
        final int agents = (int) (0.15 * size);
        final int obstacles = (int) (0.3 * size);

        return factory.createProblem(rows, cols, agents, obstacles);
    }

    private static Executor createExecutor() {
        AgentIteratorFactory factory = new PropReputationAgentIteratorFactory(-20);
        return new ERGExecutor(factory.createAgentIterators(null, null), new MeanRewardCombinator());
    }
}
