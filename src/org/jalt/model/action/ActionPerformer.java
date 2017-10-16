package org.jalt.model.action;

public interface ActionPerformer<A extends Action> {

	void execute(A action);
}
