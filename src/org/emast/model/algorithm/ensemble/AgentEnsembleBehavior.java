package org.emast.model.algorithm.ensemble;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.agent.AgentFactory;
import org.emast.model.agent.behavior.Collective;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.planning.Planner;
import org.emast.model.planning.PreservationGoalFactory;
import org.emast.model.planning.ValidPathFinder;
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
public class AgentEnsembleBehavior<M extends ERG> implements Algorithm<M, Policy>, PolicyGenerator<M>, PropertyChangeListener {

    private final PolicyGenerator<M> policyGenerator;
    private final AgentFactory agentFactory;
    private final List<Individual<M>> agentBehaviors;
    private final List<Collective<M>> behaviors;
    private List<ERGAgentIterator> agentIterators;

    public AgentEnsembleBehavior(PolicyGenerator<M> pPolicyGenerator, List<Collective<M>> pBehaviors,
            List<Individual<M>> pAgentBehaviors) {
        policyGenerator = pPolicyGenerator;
        agentFactory = new AgentFactory();
        behaviors = pBehaviors;
        agentBehaviors = pAgentBehaviors;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (ERGAgentIterator agent : agentIterators) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        Problem<M> problem = pProblem;
        M model = problem.getModel();
        Policy policy;
        int iterations = 0;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations++ + ":\n");
            //create policy
            policy = policyGenerator.run(pProblem, pParameters);
            //create new agents
            createAgents(model);
            //create planner
            Planner planner = createPlanner(policy);
            //run planner (that runs the problem for each agent)
            planner.run(problem);
            //wait to be awakened from the planner notification
            //(when it finished running all agents)
            //wait(planner);
        } while (changePreservGoal(pProblem));        

        return policy;
    }

    public Set<Proposition> choose(Collection<Map<Proposition, Double>> pReps) {
        //combine reputations for propositions from agents
        Map<Proposition, Double> combined = combine(pReps);
        Log.info("Combined prop values: "+combined);
        //get "bad" propositions
        final Set<Proposition> set = new HashSet<Proposition>();
        for (Proposition prop : combined.keySet()) {
            if (combined.get(prop) <= -10) {
                set.add(prop);
            }
        }
        return set;
    }

    public Map<Proposition, Double> combine(final Collection<Map<Proposition, Double>> pReputations) {
        if (pReputations.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        final Map<Proposition, Double> result = new HashMap<Proposition, Double>();
        final Map<Proposition, Integer> count = new HashMap<Proposition, Integer>();
        //find sums and counts
        for (Map<Proposition, Double> map : pReputations) {
            for (Proposition prop : map.keySet()) {
                //count
                Integer c = count.get(prop);
                count.put(prop, (c == null ? 0 : c) + 1);
                //sum
                Double current = map.get(prop);
                Double sum = result.get(prop);
                current = current == null ? 0 : current;
                sum = sum == null ? 0 : sum;

                result.put(prop, current + sum);
            }
        }
        //mean
        for (Proposition prop : result.keySet()) {
            Integer c = count.get(prop);
            Double sum = result.get(prop);

            result.put(prop, sum / c);
        }

        return result;
    }

    protected boolean changePreservationGoal(Problem<M> pProblem, Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and bad reward props
        Expression newPreservGoal = new PreservationGoalFactory().createPreservationGoal(originalPreservGoal, pProps);
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
            if (ValidPathFinder.exist(newProblem, policyGenerator, false)) {
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

    private void wait(Planner pPlanner) {
        try {
            synchronized (this) {
                if (!pPlanner.isFinished()) {
                    wait();
                }
            }
        } catch (InterruptedException ex) {
            Log.debug("Execution failed. Thread interrupted");
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent pEvt) {
        if (Planner.FINISHED_ALL_PROP.equals(pEvt.getPropertyName())) {
            notifyAll();
        }
    }

    private void createAgents(M model) {
        agentIterators = agentFactory.createAgents(model.getAgents(), agentBehaviors);
    }

    public void behave(Class<? extends Collective> pClass,
            Problem<M> pProblem, Object... pParameters) {
        behave(pClass, pProblem, CollectionsUtils.asStringMap(pParameters));
    }

    private void behave(Class<? extends Collective> pClass,
            Problem<M> pProblem, Map<String, Object> pParameters) {
        for (final Collective b : behaviors) {
            if (pClass.isAssignableFrom(b.getClass())) {
                b.behave(agentIterators, pProblem, pParameters);
            }
        }
    }

    private Planner createPlanner(Policy policy) {
        Planner planner = new Planner<M>(policy, agentIterators);
        //listen to changes of planner properties
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);

        return planner;
    }

    private boolean changePreservGoal(Problem<M> pProblem) {
        //run change model behaviors
        //behave(ChangeModel.class, problem);
        Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
        //get results for each agent
        for (ERGAgentIterator agent : agentIterators) {
            reps.add(agent.getPropTable().getPropValue());
        }
        //choose "bad" propositions
        Collection<Proposition> props = choose(reps);
        //verify the need to change the preservation goal
        return !props.isEmpty() && changePreservationGoal(pProblem, props);
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
