package org.emast.model.state;

import java.io.Serializable;
import org.emast.model.NamedObject;

public class State extends NamedObject implements Serializable{

    public static final String NAME_PREFIX = "s";
    public static final State ANY = new State("*");

    public State(final int pI) {
        super(pI);
    }

    public State(final String pString) {
        super(pString);
    }
}
