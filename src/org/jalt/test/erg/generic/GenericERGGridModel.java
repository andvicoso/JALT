package org.jalt.test.erg.generic;

import static org.jalt.util.DefaultTestProperties.FINAL_GOAL;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunctionProposition;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 * 
 * @author andvicoso
 */
public class GenericERGGridModel extends ERGGridModel {
	private static final char FIRST_PROP = 'a';
	public static final double CHANCE_OF_HAVING_PROP = 0.25;
	private Set<State> finalStates;

	public GenericERGGridModel(final int pRows, final int pCols, final int pAgents, final int pPropositions, final int pObstacles, final double pBadReward,
			final double pGoodReward, double pOtherwise) {
		super(pRows, pCols);
		// set props
		Set<Proposition> badRewarders = new HashSet<Proposition>(pObstacles);
		Set<Proposition> props = new HashSet<Proposition>(pPropositions);
		setPropositions(props);
		fillPropsAndBadRewarders(pPropositions, pObstacles, props, badRewarders);

		final PropositionFunction pf = new PropositionFunction();
		setPropositionFunction(pf);
		spreadPropositions();
		// final goal
		Proposition finalProp = new Proposition(FINAL_GOAL);
		setGoal(new Expression(finalProp));
		// put final goal over the grid in a state that doesn`t have a bad
		// rewarder
		finalStates = findFinalStates(pf, pAgents);// TODO: number of agents == number of final states

		pf.add(finalStates, finalProp);
		// add bad reward to bad prop
		Map<Proposition, Double> rws = CollectionsUtils.createMap(badRewarders, pBadReward);
		// add good reward to final prop
		rws.put(finalProp, pGoodReward);

		RewardFunctionProposition<GenericERGGridModel> rf = new RewardFunctionProposition<GenericERGGridModel>(this, rws, pOtherwise);
		// set two random propositions as preservation goal
		// setPreservationGoal(new Expression(BinaryOperator.AND,
		// badRewarders).negate());
		// set bad reward function

		setRewardFunction(rf);
	}

	public Set<State> getFinalStates() {
		return finalStates;
	}

	private Set<State> findFinalStates(PropositionFunction pf, int size) {
		Set<State> states = new HashSet<State>(size);
		do {
			State finalState = CollectionsUtils.getRandom(getStates());
			Set<Proposition> propsState = pf.getPropositionsForState(finalState);
			if ((propsState != null && !hasBadProp(propsState)) || propsState == null) {
				states.add(finalState);
			}
		} while (states.size() < size);
		return states;
	}

	private void fillPropsAndBadRewarders(final int pPropositions, final int pObstacles, Set<Proposition> props, Set<Proposition> badRewarders) {
		char initProp = FIRST_PROP;
		for (int i = 0; i < pPropositions; i++) {
			char c = (char) (initProp + i);
			String cs = c + "";
			if (i < pObstacles) {
				cs = cs.toUpperCase();
			}

			final Proposition proposition = new Proposition(cs);
			props.add(proposition);

			if (i < pObstacles) {
				badRewarders.add(proposition);
			}
		}
	}

	private void spreadPropositions() {
		// spread propositions over the grid
		for (State s : getStates()) {
			// chance of having some props
			int propsize = getPropositions().size();
			if (CHANCE_OF_HAVING_PROP > Math.random()) {
				for (int i = 0; i < Math.random() * propsize; i++) {
					Proposition prop = CollectionsUtils.getRandom(getPropositions());
					Set<Proposition> sprops = getPropositionFunction().getPropositionsForState(s);
					if (sprops == null) {
						sprops = new HashSet<Proposition>();
					}
					sprops.add(prop);
					getPropositionFunction().set(s, sprops);
				}
			}
		}
	}

	public static boolean hasBadProp(Set<Proposition> propsState) {
		if (propsState != null) {
			for (Proposition prop : propsState) {
				if (isBadProp(prop))
					return true;
			}
		}
		return false;
	}

	public static boolean isBadProp(Proposition prop) {
		char v = prop.getName().charAt(0);
		if (Character.isUpperCase(v)) {
			return true;
		}
		return false;
	}

}