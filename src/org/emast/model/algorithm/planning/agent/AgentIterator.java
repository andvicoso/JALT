package org.emast.model.algorithm.planning.agent;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.emast.model.action.Action;
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
public class AgentIterator<M extends MDP> implements Runnable {

    protected static final int MAX_ITERATIONS = 1000;
    private static final boolean DEBUG = true;
    protected double totalReward;
    protected State currentState;
    protected Policy policy;
    protected Plan plan;
    protected final int agent;
    private long msecs;
    private IterationState itState = IterationState.INITIAL;
    private final M model;
    private final State initialState;

    public AgentIterator(final M pModel, final Policy pInitialPolicy,
            final int pAgent, final State pInitialState) {
        agent = pAgent;
        model = pModel;
        policy = pInitialPolicy;
        initialState = pInitialState;
    }

    public IterationState getIterationState() {
        return itState;
    }

    public long getMsecs() {
        return msecs;
    }

    @Override
    public void run() {
        long initMsecs = System.currentTimeMillis();

        itState = doRun();

        msecs = System.currentTimeMillis() - initMsecs;
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

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    protected void print(final String string) {
        if (DEBUG) {
            System.out.println("Agent " + getAgent() + " " + string);
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

    protected IterationState doRun() {
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
                ? IterationState.FINISHED
                : IterationState.FINISHED_MAX_ITERATIONS;
    }

    public void printResult(PrintStream out) {
        out.println("Agent " + getAgent() + ": ");
        out.println("- Time: " + Utils.toTimeString(msecs) + "(" + msecs + " ms)");
        out.println("- Plan: " + getPlan());
        out.println("- Reward: " + getTotalReward());
    }

    protected Action getAction() {
        return policy.get(currentState);
    }

    public static enum IterationState {

        INITIAL, RUNNING, FINISHED, FINISHED_MAX_ITERATIONS, ERROR;
    }

    public boolean isFinished() {
        return itState.equals(AgentIterator.IterationState.FINISHED)
                || itState.equals(AgentIterator.IterationState.FINISHED_MAX_ITERATIONS);
    }

    public static class DefaultAgentIteratorFactory<M extends MDP> implements AgentIteratorsFactory<M> {

        @Override
        public List<AgentIterator> createAgentIterators(Problem<M> pProblem, Policy pInitialPolicy) {
            final List<AgentIterator> iterators = new ArrayList<AgentIterator>();
            final M model = pProblem.getModel();
            //for each agent, create an agent planner
            for (int i = 0; i < model.getAgents().size(); i++) {
                final State initialState = pProblem.getInitialStates().get(i);
                //create an agent iterator for each agent
                final AgentIterator ap = createAgentIterator(model, pInitialPolicy, i, initialState);
                //save them
                iterators.add(ap);
            }

            return iterators;
        }

        @Override
        public AgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
            return new AgentIterator(pModel, pPolicy, pAgent, pInitialState);
        }
    }
}
