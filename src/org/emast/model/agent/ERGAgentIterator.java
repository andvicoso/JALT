package org.emast.model.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.algorithm.table.PropTable;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Plan;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class ERGAgentIterator<M extends ERG> extends AgentIterator<M> {

    private List<Individual<M>> behaviors;
    private PropTable propTable;
    private QTable qTable;

    public ERGAgentIterator(int pNumber) {
        this(pNumber, Collections.EMPTY_LIST);
    }

    public ERGAgentIterator(int pAgent, List<Individual<M>> pBehaviors) {
        super(pAgent);
        behaviors = pBehaviors;
    }

    public List<Individual<M>> getBehaviors() {
        return behaviors;
    }

    @Override
    public Plan run(Problem<M> pProblem, Object... pParameters) {
        M model = pProblem.getModel();
        propTable = new PropTable(model.getStates(), model.getPropositions());
        qTable = new QTable(model.getStates(), model.getActions());
        Action action;

        for (int i = 0; i < 10; i++) {
            iterations = 0;
            //get the number's initial state
            state = pProblem.getInitialStates().get(getAgent());
            //create a plan for number
            plan = new Plan();
            //main loop
            do {
                //get the valid action associated with the state
                action = getAction();
                //if has somewhere to go to
                if (action != null) {
                    //get the state that the action points to
                    State nextState = model.getTransitionFunction().getBestReachableState(
                            model.getStates(), state, action);
                    //is there a state pointed by the action?
                    if (nextState != null) {
                        double reward = model.getRewardFunction().getValue(state, action);
                        //update tables
                        updateQTable(model, state, action, reward, nextState);
                        updatePropTable(model, nextState, reward);
                        //go to next state
                        changeState(nextState);
                        //add reward to total reward
                        addReward(nextState, reward);
                        //run add reward behaviors
                        //behave(RewardBehavior.class, pProblem, "state", nextState, "reward", reward);
                        //run change model behaviors
                        //behave(ChangeModel.class, pProblem, "state", nextState);
                    } else {
                        state = null;
                    }
                    //save action in plan
                    plan.add(action);
                }
                //while there is a valid action to execute and did not reach the max iteration
            } while (action != null && state != null);
        }

        print(propTable.getPropValue().toString());
        print(qTable.toString());

        return plan;
    }

    protected double getMax(MDP pModel, State pState) {
        Double max = null;

        Collection<Action> actions = pModel.getTransitionFunction().getActionsFrom(pModel.getActions(), pState);
        // search for the Q v for each state
        for (Action action : actions) {
            Double value = qTable.get(pState, action);
            if (max == null || value > max) {
                max = value;
            }
        }

        if (max == null) {
            max = 0d;
        }

        return max;
    }

    private void updateQTable(MDP pModel, State state, Action action, double reward, State nextState) {
        //get current q value
        double cq = qTable.get(state, action);
        //get new q value
        double value = reward + (0.9 * getMax(pModel, nextState)) - cq;
        double newq = cq + 0.5 * value;
        //save q
        qTable.put(state, action, newq);
    }

    private void updatePropTable(ERG pModel, State pNextState, double pReward) {
        //bad reward value is distributed equally over the state`s propostions
        Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pNextState);
        if (props != null) {
            double propReward = pReward / props.size();
            for (Proposition proposition : props) {
                propTable.put(pNextState, proposition, propReward);
            }
        }
    }

    private void behave(Class<? extends Individual> pClass, Problem problem, Object... pParameters) {
        behave(pClass, problem, CollectionsUtils.asStringMap(pParameters));
    }

    private void behave(Class<? extends Individual> pClass, Problem problem, Map<String, Object> pParameters) {
        for (final Individual<M> b : behaviors) {
            if (pClass.isAssignableFrom(b.getClass())) {
                b.behave(this, problem, pParameters);
            }
        }
    }

    public PropTable getPropTable() {
        return propTable;
    }

    public QTable getQTable() {
        return qTable;
    }
}
