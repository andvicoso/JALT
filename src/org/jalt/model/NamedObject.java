package org.jalt.model;

import java.io.Serializable;

public class NamedObject implements Comparable<NamedObject>, Serializable {

	protected static final String EMPTY_STR = "";
	private String name;

	public NamedObject() {
	}

	public NamedObject(final int pI) {
		this(pI + EMPTY_STR);
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
		return super.equals(obj)
				|| (obj instanceof NamedObject && getName().equals(obj.toString()));
	}

	@Override
	public int hashCode() {
		return getName() != null ? getName().hashCode() : 0;
	}

	@Override
	public int compareTo(final NamedObject pObj) {
		return getName() != null ? getName().compareTo(pObj.getName()) : 0;
	}
}
