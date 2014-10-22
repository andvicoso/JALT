package org.jalt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jalt.model.NamedObject;

/**
 * 
 * @author andvicoso
 */
public class CollectionsUtils {

	private static final Random random = new Random();

	private CollectionsUtils() {
	}

	public static <M> M draw(Map<M, Double> pValues) {
		if (!pValues.isEmpty()) {
			double sum = 0;
			List<Double> limits = new ArrayList<Double>(pValues.size());
			List<M> vars = new ArrayList<M>(pValues.size());

			for (M m : pValues.keySet()) {
				sum += pValues.get(m);
				limits.add(sum);
				vars.add(m);
			}

			if (sum > 0) {
				double n = random.nextDouble() % sum;
				for (Double l : limits) {
					if (n < l) {
						return vars.get(limits.indexOf(l));
					}
				}
			} else {
				return getRandom(vars);
			}
		}
		return null;
	}

	public static <S> Map<String, S> asStringMap(S[] pParameters) {
		assert pParameters.length % 2 == 0;
		final Map<String, S> map = new HashMap<String, S>();
		for (int i = 0; i < pParameters.length; i += 2) {
			String key = pParameters[i].toString();
			S value = pParameters[i + 1];
			map.put(key, value);
		}

		return map;
	}

	public static Map<String, Object> asMap(String str, Object obj) {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(str, obj);

		return map;
	}

	public static <S> Map<Integer, S> asIndexMap(List<S> pList) {
		final HashMap<Integer, S> map = new HashMap<Integer, S>(pList.size());
		for (final S s : pList) {
			map.put(pList.indexOf(s), s);
		}
		return map;
	}

	public static <T> Map<T, Double> createMap(Collection<T> pKeys, Double pValue) {
		final Map<T, Double> map = new HashMap<>();
		for (T object : pKeys) {
			map.put(object, pValue);
		}
		return map;
	}

	public static <E extends NamedObject> Set<E> createSet(Class<E> pClass, String[] pNames) {
		final Set<E> set = new HashSet<E>(pNames.length);
		for (String name : pNames) {
			try {
				final E e = pClass.getConstructor(String.class).newInstance(name);
				set.add(e);
			} catch (Exception e) {
			}
		}

		return set;
	}

	public static <E extends NamedObject> List<E> createList(Class<E> pClass, String[] pNames) {
		final List<E> list = new ArrayList<E>(pNames.length);
		for (String name : pNames) {
			try {
				final E e = pClass.getConstructor(String.class).newInstance(name);
				list.add(e);
			} catch (Exception e) {
			}
		}

		return list;
	}

	public static <E extends NamedObject> List<E> createList(final Class<E> pClass, final int pN) {
		final List<E> list = new ArrayList<E>(pN);
		for (int i = 0; i < pN; i++) {
			try {
				final E e = pClass.getConstructor(int.class).newInstance(i);
				list.add(e);
			} catch (Exception e) {
			}
		}

		return list;
	}

	public static <E extends NamedObject> Set<E> createSet(final Class<E> pClass, final int pN) {
		final Set<E> list = new HashSet<E>(pN);
		for (int i = 0; i < pN; i++) {
			try {
				final E e = pClass.getConstructor(int.class).newInstance(i);
				list.add(e);
			} catch (Exception e) {
			}
		}

		return list;
	}

	public static <K, V> Set<K> getKeysForValue(Map<K, V> hm, V value) {
		Set<K> list = new HashSet<K>();
		for (K o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				list.add(o);
			}
		}
		return list;
	}

	public static <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
		final List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		final Map<K, V> result = new LinkedHashMap<K, V>(list.size());
		for (final Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Gets the elements of received type within the received list.
	 * 
	 * @param pList
	 *            the list
	 * @param pType
	 *            the type to search for
	 * 
	 * @return the elements of the received type
	 */
	@SuppressWarnings("unchecked")
	public static <C> List<C> getElementsOfType(final Collection<?> pList, final Class<C> pType) {
		final List<C> list = new ArrayList<C>();

		if (pList != null && pList.size() > 0) {
			synchronized (pList) {
				for (final Object obj : pList) {
					if (pType.isAssignableFrom(obj.getClass())) {
						list.add((C) obj);
					}
				}
			}
		}

		return list;
	}

	public static <C> boolean containsElementOfType(final Collection<?> pList, final Class<C> pType) {
		return getElementsOfType(pList, pType).size() > 0;
	}

	@SafeVarargs
	public static <E> Set<E> asSet(final E... pEs) {
		return new HashSet<E>(Arrays.asList(pEs));
	}

	public static <T> List<List<T>> createListsFromList(final List<T> pList) {
		final List<List<T>> result = new ArrayList<List<T>>();
		for (final T item : pList) {
			result.add(Collections.singletonList(item));
		}

		return result;
	}

	public static <T extends Comparable<? super T>> List<List<T>> getAllCombinations(
			final List<T> pList, final int pSize) {
		assert (pSize < pList.size());
		final List<List<T>> result = new ArrayList<List<T>>();

		if (pSize == 0) {
			result.add(new ArrayList<T>());
			return result;
		}

		final List<List<T>> combinations = getAllCombinations(pList, pSize - 1);
		for (final List<T> combination : combinations) {
			for (final T element : pList) {
				if (combination.contains(element)) {
					continue;
				}

				final List<T> list = new ArrayList<T>();
				list.addAll(combination);

				if (list.contains(element)) {
					continue;
				}

				list.add(element);
				Collections.sort(list);

				if (result.contains(list)) {
					continue;
				}

				result.add(list);
			}
		}

		return result;
	}

	public static <T> List<List<T>> getAllCombinations(final List<T> pList) {
		final List<List<T>> result = new ArrayList<List<T>>(pList.size());
		// if it has only one element, the resulting list
		// is a list with the single element
		if (pList.size() == 1) {
			final List<T> temp = new ArrayList<T>();
			temp.add(pList.get(0));
			result.add(temp);
		} else {
			for (final T element : pList) {
				// create a new sublist
				final List<T> temp = new ArrayList<T>(pList);
				// remove the current item
				temp.remove(element);
				// get the all combinations for the sublist
				final List<List<T>> sublist = getAllCombinations(temp);
				for (final List<T> item : sublist) {
					item.add(element);
				}

				result.addAll(sublist);
			}
		}

		return result;
	}

	public static <O> O getRandom(final Collection<O> pObjects) {
		if (pObjects != null && !pObjects.isEmpty()) {
			int r = Math.abs(random.nextInt() % pObjects.size());
			List<O> objects = !(pObjects instanceof List) ? new ArrayList<O>(pObjects)
					: (List<O>) pObjects;

			return objects.get(r);
		}
		return null;
	}
}
