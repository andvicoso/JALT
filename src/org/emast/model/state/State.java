package org.emast.model.state;

import java.io.Serializable;
import org.emast.model.NamedObject;

public class State extends NamedObject implements Serializable {

    public static final String NAME_PREFIX = "s";
    public static final State ANY = new State("*");

    public State(final int pI) {
        super(pI);
    }

    public State(final String pString) {
        super(pString);
    }

    public static boolean isValid(State pState1, State pState2) {
        return pState1.equals(pState2) || pState1.equals(State.ANY) || pState2.equals(State.ANY);
    }
}
