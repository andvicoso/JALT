package org.emast.model.agent;

import java.util.Collection;
import java.util.Collections;
import org.emast.model.exception.InvalidExpressionException;
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
public class ChangePreservGoalPropRepAgent<M extends ERG> extends PropReputationAgent<M> {

    public ChangePreservGoalPropRepAgent(int pAgent, double pBadRewardThreshold) {
        super(pAgent, pBadRewardThreshold);
    }

    @Override
    protected void manageBadReward(State pNextState, double pReward) {
        super.manageBadReward(pNextState, pReward);
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pNextState)) {
            try {
                //get the new policy for the new preservation goal (if one exists)
                Policy p = changePreservationGoal(pNextState);
                //if found a policy
                if (p != null) {
                    //changed preservation goal, continue iteration 
                    //with the new preservation goal and policy
                    setPolicy(p);
                }
            } catch (InvalidExpressionException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected Problem<M> cloneProblem(M model, Expression pNewPreservGoal) {
        M cloneModel = (M) model.copy();
        //set new preservation goal
        cloneModel.setPreservationGoal(pNewPreservGoal);
        //set the initial state only for the current number
        //return a new problem with a new preservation goal and initial state
        return new Problem<M>(cloneModel, Collections.singletonMap(getAgent(), currentState));
    }

    protected Policy changePreservationGoal(State pState) throws InvalidExpressionException {
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
        return null;
    }

    private boolean canReachFinalGoal(Problem pProblem) {
        //create a new simple agent
        Agent agent = new Agent(getAgent());
        agent.setPolicy(getPolicy());
        //find the plan for the newly created problem
        //with the preservation goal changed
        agent.run(pProblem);
        //get the resulting plan
        Plan agPlan = agent.getPlan();

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
}
