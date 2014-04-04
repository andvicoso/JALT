package org.emast.model.observation;

import org.emast.model.NamedObject;

public class Observation extends NamedObject {

    public static final Observation ANY = new Observation(STAR);

    public Observation(String pName) {
        super(pName);
    }

    public Observation(int pI) {
        super(pI);
    }
}
