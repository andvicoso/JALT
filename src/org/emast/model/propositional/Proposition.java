package org.emast.model.propositional;

import org.emast.model.NamedObject;
import java.io.Serializable;

/**
 *
 * @author anderson
 */
public class Proposition extends NamedObject implements Serializable {

    public Proposition(final String pString) {
        super(pString);
    }
}
