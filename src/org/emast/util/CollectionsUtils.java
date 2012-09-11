package org.emast.util;

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
import java.util.Set;
import org.emast.model.NamedObject;

/**
 *
 * @author Anderson
 */
public class CollectionsUtils {

    public static <S> Map<Integer, S> asMap(List<S> pList) {
        final HashMap<Integer, S> map = new HashMap<Integer, S>(pList.size());
        for (final S s : pList) {
            map.put(pList.indexOf(s), s);
        }
        return map;
    }

    private CollectionsUtils() {
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

    public static <E extends NamedObject> List<E> createList(final Class<E> pClass,
            final int pN) {
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

    public static <E extends NamedObject> Set<E> createSet(final Class<E> pClass,
            final int pN) {
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

    public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        final List<V> list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        final Map<K, V> result = new LinkedHashMap<K, V>(list.size());
        for (final Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Gets the elements of received type within the received list.
     *
     * @param pList the list
     * @param pType the type to search for
     *
     * @return the elements of the received type
     */
    @SuppressWarnings("unchecked")
    public static <C> List<C> getElementsOfType(final Collection pList,
            final Class<C> pType) {
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

    public static <C> boolean containsElementOfType(final Collection pList,
            final Class<C> pType) {
        return getElementsOfType(pList, pType).size() > 0;
    }

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
        //if it has only one element, the resulting list
        //is a list with the single element
        if (pList.size() == 1) {
            final List<T> temp = new ArrayList<T>();
            temp.add(pList.get(0));
            result.add(temp);
        } else {
            for (final T element : pList) {
                //create a new sublist
                final List<T> temp = new ArrayList<T>(pList);
                //remove the current item
                temp.remove(element);
                //get the all combinations for the sublist
                final List<List<T>> sublist = getAllCombinations(temp);
                for (final List<T> item : sublist) {
                    item.add(element);
                }

                result.addAll(sublist);
            }
        }

        return result;
    }
}
