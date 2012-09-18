package org.emast.model.algorithm.executor;

import java.util.List;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.executor.rewardcombinator.RewardCombinator;
import org.emast.model.algorithm.planning.Planner;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
import org.emast.model.model.MDP;

/**
 *
 * @author Anderson
 */
public abstract class Executor<M extends MDP, A extends AgentIterator, R>
        implements Algorithm<M, R> {

    private final Planner<M, A> planner;
    private final RewardCombinator rewardCombinator;

    public Executor(List<A> pAgents, RewardCombinator pRewardCombinator) {
        planner = new Planner(pAgents);
        rewardCombinator = pRewardCombinator;
    }

    public Planner getPlanner() {
        return planner;
    }

    public RewardCombinator getRewardCombinator() {
        return rewardCombinator;
    }
}
