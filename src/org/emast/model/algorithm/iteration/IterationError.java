package org.emast.model.algorithm.iteration;

import java.util.Map;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class IterationError {

    public static double getError(int n, Map<State, Double> lastv, Map<State, Double> v) {
        double maxDif = -Double.MAX_VALUE;

        if (n == 0) {
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

        //Log.info("Error: " + String.format("%.4g", maxDif));

        return maxDif;
    }
}
