package org.emast.model.algorithm.iteration;

import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.algorithm.DefaultAlgorithm;
import org.emast.model.model.MDP;
import org.emast.model.state.State;
import static org.emast.util.DefaultTestProperties.*;

public abstract class IterationAlgorithm<M extends MDP, R> extends DefaultAlgorithm<M, R> {

    /**
     * Discount factor The discount factor determines the importance of future rewards. A factor of 0 will
     * make the agent "opportunistic" by only considering current rewards, while a factor approaching 1 will
     * make it strive for a long-term high reward. If the discount factor meets or exceeds 1, the values may
     * diverge.
     */
    protected double gama = GAMA;
    protected int episodes = 0;
    protected M model;

    public int getEpisodes() {
        return episodes;
    }

    public double getGama() {
        return gama;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nEpisodes: ").append(episodes);
        sb.append("\nGama: ").append(gama);

        return sb.toString();
    }

    protected double getError(Map<State, Double> lastv, Map<State, Double> v) {
        double maxDif = -Double.MAX_VALUE;

        if (episodes == 0) {
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

        Log.info("Error: " + String.format("%.4g", maxDif));

        return maxDif;
    }
}
