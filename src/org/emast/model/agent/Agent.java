package org.emast.model.agent;

import java.util.Collection;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import static org.emast.model.agent.AgentState.*;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.Utils;

/**
 *
 * @author Anderson
 */
public class Agent<M extends MDP> implements Algorithm<M, Plan> {

    protected static int MAX_ITERATIONS = 1000;
    protected double totalReward;
    protected int number;
    private long msecs;
    protected State currentState;
    protected Plan plan;
    private AgentState itState;
    protected M model;
    private Policy policy;

    public Agent(int pNumber) {
        number = pNumber;
    }

    public void init(Problem<M> pProblem, Policy pPolicy) {
        itState = AgentState.INITIAL;
        model = pProblem.getModel();
        policy = pPolicy;
    }

    @Override
    public Plan run(Problem<M> pProblem) {
        long initMsecs = System.currentTimeMillis();

        itState = doRun(pProblem);

        msecs = System.currentTimeMillis() - initMsecs;

        return plan;
    }

    /**
     * Receive reward and go to the next state
     *
     * @param pNextState
     * @param pReward
     */
    protected void changeState(State pNextState, Action pAction) {
        print("changed state from " + currentState + " to " + pNextState);
        //and go to it
        currentState = pNextState;
    }

    protected void addReward(State pNextState, double pReward) {
        //add current reward to total reward
        totalReward += pReward;
        print("received reward of: " + pReward + ". Total: " + totalReward);
    }

    protected AgentState doRun(Problem<M> pProblem) {
        Action action;
        int iterations = 0;
        //get the number's initial state
        currentState = pProblem.getInitialStates().get(getAgent());
        //create a plan for number
        plan = new Plan();
        //main loop
        do {
            //get the valid action associated with the state
            action = getAction();
            //if has somewhere to go to
            if (action != null) {
                //get the states that the action points to
                Collection<State> nextStates = model.getTransitionFunction().getFinalStates(
                        model.getStates(), currentState, action);
                //is there a state pointed by the action?
                if (nextStates != null && !nextStates.isEmpty()) {
                    double reward = model.getRewardFunction().getValue(currentState, action);
                    State nextState = nextStates.iterator().next();//TODO:
                    //add reward to total reward
                    addReward(nextState, reward);
                    //change to next state
                    changeState(nextState, action);
                } else {
                    currentState = null;
                }
                //count iterations
                iterations++;
                //save action in plan
                plan.add(action);
            }
            //while there is a valid state to go to and did not reach the max iteration
        } while (action != null && currentState != null && iterations < MAX_ITERATIONS);

        print(printResults());

        return iterations < MAX_ITERATIONS ? FINISHED : FINISHED_MAX_ITERATIONS;
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAgent ").append(getAgent()).append(": ");
        sb.append("\n- Time: ").append(Utils.toTimeString(msecs)).append(" (").append(msecs).append(" ms)");
        sb.append("\n- Plan: ").append(getPlan());
        sb.append("\n- Reward: ").append(getTotalReward());

        return sb.toString();
    }

    public AgentState getAgentIteratorState() {
        return itState;
    }

    public long getMsecs() {
        return msecs;
    }

    protected Action getAction() {
        return policy.get(currentState);
    }

    public boolean isFinished() {
        return itState.equals(FINISHED) || itState.equals(FINISHED_MAX_ITERATIONS);
    }

    public Plan getPlan() {
        return plan;
    }

    protected void setPlan(Plan pPlan) {
        this.plan = pPlan;
    }

    public int getAgent() {
        return number;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy pPolicy) {
        policy = pPolicy;
    }

    protected void print(String pMsg) {
        Log.info("Agent " + getAgent() + " " + pMsg);
    }

    public double getTotalReward() {
        return totalReward;
    }

    public State getCurrentState() {
        return currentState;
    }
}
