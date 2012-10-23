package org.emast.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Anderson
 */
public final class Lookup {

    private final Map<Class, List> map;

    public Lookup() {
        map = new HashMap<Class, List>();
    }

    public Lookup(List pObjs) {
        this();
        putAll(pObjs);
    }

    public Lookup(Object... pObjs) {
        this();
        putAll(pObjs);
    }

    public void putAll(List pObjs) {
        for (Object o : pObjs) {
            put(o);
        }
    }

    public void putAll(Object... pObjs) {
        for (Object o : pObjs) {
            put(o);
        }
    }

    public <O> O get(Class<O> key) {
        return (O) (map.containsKey(key)
                ? map.get(key).get(0)
                : null);
    }

    public <O> List<O> getAll(Class<O> pKey) {
        return Collections.unmodifiableList(map.get(pKey));
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
