package org.emast.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anderson
 */
public final class Lookup {

    private final Map<Class, Collection> map;

    public Lookup(Object... pObjs) {
        this();
        putAll(pObjs);
    }

    public void putAll(Collection pObjs) {
        for (Object o : pObjs) {
            put(o);
        }
    }

    public void putAll(Object... pObjs) {
        for (Object o : pObjs) {
            put(o);
        }
    }

    public Lookup() {
        map = new HashMap<Class, Collection>();
    }

    public <O> O get(Class<O> key) {
        return (O) (map.containsKey(key)
                ? map.get(key).iterator().next()
                : null);
    }

    public <O> Collection<O> getAll(Class<O> pKey) {
        return map.get(pKey);
    }

    public void put(Object pObj) {
        Collection col;
        Class key = (Class) pObj.getClass();

        if (!map.containsKey(key)) {
            map.put(key, new ArrayList());
        }

        col = map.get(key);
        col.add(pObj);
    }

    public boolean contains(Class pKey) {
        return map.containsKey(pKey);
    }
}
