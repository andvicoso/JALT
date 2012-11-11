package org.emast.model.algorithm.iteration;

import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.MDP;

public abstract class IterationAlgorithm<M extends MDP> implements PolicyGenerator<M> {

    protected int iterations;
    /**
     * Discount factor The discount factor determines the importance of future rewards. A factor of 0 will
     * make the agent "opportunistic" by only considering current rewards, while a factor approaching 1 will
     * make it strive for a long-term high reward. If the discount factor meets or exceeds 1, the values may
     * diverge.
     */
    protected double gama = 0.9d;
    protected M model;
    public static final int MAX_ITERATIONS = 1000;

    public IterationAlgorithm() {
        iterations = -1;
    }

    public int getIterations() {
        return iterations;
    }

    public double getGama() {
        return gama;
    }
}
