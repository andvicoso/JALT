package org.emast.util.grid.distancemeasure;

import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class Circle implements DistanceMeasure {

    private static DistanceMeasure euclidean = new Euclidean();

    @Override
    public double getDistance(final State pS1, final State pS2) {
        return Math.floor(euclidean.getDistance(pS1, pS2));
    }
}
