package org.jalt.model.test.erg.generic;

import static org.jalt.util.DefaultTestProperties.BAD_REWARD;
import static org.jalt.util.DefaultTestProperties.OTHERWISE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.problem.Problem;
import org.jalt.model.problem.ProblemFactory;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 * 
 * @author And
 */
public class GenericERGProblemFactory extends ProblemFactory {

	public static final double CHANCE_OF_HAVING_PROP = 0.3;
	public static final int MAX_PROPS_PER_STATE = 4;
	private final int rows;
	private final int cols;
	private final int agents;
	private final double badReward;
	private final double otherwiseReward;
	private final int numberOfBadProps;
	private final int numberOfPropositions;

	public static ProblemFactory createDefaultFactory() {
		return createDefaultFactory(1);
	}

	public static ProblemFactory createDefaultFactory(int agents) {
		// double agentsRatio = 0.02;
		// agents = (int) Math.ceil(rows * cols * agentsRatio);
		int rows = 10;
		int cols = rows;
		int props = (int) Math.ceil(rows / 5);
		int numberOfBadProps = (int) Math.ceil(rows / 5);

		return new GenericERGProblemFactory(rows, cols, agents, props, numberOfBadProps,
				BAD_REWARD, OTHERWISE);
	}

	public GenericERGProblemFactory(final int pRows, final int pCols, final int pAgents,
			final int pPropositions, final int pBadProps, final double pBadReward,
			final double pOtherwiseReward) {
		rows = pRows;
		cols = pCols;
		agents = pAgents;
		numberOfPropositions = pPropositions;
		numberOfBadProps = pBadProps;
		badReward = pBadReward;
		otherwiseReward = pOtherwiseReward;
	}

	@Override
	public Problem<ERG> doCreate() {
		final GenericERGProblem model = new GenericERGProblem(rows, cols, agents,
				numberOfPropositions, numberOfBadProps, badReward, otherwiseReward);
		final PropositionFunction pf = new PropositionFunction();
		model.setPropositionFunction(pf);
		spreadPropositions(model, pf);
		// put final goal over the grid in a state that doesn`t have a bad
		// rewarder
		State finalState = findFinalState(model, pf);
		pf.add(finalState, model.getFinalProp());
		// create initial states
		final List<State> initStates = findInitialStates(model, agents, pf, finalState);

		return new Problem<ERG>(model, CollectionsUtils.asIndexMap(initStates),
				Collections.singleton(finalState));
	}

	private void spreadPropositions(final ERG model, final PropositionFunction pf) {
		// spread propositions over the grid
		for (State s : model.getStates()) {
			// chance of having some props
			if (CHANCE_OF_HAVING_PROP > Math.random()) {
				for (int i = 0; i < Math.random() * MAX_PROPS_PER_STATE; i++) {
					Proposition prop = CollectionsUtils.getRandom(model.getPropositions());
					Set<Proposition> sprops = pf.getPropositionsForState(s);
					if (sprops == null) {
						sprops = new HashSet<Proposition>();
					}
					sprops.add(prop);
					pf.set(s, sprops);
				}
			}
		}
	}

	private State findFinalState(final ERG model, final PropositionFunction pf) {
		State finalState;
		do {
			finalState = CollectionsUtils.getRandom(model.getStates());
			Set<Proposition> propsState = pf.getPropositionsForState(finalState);
			if ((propsState != null && !hasBadProp(propsState)) || propsState == null) {
				break;
			}
		} while (true);
		return finalState;
	}

	private List<State> findInitialStates(GenericERGProblem model, int agents,
			PropositionFunction pf, State finalState) {
		List<State> initialStates = new ArrayList<State>();
		for (int i = 0; i < agents; i++) {
			State state;
			do {
				state = CollectionsUtils.getRandom(model.getStates());
				Set<Proposition> propsState = pf.getPropositionsForState(state);
				if (!state.equals(finalState)
						&& ((propsState != null && !hasBadProp(propsState)) || propsState == null)) {
					break;
				}
			} while (true);
			initialStates.add(state);
		}
		return initialStates;
	}

	private boolean hasBadProp(Set<Proposition> propsState) {
		if (propsState != null) {
			for (Proposition prop : propsState) {
				char v = prop.getName().charAt(0);
				if (Character.isUpperCase(v)) {
					return true;
				}
			}
		}
		return false;
	}
}
