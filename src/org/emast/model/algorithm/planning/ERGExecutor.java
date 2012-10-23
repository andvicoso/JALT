package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.agent.AgentFactory;
import org.emast.model.agent.behaviour.Behaviour;
import org.emast.model.agent.combineresults.CombineResults;
import org.emast.model.model.ERG;
import org.emast.model.planning.Planner;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author Anderson
 */
public class ERGExecutor implements PolicyGenerator<ERG>, PropertyChangeListener {

    private final int maxIterations;
    private final PolicyGenerator<ERG> policyGenerator;
    private final AgentFactory agentFactory;
    private final CombineResults<ERG> combineResults;
    private final List<Behaviour> behaviours;
    private List<Agent> agents;

    public ERGExecutor(PolicyGenerator<ERG> pPolicyGenerator, List<Behaviour> pBehaviours,
            CombineResults<ERG> pCombineResults, int pMaxIterations) {
        combineResults = pCombineResults;
        maxIterations = pMaxIterations;
        policyGenerator = pPolicyGenerator;
        agentFactory = new AgentFactory();
        behaviours = pBehaviours;
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
    public synchronized Policy run(final Problem<ERG> pProblem) {
        Problem<ERG> problem = pProblem;
        ERG model = problem.getModel();
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

            combineResults.combine(problem, agents);
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

    private Planner<ERG> createPlanner(List<Agent> pAgents) {
        Planner<ERG> planner = new Planner<ERG>(policyGenerator, pAgents);
        //listen to changes of planner properties
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);

        return planner;
    }

    private void createAgents(ERG model) {
        agents = agentFactory.createAgents(model.getAgents(), behaviours);
    }
}
