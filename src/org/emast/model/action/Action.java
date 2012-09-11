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
}
