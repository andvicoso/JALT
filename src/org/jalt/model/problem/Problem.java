package org.jalt.model.problem;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.state.State;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.FileUtils;
import org.jalt.util.Utils;
import org.jalt.util.grid.GridPrinter;

/**
 * 
 * @author andvicoso
 */
public class Problem<M extends MDP> implements Serializable {

	public static final int MAX_SIZE_PRINT = 500;
	public static final String PROB_EXT = ".jalt";
	private Map<Integer, State> initialStates;
	private Set<State> finalStates;
	private M model;

	public Problem(M model, Map<Integer, State> initialStates) {
		this(model, initialStates, Collections.<State> emptySet());
	}

	public Problem(Problem<M> pProb) {
		this(pProb.getModel(), pProb.getInitialStates(), pProb.getFinalStates());
	}

	public Problem(M pModel, Map<Integer, State> pInitialStates, Set<State> pFinalStates) {
		this.model = pModel;
		this.initialStates = pInitialStates;
		this.finalStates = pFinalStates;
	}

	public Problem(M pModel) {
		this.model = pModel;
	}

	public Map<Integer, State> getInitialStates() {
		return initialStates;
	}

	public M getModel() {
		return model;
	}

	public void save() {
		final String filename = getClass().getSimpleName() + Utils.toFileTimeString(System.currentTimeMillis()) + PROB_EXT;
		FileUtils.toObjectFile(this, filename, true);
	}

	@Override
	public String toString() {
		return print(null).toString();
	}

	private StringBuilder print(Object pResult) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\nInitial states: ").append(initialStates);
		if (finalStates != null && !finalStates.isEmpty()) {
			sb.append("\nFinal states (").append(DefaultTestProperties.FINAL_GOAL + "): ").append(finalStates);
		}

		if (model instanceof GridModel && model.getStates().size() < MAX_SIZE_PRINT) {
			sb.append("\nEnvironment: ");
			final GridPrinter gridPrinter = new GridPrinter();
			final String grid = gridPrinter.print((GridModel) model, initialStates, finalStates, pResult);
			sb.append("\n\n").append(grid);
		} else if (pResult != null) {
			final String resultName = pResult.getClass().getSimpleName();
			sb.append("\n").append(resultName).append(": \n").append(pResult);
		}

		return sb;
	}

	public String toString(Object pResult) {
		return print(pResult).toString();
	}

	public Set<State> getFinalStates() {
		return finalStates;
	}

	public void setFinalStates(Set<State> finalStates) {
		this.finalStates = finalStates;
	}
}
