package org.jalt.test.erg.rover;

import java.util.Set;

import org.jalt.model.function.reward.RewardFunctionProposition;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.util.CollectionsUtils;

import static org.jalt.util.DefaultTestProperties.*;

/**
 *
 * @author andvicoso
 */
public class RoverModel extends ERGGridModel {

	public RoverModel(final int pRows, final int pCols) {
		super(pRows, pCols);
		// set propositions
		setPropositions(getDefaultPropositions());
		// set goals
		setPreservationGoal(createPreservationGoal());
		setGoal(createFinalGoal());
		// set bad reward function
		setRewardFunction(new RewardFunctionProposition(this, CollectionsUtils.createMap(getBadRewardObstacles(), BAD_REWARD), OTHERWISE));
	}

	public static Expression createFinalGoal() {
		return new Expression("exit");
	}

	public static Expression createPreservationGoal() {
		return new Expression("!hole & !stone");
	}

	public static Set<Proposition> getBadRewardObstacles() {
		String[] props = { "water", "oil" };
		return CollectionsUtils.createSet(Proposition.class, props);
	}

	public static Set<Proposition> getObstacles() {
		String[] props = { "hole", "stone", "water", "oil" };
		return CollectionsUtils.createSet(Proposition.class, props);
	}

	public static Set<Proposition> getDefaultPropositions() {
		String[] props = { "hole", "stone", "water", "oil", "exit" };
		return CollectionsUtils.createSet(Proposition.class, props);
	}
}
