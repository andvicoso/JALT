package org.emast.model.planning;

import java.beans.PropertyChangeSupport;
import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP> implements Algorithm<M, List<Agent>> {

    public static final String FINISHED_PROP = "FINISHED";
    public static final String FINISHED_ALL_PROP = "FINISHED_ALL";
    private final List<Agent> agents;
    private final Policy policy;
    private final PropertyChangeSupport pcs;

    public Planner(Policy pPolicy, List<Agent> pAgents) {
        agents = pAgents;
        policy = pPolicy;
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public List<Agent> run(final Problem<M> pProblem, Object... pParameters) {
        //execute them all
        for (final Agent agent : agents) {
            createThread(agent, pProblem).start();
        }

        return agents;
    }

    private Thread createThread(final Agent agent, final Problem<M> pProblem) {
        //get new thread name
        String threadName = agent.getClass().getSimpleName()
                + "-" + agent.getNumber()
                + "-" + pProblem.getClass().getSimpleName();
        //create and run an thread for the agent execution 
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                agent.run(pProblem, policy);

                finished(agent);
            }
        }, threadName);

        return t;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        for (Agent agent : agents) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    protected void finished(final Agent agent) {
        pcs.firePropertyChange(FINISHED_PROP, 0, 0);
        if (isFinished()) {
            finished();
        }
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

    public List<Agent> getAgents() {
        return agents;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }
}
