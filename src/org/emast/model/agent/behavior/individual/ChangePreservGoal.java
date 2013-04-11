package org.emast.model.agent.behavior.individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.emast.infra.log.Log;
import org.emast.model.Combinator;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.agent.behavior.individual.reward.PropReward;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.ValidPathFinder;
import org.emast.model.planning.rewardcombinator.MeanValueCombinator;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class ChangePreservGoal implements Individual<ERG>, ChangeModel<ERG> {

    private final PolicyGenerator<ERG> algorithm;
    private final MultiChooser<Proposition> chooser;
    private final PreservationGoalFactory factory;

    public ChangePreservGoal(PolicyGenerator<ERG> pAlgorithm, MultiChooser<Proposition> pChooser) {
        algorithm = pAlgorithm;
        chooser = pChooser;
        factory = new PreservationGoalFactory();
    }

    @Override
    public void behave(ERGAgentIterator pAgent, Problem<ERG> pProblem, Map<String, Object> pParameters) {
        ERG pModel = pProblem.getModel();
        State pState = (State) pParameters.get("state");
        //verify the need to change the preservation goal
        if (mustChangePreservationGoal(pAgent, pModel, pState)) {
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
            }
        }
    }

    protected Problem<ERG> cloneProblem(ERG pModel, State pState, int pAgent, Expression pNewPreservGoal) {
        ERG cloneModel = (ERG) pModel.copy();
        //set new preservation goal
        cloneModel.setPreservationGoal(pNewPreservGoal);
        //set the initial state only for the current number
        //return a new problem with a new preservation goal and initial state
        return new Problem<ERG>(cloneModel, Collections.singletonMap(pAgent, pState));
    }

    protected Policy changePreservationGoal(ERGAgentIterator pAgent, ERG pModel, State pState)
            throws InvalidExpressionException {
        int number = pAgent.getAgent();
        //save the goal
        Expression finalGoal = pModel.getGoal();
        //save the original preservation goal
        Expression originalPreservGoal = pModel.getPreservationGoal();
        //TODO: Decide which propositions are giving a bad reward
        Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pState);
        //get the new preservation goal, based on the original and the state
        Expression newPreservGoal = factory.createPreservationGoal(originalPreservGoal, props);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal.negate())
                && !pModel.getPropositionFunction().satisfies(pState, finalGoal)) {
            //create a new cloned problem
            Problem<ERG> newProblem = cloneProblem(pModel, pState, number, newPreservGoal);
            //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
            Policy p = algorithm.run(newProblem);
            //if there is a path to reach the goal
            if (ValidPathFinder.exist(newProblem, pAgent.getPolicy(), number)) {
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

    protected boolean mustChangePreservationGoal(ERGAgentIterator pAgent, ERG pModel, State pState) {
        Collection<Proposition> props = pModel.getPropositionFunction().getPropositionsForState(pState);

        if (props != null) {
            List<PropReward> behaviors = CollectionsUtils.getElementsOfType(pAgent.getBehaviors(),
                    PropReward.class);
            Set<Proposition> badProps = getPropositions(behaviors);
            //if have one bad property, change preservation goal
            for (final Proposition prop : badProps) {
                if (props.contains(prop)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Set<Proposition> getPropositions(List<PropReward> pBehaviors) {
        Collection<Map<Proposition, Double>> values = new ArrayList<Map<Proposition, Double>>();

        for (PropReward beh : pBehaviors) {
            Map<Proposition, Double> map = beh.getResult();
            values.add(map);
        }

        Combinator<Proposition> combinator = new MeanValueCombinator<Proposition>();
        Map<Proposition, Double> combined = combinator.combine(values);

        return chooser.choose(combined);
    }
}
