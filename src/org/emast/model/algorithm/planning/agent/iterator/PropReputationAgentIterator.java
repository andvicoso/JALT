package org.emast.model.algorithm.planning.agent.iterator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class PropReputationAgentIterator<M extends ERG> extends ERGAgentIterator<M> {

    private double badRewardThreshold;
    private Map<Proposition, Double> localPropositionsReputation;

    public PropReputationAgentIterator(int pAgent, double pBadRewardThreshold) {
        super(pAgent);
        badRewardThreshold = pBadRewardThreshold;
        localPropositionsReputation = new HashMap<Proposition, Double>();
    }

    @Override
    protected void addReward(State pNextState, double pReward) {
        super.addReward(pNextState, pReward);
        //if this is a bad reward state,
        if (pNextState != null && isBadRewardState(pNextState, pReward)) {
            manageBadReward(pNextState, pReward);
        }
    }

    protected void manageBadReward(State pNextState, double pReward) {
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pNextState, pReward, localPropositionsReputation);
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pNextState)) {
            //get the new policy for the new preservation goal (if one exists)
            Policy p = changePreservationGoal(pNextState);
            //if found a policy
            if (p != null) {
                //changed preservation goal, continue iteration 
                //with the new preservation goal and policy
                setPolicy(p);
            }
        }
    }

    //TODO: define better what is a bad reward state (using state)
    private boolean isBadRewardState(State pState, double pReward) {
        return pReward < badRewardThreshold;
    }

    protected void savePropositionReputation(State pNextState,
            double pReward, Map<Proposition, Double> pPropositionsReputation) {
        if (pPropositionsReputation != null) {
            //bad reward value is distributed equally over the state`s propostions
            Collection<Proposition> props = getPropositionsForState(pNextState);
            double propReward = pReward / props.size();
            for (Proposition proposition : props) {
                Double currPropReward = pPropositionsReputation.get(proposition);
                currPropReward = currPropReward == null ? 0d : currPropReward;
                pPropositionsReputation.put(proposition, propReward + currPropReward);
            }
        }
    }

    protected Double getPropositionReputation(Proposition pProposition) {
        return localPropositionsReputation.get(pProposition);
    }

    protected Problem<M> cloneProblem(M model, Expression pNewPreservGoal) {
        M cloneModel = (M) model.copy();
        //set new preservation goal
        cloneModel.setPreservationGoal(pNewPreservGoal);
        //set the initial state only for the current agent
        //return a new problem with a new preservation goal and initial state
        return new Problem<M>(cloneModel, Collections.singletonMap(getAgent(), currentState));
    }

    public Map<Proposition, Double> getLocalPropositionsReputation() {
        return localPropositionsReputation;
    }

    protected Policy changePreservationGoal(State pState) {
        //save the goal
        Expression finalGoal = model.getGoal();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and the state
        Expression newPropsExp = getNewPreservationGoal(originalPreservGoal, pState);
        //copy the original preservation goal
        Expression newPreservGoal = new Expression(originalPreservGoal.toString());
        //and join them with an AND operator
        newPreservGoal.add(newPropsExp, BinaryOperator.AND);

        try {
            //compare previous goal with the newly created
            if (!newPreservGoal.equals(originalPreservGoal)
                    && !originalPreservGoal.contains(newPropsExp)
                    && !originalPreservGoal.contains(newPropsExp.negate())
                    && !model.getPropositionFunction().satisfies(pState, finalGoal)) {
                //TODO: Decide which propositions are giving a bad reward
                //create a new cloned problem
                Problem<M> newProblem = cloneProblem(model, newPreservGoal);
                //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
                Policy p = getAlgorithm().run(newProblem);
                //if there isn`t a path to reach the goal,
                if (canReachFinalGoal(newProblem)) {
                    //set the new preservation goal to the current problem
                    newProblem.getModel().setPreservationGoal(newPreservGoal);
                    //confirm the goal modification
                    print("changed preservation goal from {"
                            + originalPreservGoal + "} to {" + newPreservGoal + "}");
                    return p;
                }
            }
        } catch (EvaluationException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean canReachFinalGoal(Problem pProblem) {
        //create a new simple agent iterator
        AgentIterator iterator = new AgentIterator(getAgent());
        //find the plan for the newly created problem
        //with the preservation goal changed
        iterator.run(pProblem);
        //get the resulting plan
        Plan agPlan = iterator.getPlan();
        return agPlan != null && !agPlan.isEmpty();
    }

    private Expression getNewPreservationGoal(Expression pOriginalPreservGoal, State pState) {
        //get the "problematic" expression
        //i.e. the one that is giving the bad reward
        Expression newPropsExp =
                model.getPropositionFunction().getExpressionForState(pState);
        //negate it
        newPropsExp = newPropsExp.negate();

        return newPropsExp;
    }

    protected boolean mustChangePreservationGoal(State pState) {
        Proposition changedProp = null;
        Collection<Proposition> props = getPropositionsForState(pState);

        if (props != null) {
            for (Proposition proposition : props) {
                Double rep = getPropositionReputation(proposition);
                if (rep < badRewardThreshold) {
                    changedProp = proposition;
                    break;
                }
            }
        }

        return changedProp != null;
    }

    public Map<Proposition, Double> getPropositionsReputation() {
        return localPropositionsReputation;
    }
}
