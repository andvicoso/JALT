package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.agent.AgentFactory;
import org.emast.model.agent.behaviour.CollectiveBehaviour;
import org.emast.model.agent.behaviour.IndividualBehaviour;
import org.emast.model.agent.behaviour.collective.ChangeModel;
import org.emast.model.model.MDP;
import org.emast.model.planning.Planner;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class AgentGroup<M extends MDP> implements PolicyGenerator<M>, PropertyChangeListener {

    private final PolicyGenerator<M> policyGenerator;
    private final AgentFactory agentFactory;
    private final List<IndividualBehaviour<M>> agentBehaviours;
    private final List<CollectiveBehaviour<M>> behaviours;
    private final int maxIterations;
    private List<Agent> agents;

    public AgentGroup(PolicyGenerator<M> pPolicyGenerator,
            List<CollectiveBehaviour<M>> pBehaviours, List<IndividualBehaviour<M>> pAgentBehaviours, int pMaxIterations) {
        maxIterations = pMaxIterations;
        policyGenerator = pPolicyGenerator;
        agentFactory = new AgentFactory();
        behaviours = pBehaviours;
        agentBehaviours = pAgentBehaviours;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (Agent agent : agents) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        Problem<M> problem = pProblem;
        M model = problem.getModel();
        int iterations = 1;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations + ":\n");
            createAgents(model);
            Planner planner = createPlanner(agents);
            //run problem
            planner.run(problem);
            //wait to be awakened from a planner notification (when it finished running all agents)
            try {
                if (!planner.isFinished()) {
                    wait();
                }
            } catch (InterruptedException ex) {
                Log.debug("Execution failed. Thread interrupted");
                return null;
            }

            behave(ChangeModel.class, problem);
        } while (iterations++ < maxIterations);
        //run problem again with the combined preserv. goals
        //to get the policy
        createAgents(model);
        //create a new planner
        Planner planner = createPlanner(agents);
        //run problem
        return planner.run(pProblem);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent pEvt) {
        if (Planner.FINISHED_ALL_PROP.equals(pEvt.getPropertyName())) {
            notifyAll();
        }
    }

    private Planner<M> createPlanner(List<Agent> pAgents) {
        Planner<M> planner = new Planner<M>(policyGenerator, pAgents);
        //listen to changes of planner properties
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);

        return planner;
    }

    private void createAgents(M model) {
        agents = agentFactory.createAgents(model.getAgents(), agentBehaviours);
    }

    private void behave(Class<? extends CollectiveBehaviour> pClass,
            Problem<M> pProblem, Object... pParameters) {
        behave(pClass, pProblem, CollectionsUtils.asStringMap(pParameters));
    }

    private void behave(Class<? extends CollectiveBehaviour> pClass,
            Problem<M> pProblem, Map<String, Object> pParameters) {
        for (final CollectiveBehaviour b : behaviours) {
            if (pClass.isAssignableFrom(b.getClass())) {
                b.behave(agents, pProblem, pParameters);
            }
        }
    }
}
