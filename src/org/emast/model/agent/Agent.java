package org.emast.model.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import static org.emast.model.agent.AgentState.*;
import org.emast.model.agent.behaviour.Individual;
import org.emast.model.agent.behaviour.individual.ChangeModel;
import org.emast.model.agent.behaviour.individual.reward.RewardBehaviour;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import org.emast.util.Utils;

/**
 *
 * @author Anderson
 */
public class Agent<M extends MDP> implements Algorithm<M, Plan> {

    private static final int MAX_ITERATIONS = 1000;
    private double totalReward;
    /**
     * Number that identifies the agent in the problem
     */
    private int number;
    private long msecs;
    private State currentState;
    private Plan plan;
    private AgentState executionState;
    private Policy policy;
    private List<Individual<M>> behaviours;

    public Agent(int pNumber) {
        this(pNumber, Collections.EMPTY_LIST);
    }

    public Agent(int pNumber, List<Individual<M>> pBehaviours) {
        number = pNumber;
        behaviours = pBehaviours;
        executionState = AgentState.INITIAL;
    }

    public List<Individual<M>> getBehaviours() {
        return behaviours;
    }

    @Override
    public Plan run(Problem<M> pProblem, Object... pParameters) {
        executionState = AgentState.RUNNING;
        policy = (Policy) pParameters[0];
        //run
        long initMsecs = System.currentTimeMillis();

        executionState = doRun(pProblem);

        msecs = System.currentTimeMillis() - initMsecs;

        return plan;
    }

    /**
     * Receive reward and go to the next state
     *
     * @param pNextState
     * @param pReward
     */
    private void changeState(State pNextState) {
        currentState = pNextState;
        print("changed state from " + currentState + " to " + pNextState);
    }

    public void addReward(State pNextState, double pReward) {
        //add received reward to total reward
        totalReward += pReward;
        print("received reward of: " + pReward + ". Total: " + totalReward);
    }

    private AgentState doRun(Problem<M> pProblem) {
        M model = pProblem.getModel();
        Action action;
        int iterations = 0;
        //get the number's initial state
        currentState = pProblem.getInitialStates().get(getNumber());
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
                    //go to next state
                    changeState(nextState);
                    //add reward to total reward
                    addReward(nextState, reward);
                    //run add reward behaviours
                    behave(RewardBehaviour.class, pProblem, "state", nextState, "reward", reward);
                    //run change model behaviours
                    behave(ChangeModel.class, pProblem, "state", nextState);
                } else {
                    currentState = null;
                }
                //count iterations
                iterations++;
                //save action in plan
                plan.add(action);
            }
            //while there is a valid action to execute and did not reach the max iteration
        } while (action != null && currentState != null && iterations < MAX_ITERATIONS);

        return iterations < MAX_ITERATIONS ? FINISHED : FINISHED_MAX_ITERATIONS;
    }

    private void behave(Class<? extends Individual> pClass, Problem problem, Object... pParameters) {
        behave(pClass, problem, CollectionsUtils.asStringMap(pParameters));
    }

    private void behave(Class<? extends Individual> pClass, Problem problem, Map<String, Object> pParameters) {
        for (final Individual<M> b : behaviours) {
            if (pClass.isAssignableFrom(b.getClass())) {
                b.behave(this, problem, pParameters);
            }
        }
    }

    @Override
    public String printResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAgent ").append(getNumber()).append(": ");
        sb.append("\n- Time: ").append(Utils.toTimeString(msecs)).append(" (").append(msecs).append(" ms)");
        sb.append("\n- Plan: ").append(getPlan());
        sb.append("\n- Reward: ").append(getTotalReward());

        return sb.toString();
    }

    public AgentState getAgentIteratorState() {
        return executionState;
    }

    public long getMsecs() {
        return msecs;
    }

    private Action getAction() {
        return policy.get(currentState);
    }

    public boolean isFinished() {
        return executionState.equals(FINISHED) || executionState.equals(FINISHED_MAX_ITERATIONS);
    }

    public Plan getPlan() {
        return plan;
    }

    public int getNumber() {
        return number;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy pPolicy) {
        policy = pPolicy;
    }

    private void print(String pMsg) {
        Log.info("Agent " + getNumber() + ": " + pMsg);
    }

    public double getTotalReward() {
        return totalReward;
    }

    public State getCurrentState() {
        return currentState;
    }
}
