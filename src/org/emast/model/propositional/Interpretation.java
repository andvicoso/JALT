package org.emast.model.propositional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author And
 */
public class Interpretation extends HashMap<Proposition, Boolean> {

    public Interpretation(final Collection<Proposition> pProps) {
        for (final Proposition proposition : pProps) {
            put(proposition, Boolean.TRUE);
        }
    }

    public Interpretation(final Map<Proposition, Boolean> pValues) {
        super(pValues);
    }

    public Interpretation() {
    }

    public boolean isAllFalse() {
        return !values().contains(Boolean.TRUE);
    }

    @Override
    public String toString() {
        Iterator<Entry<Proposition, Boolean>> i = entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<Proposition, Boolean> e = i.next();
            Proposition key = e.getKey();
            Boolean value = e.getValue();
            sb.append(key);
            if (!value.booleanValue()) {
                sb.append('=');
                sb.append(value);
            }
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(", ");
        }
    }
}
