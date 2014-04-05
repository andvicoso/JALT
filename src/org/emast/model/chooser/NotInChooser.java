package org.emast.model.chooser;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author andvicoso
 */
public class NotInChooser<T> implements Chooser<T> {

	private final Set<T> avoid;

	public NotInChooser(Set<T> avoid) {
		this.avoid = avoid;
	}

	@Override
	public T chooseOne(Map<T, Double> pValues) {
		Set<T> objs = choose(pValues);

		if (!objs.isEmpty()) {
			return objs.iterator().next();
		}
		return null;
	}

	@Override
	public Set<T> choose(Map<T, Double> pValues) {
		Set<T> objs = pValues.keySet();
		// remove all the current avoidable objects
		objs.removeAll(avoid);

		return objs;
	}
}
