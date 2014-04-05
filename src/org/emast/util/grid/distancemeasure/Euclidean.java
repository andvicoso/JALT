package org.emast.util.grid.distancemeasure;

import org.emast.model.state.State;
import org.emast.util.grid.GridUtils;

/**
 *
 * @author andvicoso
 */
public class Euclidean implements DistanceMeasure {

    @Override
    public double getDistance(final State pS1, final State pS2) {
        int difr = GridUtils.getRow(pS1) - GridUtils.getRow(pS2);
        int difc = GridUtils.getCol(pS1) - GridUtils.getCol(pS2);
        difr *= difr;
        difc *= difc;

        int sum = Math.abs(difr) + Math.abs(difc);
        return Math.sqrt(sum);
    }
}
