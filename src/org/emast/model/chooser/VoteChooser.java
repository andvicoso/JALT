package org.emast.model.chooser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.emast.util.CollectionsUtils;

/**
 * 
 * @author Anderson
 */
public class VoteChooser<T> implements Chooser<T> {

	@Override
	public T chooseOne(Map<T, Double> pValues) {
		return choose(pValues).iterator().next();
	}

	@Override
	public Set<T> choose(Map<T, Double> pValues) {
		Map<T, Integer> map = getMap(pValues);
		Integer max = Collections.max(map.values());

		return CollectionsUtils.getKeysForValue(map, max);
	}

	private Map<T, Integer> getMap(Map<T, Double> pValues) {
		Map<T, Integer> map = new HashMap<T, Integer>(pValues.size());
		for (T t : pValues.keySet()) {
			int count = map.containsKey(t) ? map.get(t) : 0;
			count++;
			map.put(t, count);
		}
		return map;
	}
}
