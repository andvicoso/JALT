package org.emast.model.agent.behaviour.changemodel;

import java.util.Collection;
import java.util.Collections;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.ValidPlanFinder;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.BehaviourUtil;

/**
 *
 * @author Anderson
 */
public class ChangePreservGoalBehaviour<M extends ERG> implements ChangeModelBehaviour<M> {

    @Override
    public void changeModel(Agent pAgent, M pModel, State pState) {
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pModel, pState)) {
            try {
                //get the new policy for the new preservation goal (if one exists)
                Policy p = changePreservationGoal(pAgent, pModel, pState);
                //if found a policy
                if (p != null) {
                    //changed preservation goal, continue iteration 
                    //with the new preservation goal and policy
                    pAgent.setPolicy(p);
                }
            } catch (InvalidExpressionException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected Problem<M> cloneProblem(M pModel, State pState, int pAgent, Expression pNewPreservGoal) {
        M cloneModel = (M) pModel.copy();
        //set new preservation goal
        cloneModel.setPreservationGoal(pNewPreservGoal);
        //set the initial state only for the current number
        //return a new problem with a new preservation goal and initial state
        return new Problem<M>(cloneModel, Collections.singletonMap(pAgent, pState));
    }

    protected Policy changePreservationGoal(Agent pAgent, M pModel, State pState)
            throws InvalidExpressionException {
        //save the goal
        Expression finalGoal = pModel.getGoal();
        //save the original preservation goal
        Expression originalPreservGoal = pModel.getPreservationGoal();
        //get the new preservation goal, based on the original and the state
        Expression newPropsExp = getNewPreservationGoal(pModel, originalPreservGoal, pState);
        //copy the original preservation goal
        Expression newPreservGoal = new Expression(originalPreservGoal.toString());
        //and join them with an AND operator
        newPreservGoal.add(newPropsExp, BinaryOperator.AND);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPropsExp)
                && !originalPreservGoal.contains(newPropsExp.negate())
                && !pModel.getPropositionFunction().satisfies(pState, finalGoal)) {
            //TODO: Decide which propositions are giving a bad reward
            //create a new cloned problem
            Problem<M> newProblem = cloneProblem(pModel, pState, pAgent.getNumber(), newPreservGoal);
            //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
            Policy p = pAgent.getAlgorithm().run(newProblem);
            //if there is a path to reach the goal
            if (ValidPlanFinder.exist(newProblem, pAgent.getPolicy(), pAgent.getNumber())) {
                //set the new preservation goal to the current problem
                newProblem.getModel().setPreservationGoal(newPreservGoal);
                //confirm the goal modification
                Log.info("changed preservation goal from {"
                        + originalPreservGoal + "} to {" + newPreservGoal + "}");
                return p;
            }
        }
        return null;
    }

    private Expression getNewPreservationGoal(M pModel, Expression pOriginalPreservGoal, State pState) {
        //get the "problematic" expression
        //i.e. the one that is giving the bad reward
        Expression newPropsExp =
                pModel.getPropositionFunction().getExpressionForState(pState);
        //negate it
        newPropsExp = newPropsExp.negate();

        return newPropsExp;
    }

    protected boolean mustChangePreservationGoal(M pModel, State pState) {
        Proposition changedProp = null;
        Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pState);

        if (props != null) {
            BehaviourUtil.getPropositionsRewards(null, null)
        }

        return changedProp != null;
    }
}
