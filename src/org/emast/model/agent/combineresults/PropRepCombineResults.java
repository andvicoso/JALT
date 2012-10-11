package org.emast.model.agent.combineresults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.agent.PropReputationAgent;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.propositionschooser.PropositionsChooser;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class PropRepCombineResults implements CombineResults<ERG, PropReputationAgent> {

    private final PropositionsChooser chooser;
    private final PreservationGoalFactory factory;

    public PropRepCombineResults(PropositionsChooser pChooser, PreservationGoalFactory pFactory) {
        chooser = pChooser;
        factory = pFactory;
    }

    @Override
    public void combine(Problem<ERG> pProblem, List<PropReputationAgent> pAgents) {
        Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
        //get results for each agent
        for (PropReputationAgent agent : pAgents) {
            reps.add(agent.getPropositionsReputation());
        }
        //choose "bad" propositions
        Collection<Proposition> props = chooser.choose(reps);
        //verify the need to change the preservation goal
        if (!props.isEmpty()) {
            changePreservationGoal(pProblem, props);
        }
    }

    protected boolean changePreservationGoal(Problem<ERG> pProblem, Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and bad reward props
        Expression newPreservGoal = factory.createPreservationGoal(originalPreservGoal, pProps);
        //compare previous goal with the newly created
        if (!newPreservGoal.equals(originalPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal)
                && !originalPreservGoal.contains(newPreservGoal.negate())
                && existValidFinalState(model, newPreservGoal)) {
            //create a new cloned problem
            //ERG newModel = cloneModel(model, newPreservGoal);
            //Problem newProblem = new Problem(newModel, pProblem.getInitialStates());
            //Execute the base algorithm (PPFERG) over the new model (with new preservation goal)
            //if there are paths for all to reach the goal
            //Log.info("Trying to find a valid plan for preserv: " + model.getPreservationGoal());
            //if (ValidPlanFinder.exist(newProblem, policyGenerator)) {
            //set the preservation goal to the current problem
            pProblem.getModel().setPreservationGoal(newPreservGoal);
            //confirm the goal modification
            Log.info("Changed preservation goal from {"
                    + originalPreservGoal + "} to {" + newPreservGoal + "}");
            return true;
            // }
        }
        return false;
    }

    protected ERG cloneModel(ERG pModel, Expression pNewPreservGoal) {
        ERG newModel = (ERG) pModel.copy();
        //set new preservation goal
        newModel.setPreservationGoal(pNewPreservGoal);

        return newModel;
    }

    private boolean existValidFinalState(ERG model, Expression newPreservGoal) {
        try {
            Collection<State> finalStates = model.getPropositionFunction().intension(
                    model.getStates(), model.getPropositions(), model.getGoal());

            for (State state : finalStates) {
                if (model.getPropositionFunction().satisfies(state, newPreservGoal)) {
                    return true;
                }
            }
        } catch (InvalidExpressionException ex) {
        }

        return false;
    }
}
