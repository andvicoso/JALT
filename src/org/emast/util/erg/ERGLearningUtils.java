package org.emast.util.erg;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.emast.model.action.Action;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.solution.SinglePolicy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import org.emast.util.ModelUtils;

public class ERGLearningUtils {

	public static ERG createModel(ERG oldModel, ERGQTable q, Set<Expression> avoid) {
		ERG model = ModelUtils.createModel(oldModel, q);
		model.setGoal(oldModel.getGoal());
		// GET THE SET OF PROPOSITIONS FROM EXPLORATED STATES
		model.setPropositions(getPropositions(q.getExpsValues()));
		// CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
		Expression newPreservGoal = createNewPreservationGoal(oldModel.getPreservationGoal(), avoid);
		model.setPreservationGoal(newPreservGoal);
		// CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE)
		PropositionFunction pf = ERGFactory.createPropositionFunction(q);
		model.setPropositionFunction(pf);

		return model;
	}

	private static Set<Proposition> getPropositions(Map<Expression, Double> expsValues) {
		Set<Proposition> props = new HashSet<Proposition>();
		for (Expression exp : expsValues.keySet()) {
			Set<Proposition> expProps = exp.getPropositions();
			props.addAll(expProps);
		}

		return props;
	}

	private static Expression createNewPreservationGoal(Expression pCurrent, Set<Expression> pAvoid) {
		Expression badExp = new Expression(BinaryOperator.OR, pAvoid.toArray(new Expression[pAvoid
				.size()]));
		return pCurrent.and(badExp.parenthesize().negate());
	}

	public static SinglePolicy optmize(Policy policy, ERGQTable q) {
		SinglePolicy single = new SinglePolicy();
		for (Map.Entry<State, Map<Action, Double>> entry : policy.entrySet()) {
			State state = entry.getKey();
			Action bestAction = getBestAction(entry.getValue(), q.getDoubleValues(state));
			single.put(state, bestAction);
		}

		return single;
	}

	public static Action getBestAction(Map<Action, Double> policy, Map<Action, Double> q) {
		Collection<Action> best = getBestAction(policy, policy.keySet());
		if (policy.size() > 1) {
			Collection<Action> bestq = getBestAction(q, policy.keySet());
			if (bestq.size() > 1) {
				best = bestq;
			}
		}

		return CollectionsUtils.getRandom(best);
	}

	public static Collection<Action> getBestAction(Map<Action, Double> map, Set<Action> keySet) {
		Map<Action, Double> temp = new HashMap<Action, Double>(map);
		for (Action action : map.keySet()) {
			if (!keySet.contains(action)) {
				temp.remove(action);
			}
		}

		Double max = Collections.max(temp.values());
		return CollectionsUtils.getKeysForValue(temp, max);
	}

	private ERGLearningUtils() {
	}

}
