package org.emast.model.planning;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.emast.infra.log.Log;
import org.emast.model.agent.ERGAgentIterator;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP> implements Algorithm<M, List<ERGAgentIterator>> {

    public static final String FINISHED_PROP = "FINISHED";
    public static final String FINISHED_ALL_PROP = "FINISHED_ALL";
    private final List<ERGAgentIterator> agentIterators;
    private final List<Boolean> finished;
    private final Policy policy;
    private final PropertyChangeSupport pcs;

    public Planner(Policy pPolicy, List<ERGAgentIterator> pAgents) {
        agentIterators = pAgents;
        policy = pPolicy;
        pcs = new PropertyChangeSupport(this);
        finished = new ArrayList<Boolean>(agentIterators.size());
        for (int i = 0; i < agentIterators.size(); i++) {
            finished.add(Boolean.FALSE);
        }
    }

    @Override
    public List<ERGAgentIterator> run(final Problem<M> pProblem, Object... pParameters) {
        //execute them all
        for (final ERGAgentIterator agentIterator : agentIterators) {
            agentIterator.run(pProblem, policy);//createThread(agentIterator, pProblem).start();
        }

        return agentIterators;
    }

    private Thread createThread(final ERGAgentIterator agent, final Problem<M> pProblem) {
        //get new thread name
        String threadName = agent.getClass().getSimpleName()
                + "-" + agent.getAgent()
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
        for (ERGAgentIterator agent : agentIterators) {
            sb.append(agent.printResults());
        }
        return sb.toString();
    }

    protected void finished(final ERGAgentIterator agent) {
        pcs.firePropertyChange(FINISHED_PROP, 0, 0);
        finished.add(agent.getAgent(), true);

        if (isFinished()) {
            finished();
        }
    }

    public boolean isFinished() {
        boolean ret = true;
        for (ERGAgentIterator agent : agentIterators) {
            ret &= finished.get(agent.getAgent()) != null;
        }

        return ret;
    }

    protected void finished() {
        pcs.firePropertyChange(FINISHED_ALL_PROP, 0, 1);
        Log.info("\n"+printResults());
    }

    public List<ERGAgentIterator> getAgents() {
        return agentIterators;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }
    
    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
