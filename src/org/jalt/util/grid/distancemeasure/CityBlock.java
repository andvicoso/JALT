package org.jalt.util.grid.distancemeasure;

import org.jalt.model.state.State;
import org.jalt.util.grid.GridUtils;

/**
 *
 * @author andvicoso
 */
public class CityBlock implements DistanceMeasure {

    @Override
    public double getDistance(final State pS1, final State pS2) {
        int difr = GridUtils.getRow(pS1) - GridUtils.getRow(pS2);
        int difc = GridUtils.getCol(pS1) - GridUtils.getCol(pS2);
        return Math.abs(difr) + Math.abs(difc);
    }
}
