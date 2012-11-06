package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.agent.AgentFactory;
import org.emast.model.agent.behavior.Collective;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.agent.behavior.collective.ChangeModel;
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
    private final List<Individual<M>> agentBehaviors;
    private final List<Collective<M>> behaviors;
    private final int maxIterations;
    private List<Agent> agents;

    public AgentGroup(PolicyGenerator<M> pPolicyGenerator,
            List<Collective<M>> pBehaviors, List<Individual<M>> pAgentBehaviors, int pMaxIterations) {
        maxIterations = pMaxIterations;
        policyGenerator = pPolicyGenerator;
        agentFactory = new AgentFactory();
        behaviors = pBehaviors;
        agentBehaviors = pAgentBehaviors;
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
        Planner planner = null;
        Policy policy;
        int iterations = 0;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations + ":\n");
            //create policy
            policy = policyGenerator.run(pProblem, pParameters);
            //create new agents
            createAgents(model);
            //create planner
            planner = createAndRun(planner, policy, problem);

            if (++iterations >= maxIterations) {
                break;
            }
            //run change model behaviors
            behave(ChangeModel.class, problem);
        } while (true);

        return policy;
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

    private Planner<M> createPlanner(Policy pPolicy, List<Agent> pAgents) {
        Planner<M> planner = new Planner<M>(pPolicy, pAgents);
        //listen to changes of planner properties
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);

        return planner;
    }

    private void createAgents(M model) {
        agents = agentFactory.createAgents(model.getAgents(), agentBehaviors);
    }

    private void behave(Class<? extends Collective> pClass,
            Problem<M> pProblem, Object... pParameters) {
        behave(pClass, pProblem, CollectionsUtils.asStringMap(pParameters));
    }

    private void behave(Class<? extends Collective> pClass,
            Problem<M> pProblem, Map<String, Object> pParameters) {
        for (final Collective b : behaviors) {
            if (pClass.isAssignableFrom(b.getClass())) {
                b.behave(agents, pProblem, pParameters);
            }
        }
    }

    private Planner createAndRun(Planner planner, Policy policy, Problem problem) {
        if (planner != null) {
            planner.getPropertyChangeSupport().removePropertyChangeListener(this);
        }
        //create planner to manage agents' execution
        planner = createPlanner(policy, agents);
        //run problem for each agent
        planner.run(problem);
        //wait to be awakened from the planner notification
        //(when it finished running all agents)
        wait(planner);

        return planner;
    }
}
