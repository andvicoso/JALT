package org.emast.model;

import java.io.Serializable;

public class NamedObject implements Comparable<NamedObject>, Serializable {

    private String name;

    public NamedObject() {
    }

    public NamedObject(final int pI) {
        this(pI + "");
    }

    public NamedObject(final String pName) {
        name = pName;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof NamedObject
                && (super.equals(obj) || getName().equals(obj.toString()));
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(final NamedObject pObj) {
        return getName().compareTo(pObj.getName());
    }
}
