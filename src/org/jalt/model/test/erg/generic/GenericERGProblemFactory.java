package org.jalt.model.test.erg.generic;

import static org.jalt.util.DefaultTestProperties.GOOD_REWARD;
import static org.jalt.util.DefaultTestProperties.BAD_REWARD;
import static org.jalt.util.DefaultTestProperties.OTHERWISE;

import java.util.ArrayList;
import java.util.Collections;
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

	private final int rows;
	private final int cols;
	private final int agents;
	private final double badReward;
	private final double otherwiseReward;
	private final int numberOfBadProps;
	private final int numberOfPropositions;
	private double goodReward;

	public static ProblemFactory createDefaultFactory() {
		return createDefaultFactory(1, 10);
	}

	public static ProblemFactory createDefaultFactory(int agents, int rows) {
		// double agentsRatio = 0.02;
		// agents = (int) Math.ceil(rows * cols * agentsRatio);
		int cols = rows;
		int props = 2;// (int) Math.ceil(rows / 5);
		int numberOfBadProps = 2;// (int) Math.ceil(rows / 5);

		return new GenericERGProblemFactory(rows, cols, agents, props, numberOfBadProps,
				BAD_REWARD, GOOD_REWARD, OTHERWISE);
	}

	public GenericERGProblemFactory(final int pRows, final int pCols, final int pAgents,
			final int pPropositions, final int pBadProps, final double pBadReward,
			final double pGoodReward, final double pOtherwiseReward) {
		rows = pRows;
		cols = pCols;
		agents = pAgents;
		numberOfPropositions = pPropositions;
		numberOfBadProps = pBadProps;
		badReward = pBadReward;
		goodReward = pGoodReward;
		otherwiseReward = pOtherwiseReward;
	}

	@Override
	public Problem<ERG> doCreate() {
		final GenericERGGridModel model = new GenericERGGridModel(rows, cols, agents,
				numberOfPropositions, numberOfBadProps, badReward, goodReward, otherwiseReward);
		final PropositionFunction pf = model.getPropositionFunction();
		State finalState = model.getFinalState();
		// create initial states
		final List<State> initStates = findInitialStates(model, agents, pf, finalState);

		return new Problem<ERG>(model, CollectionsUtils.asIndexMap(initStates),
				Collections.singleton(finalState));
	}

	private List<State> findInitialStates(GenericERGGridModel model, int agents,
			PropositionFunction pf, State finalState) {
		List<State> initialStates = new ArrayList<State>();
		for (int i = 0; i < agents; i++) {
			State state;
			do {
				state = CollectionsUtils.getRandom(model.getStates());
				Set<Proposition> propsState = pf.getPropositionsForState(state);
				if (!state.equals(finalState)
						&& ((propsState != null && !GenericERGGridModel.hasBadProp(propsState)) || propsState == null)) {
					break;
				}
			} while (true);
			initialStates.add(state);
		}
		return initialStates;
	}

	
}
