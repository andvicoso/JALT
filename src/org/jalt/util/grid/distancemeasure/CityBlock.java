package org.jalt.util.grid.distancemeasure;

import org.jalt.model.state.State;
import org.jalt.util.grid.GridUtils;

/**
 *
 * @author andvicoso
 */
public class CityBlock implements DistanceMeasure {

    @Override
    public double getDistance(final State s1, final State s2) {
        int difr = GridUtils.getRow(s1) - GridUtils.getRow(s2);
        int difc = GridUtils.getCol(s1) - GridUtils.getCol(s2);
        return Math.abs(difr) + Math.abs(difc);
    }
}
