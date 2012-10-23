package org.emast.model.planning;

import java.beans.PropertyChangeSupport;
import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP> implements PolicyGenerator<M> {

    public static final String FINISHED_PROP = "FINISHED";
    public static final String FINISHED_ALL_PROP = "FINISHED_ALL";
    private final List<Agent> agents;
    private final PolicyGenerator<M> policyGenerator;
    private final PropertyChangeSupport pcs;

    public Planner(PolicyGenerator<M> pPolicyGen, List<Agent> pAgents) {
        agents = pAgents;
        policyGenerator = pPolicyGen;
        pcs = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        final Policy policy = policyGenerator.run(pProblem);
        //run
        doRun(pProblem, policy);

        return policy;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (Agent agent : agents) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    private void doRun(final Problem<M> pProblem, final Policy policy) {
        //execute them all
        for (final Agent agent : agents) {
            //get new thread name
            String threadName = agent.getClass().getSimpleName()
                    + " - " + pProblem.getClass().getSimpleName();
            //create and run an thread for the agent iterator 
            new Thread(new Runnable() {
                @Override
                public void run() {
                    agent.run(pProblem, policy);

                    finished(agent);
                }
            }, threadName).start();
        }
    }

    protected void finished(final Agent agent) {
        pcs.firePropertyChange(FINISHED_PROP, 0, 0);
        if (isFinished()) {
            finished();
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public boolean isFinished() {
        boolean ret = true;
        for (Agent agent : agents) {
            ret &= agent.isFinished();
        }

        return ret;
    }

    protected void finished() {
        pcs.firePropertyChange(FINISHED_ALL_PROP, 0, 1);
        System.out.println(printResults());
    }

    public PolicyGenerator<M> getPolicyGenerator() {
        return policyGenerator;
    }
}
