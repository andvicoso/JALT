package org.emast.model.chooser.base;

import java.util.Map;

/**
 *
 * @author Anderson
 */
public interface SingleChooser<T> {

    T chooseOne(Map<T, Double> pValues);
}
