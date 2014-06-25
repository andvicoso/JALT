package org.jalt.util.erg;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.model.ERG;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.propositional.operator.BinaryOperator;
import org.jalt.model.solution.Policy;
import org.jalt.model.solution.SinglePolicy;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.ModelUtils;

public class ERGLearningUtils {

	public static ERG createModel(ERG oldModel, ERGQTable q, Set<Expression> avoid) {
		ERG model = ModelUtils.createModel(oldModel, q);
		model.setGoal(oldModel.getGoal());
		// GET THE SET OF PROPOSITIONS FROM EXPLORATED STATES
		model.setPropositions(oldModel.getPropositions());
		// CREATE NEW PRESERVATION GOAL FROM EXPRESSIONS THAT SHOULD BE AVOIDED
		Expression newPreservGoal = createNewPreservationGoal(oldModel.getPreservationGoal(), avoid);
		model.setPreservationGoal(newPreservGoal);
		// CREATE NEW PROPOSITION FUNCTION FROM AGENT'S EXPLORATION (Q TABLE) AND PREVIOUS(BLOCKED)
		//PropositionFunction pf = //ERGFactory.createPropositionFunction(model, q, oldModel.getPropositionFunction());
		model.setPropositionFunction(oldModel.getPropositionFunction());

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
