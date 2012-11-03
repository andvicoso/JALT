package org.emast.model.action;

import java.io.Serializable;
import org.emast.model.NamedObject;

public class Action extends NamedObject implements Serializable {

    public static final Action ANY = new Action("*");
    public static final Action TRIVIAL_ACTION = new Action("trivial_action");

    public Action() {
        super();
    }

    public Action(int pI) {
        super(pI);
    }

    public Action(final String pName) {
        super(pName);
    }

    public static boolean isValid(Action pAction1, Action pAction2) {
        return pAction1.equals(pAction2) || pAction1.equals(Action.ANY) || pAction2.equals(Action.ANY);
    }
}
