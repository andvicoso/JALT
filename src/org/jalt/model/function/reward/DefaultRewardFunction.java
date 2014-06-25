package org.jalt.model.function.reward;

import java.io.Serializable;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.table.DoubleTable;
import org.jalt.model.model.MDP;
import org.jalt.model.state.State;

/**
 * 
 * @author andvicoso
 */
public class DefaultRewardFunction<M extends MDP, C> implements RewardFunction, Serializable {
	protected DoubleTable table;
	private final M model;
	private final Map<C, Double> rewards;
	private final double otherwiseValue;

	public DefaultRewardFunction(M pModel, Map<C, Double> pRewardValues, double pOtherwiseValue) {
		model = pModel;
		rewards = pRewardValues;
		otherwiseValue = pOtherwiseValue;
		table = new DoubleTable(pModel.getStates(), pModel.getActions());
	}

	public Map<C, Double> getRewards() {
		return rewards;
	}

	public double getOtherwiseValue() {
		return otherwiseValue;
	}

	public M getModel() {
		return model;
	}

	@Override
	public double getValue(final State pState, final Action pAction) {
		return table.get(pState, pAction);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getRewards());
		sb.append(", otherwise=").append(getOtherwiseValue());
		return sb.toString();
	}
}
