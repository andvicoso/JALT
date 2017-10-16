package org.jalt.model.problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jalt.model.model.MDP;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public abstract class ProblemFactory<M extends MDP> {

	private List<State> usedStates;
	private static final Random random = new Random();

	public ProblemFactory() {
	}

	protected State getRandomEmptyState(final M pModel) {
		State st;
		do {
			st = getRandomState(pModel);
		} while (usedStates.contains(st));
		// save state as already used
		usedStates.add(st);

		return st;
	}

	public static List<State> getRandomStates(final MDP pModel, int pNum) {
		final List<State> states = new ArrayList<State>();
		for (int i = 0; i < pNum; i++) {
			states.add(getRandomState(pModel));
		}
		return states;
	}

	public List<State> getRandomEmptyStates(final M pModel, int pNum) {
		final List<State> states = new ArrayList<State>(pNum);
		for (int i = 0; i < pNum; i++) {
			states.add(getRandomEmptyState(pModel));
		}
		return states;
	}

	public static State getRandomState(final MDP pModel) {
		final int index = random.nextInt(pModel.getStates().size());
		return (State) pModel.getStates().toArray()[index];
	}

	protected abstract Problem<M> doCreate();

	public Problem<M> create() {
		usedStates = new ArrayList<State>();
		return doCreate();
	}
}
