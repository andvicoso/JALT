package org.emast.model.algorithm.iteration;

import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import static org.emast.util.DefaultTestProperties.*;

public abstract class IterationAlgorithm<M extends MDP, R> implements Algorithm<M, R> {

    /**
     * Discount factor The discount factor determines the importance of future rewards. A factor of 0 will
     * make the agent "opportunistic" by only considering current rewards, while a factor approaching 1 will
     * make it strive for a long-term high reward. If the discount factor meets or exceeds 1, the values may
     * diverge.
     */
    protected double gama = GAMA;
    protected int episodes = 0;
    protected M model;

    public int getIterations() {
        return episodes;
    }

    public double getGama() {
        return gama;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nEpisodes: ").append(episodes);
        //sb.append("\nGama: ").append(gama);//TODO:descomentar em produção

        return sb.toString();
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
