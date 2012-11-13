package org.emast.model.agent.behavior.collective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.emast.infra.log.Log;
import org.emast.model.Chooser;
import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.agent.behavior.Collective;
import org.emast.model.agent.behavior.individual.reward.PropReward;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.ValidPathFinder;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class ChangePreservGoal implements Collective<ERG>, ChangeModel<ERG> {

    private final PolicyGenerator<ERG> algorithm;
    private final Chooser<Proposition> chooser;
    private final boolean acceptOnePath;
    private final PreservationGoalFactory factory;

    /**
     *
     * @param pAlgorithm
     * @param pChooser
     * @param pAcceptOnePath Indicates if the algorithm should accept at least one 
     * valid path to the final goal for one agent
     */
    public ChangePreservGoal(PolicyGenerator<ERG> pAlgorithm, Chooser<Proposition> pChooser,
            boolean pAcceptOnePath) {
        algorithm = pAlgorithm;
        chooser = pChooser;
        acceptOnePath = pAcceptOnePath;
        factory = new PreservationGoalFactory();
    }

    @Override
    public void behave(List<ERGAgentIterator> pAgents, Problem<ERG> pProblem, Map<String, Object> pParameters) {
        Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
        //get results for each agent
        for (ERGAgentIterator agent : pAgents) {
            List<PropReward> behaviors = CollectionsUtils.getElementsOfType(agent.getBehaviors(),
                    PropReward.class);
            reps.addAll(getPropositionsValues(behaviors));
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
            ERG newModel = cloneModel(model, newPreservGoal);
            Problem newProblem = new Problem(newModel, pProblem.getInitialStates());
            //Execute the base algorithm (PPFERG) over the new model (with new preservation goal)
            //if there are paths for all to reach the goal
            Log.info("Trying to find a valid plan for preserv: " + model.getPreservationGoal());
            if (ValidPathFinder.exist(newProblem, algorithm, acceptOnePath)) {
                //set the preservation goal to the current problem
                model.setPreservationGoal(newPreservGoal);
                //confirm the goal modification
                Log.info("Changed preservation goal from {"
                        + originalPreservGoal + "} to {" + newPreservGoal + "}");
                return true;
            }
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

    private Collection<Map<Proposition, Double>> getPropositionsValues(List<PropReward> pBehaviors) {
        Collection<Map<Proposition, Double>> list = new ArrayList<Map<Proposition, Double>>();

        for (PropReward beh : pBehaviors) {
            Map<Proposition, Double> map = beh.getResult();
            list.add(map);
        }

        return list;
    }
}
