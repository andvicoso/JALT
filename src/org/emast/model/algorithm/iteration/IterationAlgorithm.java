package org.emast.model.algorithm.iteration;

import java.util.Map;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.model.MDP;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

public abstract class IterationAlgorithm<M extends MDP> extends DefaultAlgorithm<M, Policy>
        implements PolicyGenerator<M> {

    public static final int MAX_ITERATIONS = 10;
    /**
     * Discount factor The discount factor determines the importance of future rewards. A factor of 0 will
     * make the agent "opportunistic" by only considering current rewards, while a factor approaching 1 will
     * make it strive for a long-term high reward. If the discount factor meets or exceeds 1, the values may
     * diverge.
     */
    protected double gama = 0.9d;
    protected int iterations;
    protected M model;

    public IterationAlgorithm() {
        iterations = -1;
    }

    public int getIterations() {
        return iterations;
    }

    public double getGama() {
        return gama;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);

        return sb.toString();
    }

    protected double getError(Map<State, Double> lastv, Map<State, Double> v) {
        double maxDif = -Double.MAX_VALUE;

        if (iterations == 0) {
            maxDif = Double.MAX_VALUE;
        } else {
            for (State state : lastv.keySet()) {
                Double val1 = lastv.get(state);
                Double val2 = v.get(state);

                if (val1 == null || val2 == null) {
                    break;
                }

                double dif = Math.abs(val2 - val1);
                if (dif > maxDif) {
                    maxDif = dif;
                }
            }
        }

        return maxDif;
    }
}
