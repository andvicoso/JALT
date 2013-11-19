package org.emast.model.agent;

import java.util.Map;

import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 * 
 * @author Anderson
 */
public class AgentIterator<M extends MDP> implements Algorithm<M, Plan> {

	private double totalReward;
	/**
	 * Number that identifies the agent in the problem
	 */
	protected int agent;
	protected State state;
	protected Plan plan;
	protected Policy policy;
	protected int iterations;

	public AgentIterator() {
		this(0);
	}

	public AgentIterator(int pAgent) {
		agent = pAgent;
	}

	/**
	 * Receive reward and go to the next state
	 * 
	 * @param pNextState
	 * @param pReward
	 */
	protected void changeState(State pNextState) {
		print("changed state from " + state + " to " + pNextState);
		state = pNextState;
	}

	public void addReward(State pNextState, double pReward) {
		// add received reward to total reward
		totalReward += pReward;
		print("received reward of: " + pReward + ". Total: " + totalReward);
	}

	@Override
	public Plan run(Problem<M> pProblem, Map<String, Object> pParameters) {
		policy = (Policy) pParameters.get("policy");
		M model = pProblem.getModel();
		Action action;
		iterations = 0;
		// get the number's initial state
		state = pProblem.getInitialStates().get(getAgent());
		// create a plan for number
		plan = new Plan();
		// main loop
		do {
			// get the valid action associated with the state
			action = getAction();
			// if has somewhere to go to
			if (action != null) {
				// get the state that the action points to
				State nextState = model.getTransitionFunction().getBestReachableState(
						model.getStates(), state, action);
				// is there a state pointed by the action?
				if (nextState != null) {
					double reward = model.getRewardFunction().getValue(state, action);
					// go to next state
					changeState(nextState);
					// add reward to total reward
					addReward(nextState, reward);
				} else {
					state = null;
				}
				// count iterations
				iterations++;
				// save action in plan
				plan.add(action);
			}
			// while there is a valid action to execute and did not reach the max iteration
		} while (action != null && state != null);

		return plan;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nAgent ").append(getAgent()).append(": ");
		sb.append("\n- Plan: ").append(getPlan());
		sb.append("\n- Reward: ").append(getTotalReward());

		return sb.toString();
	}

	protected Action getAction() {
		Map<Action, Double> values = policy.get(state);
		return values == null ? null : CollectionsUtils.draw(values);
	}

	public Plan getPlan() {
		return plan;
	}

	public int getAgent() {
		return agent;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy pPolicy) {
		policy = pPolicy;
	}

	public double getTotalReward() {
		return totalReward;
	}

	public State getCurrentState() {
		return state;
	}

	protected void print(String pMsg) {
		Log.info("Agent " + getAgent() + ": " + pMsg);
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

}
