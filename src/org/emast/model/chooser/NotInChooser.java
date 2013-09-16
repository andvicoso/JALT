package org.emast.model.chooser;

import java.util.Map;
import java.util.Set;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.chooser.base.SingleChooser;

/**
 *
 * @author anderson
 */
public class NotInChooser<T> implements SingleChooser<T>, MultiChooser<T> {

    private final Set<T> avoid;
    private final MultiChooser<T> delegate;

    public NotInChooser(MultiChooser<T> delegate, Set<T> avoid) {
        this.avoid = avoid;
        this.delegate = delegate;
    }

    @Override
    public T chooseOne(Map<T, Double> pValues) {
        Set<T> objs = delegate.choose(pValues);
        //remove all the current avoidable objects
        objs.removeAll(avoid);

        if (!objs.isEmpty()) {
            return objs.iterator().next();
        }
        return null;
    }

    @Override
    public Set<T> choose(Map<T, Double> pValues) {
        Set<T> objs = delegate.choose(pValues);
        //remove all the current avoidable objects
        objs.removeAll(avoid);

        return objs;
    }
}
