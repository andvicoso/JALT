package org.emast.model.algorithm.planning.agent.iterator;

import java.io.PrintStream;
import java.util.Collection;
import org.emast.model.action.Action;
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
public class AgentIterator<M extends MDP> implements Runnable, Algorithm<M, Plan> {

    private static final PrintStream DEBUG_WRITER = System.out;
    protected static final int MAX_ITERATIONS = 1000;
    private static final boolean DEBUG = true;
    protected double totalReward;
    protected State currentState;
    protected Policy policy;
    protected Plan plan;
    protected final int agent;
    private long msecs;
    private AgentIteratorState itState = AgentIteratorState.INITIAL;
    private final M model;
    private final State initialState;

    public AgentIterator(final M pModel, final Policy pInitialPolicy,
            final int pAgent, final State pInitialState) {
        agent = pAgent;
        model = pModel;
        policy = pInitialPolicy;
        initialState = pInitialState;
    }

    public AgentIteratorState getAgentIteratorState() {
        return itState;
    }

    public long getMsecs() {
        return msecs;
    }

    @Override
    public void run() {
        run(null);
    }

    @Override
    public Plan run(Problem<M> pProblem) {
        long initMsecs = System.currentTimeMillis();

        itState = doRun();

        msecs = System.currentTimeMillis() - initMsecs;

        return plan;
    }

    public M getModel() {
        return model;
    }

    public Plan getPlan() {
        return plan;
    }

    protected void setPlan(final Plan plan) {
        this.plan = plan;
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

    protected void print(final String pMsg) {
        if (DEBUG) {
            DEBUG_WRITER.println("Agent " + getAgent() + " " + pMsg);
        }
    }

    protected static boolean isDebug() {
        return DEBUG;
    }

    public double getTotalReward() {
        return totalReward;
    }

    public State getInitialState() {
        return initialState;
    }

    public State getCurrentState() {
        return currentState;
    }

    /**
     * Receive reward and go to the next state
     *
     * @param pNextState
     * @param pReward
     */
    protected void changeState(final State pNextState, final Action pAction) {
        print("changed state from " + currentState + " to " + pNextState);
        //and go to it
        currentState = pNextState;
    }

    protected void addReward(State pNextState, double pReward) {
        //add current reward to total reward
        if (pReward != 0) {
            totalReward += pReward;
            print("received reward of: " + pReward + ". Total: " + totalReward);
        }
    }

    protected AgentIteratorState doRun() {
        Action action;
        int count = 0;
        //get the agent's initial state
        currentState = getInitialState();
        //create a plan for agent
        plan = new Plan();
        //main loop
        do {
            //get the valid action associated with the state
            action = getAction();
            //if has somewhere to go to
            if (action != null) {
                //get the states that the action points to
                final Collection<State> nextStates =
                        model.getTransitionFunction().getFinalStates(model.getStates(), currentState, action);
                //is there a state pointed by the action?
                if (nextStates != null && !nextStates.isEmpty()) {
                    final double reward = getModel().getRewardFunction().getValue(currentState, action);
                    State nextState = nextStates.iterator().next();
                    //add reward to total reward
                    addReward(nextState, reward);
                    //change to next state
                    changeState(nextState, action);
                } else {
                    currentState = null;
                }
                count++;
            }
            //while there is a valid state to go to and did not reach the max iteration
        } while (action != null && currentState != null && count < MAX_ITERATIONS);

        return count < MAX_ITERATIONS
                ? AgentIteratorState.FINISHED
                : AgentIteratorState.FINISHED_MAX_ITERATIONS;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nAgent ").append(getAgent()).append(": ");
        sb.append("\n- Time: ").append(Utils.toTimeString(msecs)).append(" (").append(msecs).append(" ms)");
        sb.append("\n- Plan: ").append(getPlan());
        sb.append("\n- Reward: ").append(getTotalReward());

        return sb.toString();
    }

    protected Action getAction() {
        return policy.get(currentState);
    }

    public boolean isFinished() {
        return itState.equals(AgentIteratorState.FINISHED)
                || itState.equals(AgentIteratorState.FINISHED_MAX_ITERATIONS);
    }
}
