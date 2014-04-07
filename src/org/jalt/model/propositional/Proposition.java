package org.jalt.model.propositional;

import org.jalt.model.NamedObject;

import java.io.Serializable;

/**
 *
 * @author andvicoso
 */
public class Proposition extends NamedObject implements Serializable {

    public Proposition(final String pString) {
        super(pString);
    }
}
