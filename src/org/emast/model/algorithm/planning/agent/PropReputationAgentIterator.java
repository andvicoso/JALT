package org.emast.model.algorithm.planning.agent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.emast.model.algorithm.planning.agent.ERGAgentIterator.ERGAgentIteratorFactory;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
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
public class PropReputationAgentIterator<M extends MDP & ERG> extends ERGAgentIterator<M> {

    private final double badRewardThreshold;
    private final Map<Proposition, Double> localPropositionsReputation;

    public PropReputationAgentIterator(M pModel, Policy pInitialPolicy, int pAgent, State pInitialState,
            double pBadRewardThreshold) {
        super(pModel, pInitialPolicy, pAgent, pInitialState);
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

    protected void manageBadReward(final State pNextState, final double pReward) {
        //save proposition reputation based on the state and reward received
        savePropositionReputation(pNextState, pReward, localPropositionsReputation);
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pNextState)) {
            //get the new policy for the new preservation goal (if one exists)
            final Policy p = changePreservationGoal(pNextState);
            //if found a policy
            if (p != null) {
                //changed preservation goal, continue iteration 
                //with the new preservation goal and policy
                setPolicy(p);
            }
        }
    }
    //TODO: define better what is a bad reward state (using state)

    private boolean isBadRewardState(final State pState, final double pReward) {
        return pReward < badRewardThreshold;
    }

    protected void savePropositionReputation(final State pNextState,
            final double pReward, final Map<Proposition, Double> pPropositionsReputation) {
        if (pPropositionsReputation != null) {
            //bad reward value is distributed equally over the state`s propostions
            final Collection<Proposition> props = getPropositionsForState(pNextState);
            final double propReward = pReward / props.size();
            for (final Proposition proposition : props) {
                Double currPropReward = pPropositionsReputation.get(proposition);
                currPropReward = currPropReward == null ? 0d : currPropReward;
                pPropositionsReputation.put(proposition, propReward + currPropReward);
            }
        }
    }

    protected Double getPropositionReputation(final Proposition pProposition) {
        return localPropositionsReputation.get(pProposition);
    }

    protected Problem<M> cloneProblem(final Expression pNewPreservGoal) {
        final M cloneModel = (M)getModel().copy();
        //set new preservation goal
        cloneModel.setPreservationGoal(pNewPreservGoal);
        //set the initial state only for the current agent
        //return a new problem with a new preservation goal and initial state
        return new Problem<M>(cloneModel, Collections.singletonMap(getAgent(), currentState));
    }

    public Map<Proposition, Double> getLocalPropositionsReputation() {
        return localPropositionsReputation;
    }

    protected Policy changePreservationGoal(final State pState) {
        //save the final goal
        final Expression finalGoal = getModel().getGoal();
        //save the original preservation goal
        final Expression originalPreservGoal = getModel().getPreservationGoal();
        //get the new preservation goal, based on the original and the state
        final Expression newPropsExp = getNewPreservationGoal(originalPreservGoal, pState);
        //copy the original preservation goal
        final Expression newPreservGoal = new Expression(originalPreservGoal.toString());
        //and join them with an AND operator
        newPreservGoal.add(newPropsExp, BinaryOperator.AND);

        try {
            //compare previous goal with the newly created
            if (!newPreservGoal.equals(originalPreservGoal)
                    && !originalPreservGoal.contains(newPropsExp)
                    && !originalPreservGoal.contains(newPropsExp.negate())
                    && !getModel().getPropositionFunction().satisfies(getModel().getPropositions(), pState, finalGoal)) {
                //TODO: Decide which propositions are giving a bad reward
                //create a new cloned problem
                final Problem<M> newProblem = cloneProblem(newPreservGoal);
                //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
                final Policy p = getAlgorithm().run(newProblem);
                //if there isn`t a path to reach the final goal,
                if (canReachFinalGoal(p, newProblem.getModel())) {
                    //set the new preservation goal to the current problem
                    newProblem.getModel().setPreservationGoal(newPreservGoal);
                    //confirm the goal modification
                    print("changed preservation goal from {"
                            + originalPreservGoal + "} to {" + newPreservGoal + "}");
                    return p;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private boolean canReachFinalGoal(final Policy pPolicy, final M pModel) {
        //create a new simple agent iterator
        final AgentIterator iterator = new AgentIterator(pModel,
                pPolicy, getAgent(), getInitialState());
        //find the plan for the newly created problem
        //with the preservation goal changed
        iterator.run();
        //get the resulting plan
        final Plan agPlan = iterator.getPlan();
        return agPlan != null && !agPlan.isEmpty();
    }

    private Expression getNewPreservationGoal(final Expression pOriginalPreservGoal,
            final State pState) {
        //get the prop func item, to get the "problematic" expression
        //i.e. the one that is giving the bad reward
        Expression newPropsExp =
                getModel().getPropositionFunction().getExpressionForState(pState);
        //negate it
        newPropsExp = newPropsExp.negate();

        return newPropsExp;
    }

    protected boolean mustChangePreservationGoal(final State pState) {
        Proposition changedProp = null;
        final Collection<Proposition> props = getPropositionsForState(pState);

        if (props != null) {
            for (final Proposition proposition : props) {
                final Double rep = getPropositionReputation(proposition);
                if (rep < badRewardThreshold) {
                    changedProp = proposition;
                    break;
                }
            }
        }

        return changedProp != null;
    }

    public static class PropReputationAgentIteratorFactory<M extends MDP & ERG> extends ERGAgentIteratorFactory<M> {

        protected double badRewardThreshold;

        public PropReputationAgentIteratorFactory(double pBadRewardThreshold) {
            badRewardThreshold = pBadRewardThreshold;
        }

        @Override
        public PropReputationAgentIterator createAgentIterator(M pModel, Policy pPolicy, int pAgent, State pInitialState) {
            final M newModel = (M)pModel.copy();
            return new PropReputationAgentIterator(newModel, pPolicy, pAgent, pInitialState, badRewardThreshold);
        }
    }
}
