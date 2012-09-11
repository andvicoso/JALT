package org.emast.model.agent;

import java.io.Serializable;
import org.emast.model.NamedObject;

public class Agent extends NamedObject implements Serializable {

    public Agent(final int pI) {
        super(pI);
    }

    public Agent(final String pName) {
        super(pName);
    }
}
