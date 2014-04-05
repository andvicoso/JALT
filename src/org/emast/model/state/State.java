package org.emast.model.state;

import java.io.Serializable;
import org.emast.model.NamedObject;
/**
 * 
 * @author andvicoso
 */
public class State extends NamedObject implements Serializable {

    public static final String NAME_PREFIX = "s";

    public State() {
    }

    public State(final int pInt) {
        super(pInt);
    }

    public State(final String pString) {
        super(pString);
    }

    public static boolean isValid(State pState1, State pState2) {
        return pState1 != null && pState2 != null && pState1.equals(pState2);
                //&& (pState1.equals(pState2) || pState1.equals(State.ANY) || pState2.equals(State.ANY));
    }
}
