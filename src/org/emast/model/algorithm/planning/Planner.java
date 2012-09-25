package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeSupport;
import java.util.List;
import org.emast.model.agent.Agent;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;

/**
 *
 * @author anderson
 */
public class Planner<M extends MDP, A extends Agent> {

    public static final String FINISHED_PROP = "FINISHED";
    public static final String FINISHED_ALL_PROP = "FINISHED_ALL";
    private final List<A> agents;
    private final PolicyGenerator<M> policyGenerator;
    private final PropertyChangeSupport pcs;

    public Planner(PolicyGenerator<M> pPolicyGen, List<A> pAgents) {
        agents = pAgents;
        policyGenerator = pPolicyGen;
        pcs = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    public void run(final Problem<M> pProblem) {
        final Policy policy = policyGenerator.run(pProblem);
        //init
        init(pProblem, policy);
        //run
        doRun(pProblem);
    }

    private void init(Problem<M> pProblem, Policy pPolicy) {
        //execute them all
        for (final A agent : agents) {
            //set the initial policy and model
            agent.init(pProblem, pPolicy);
        }
    }

    private void doRun(final Problem<M> pProblem) {
        //execute them all
        for (final A agent : agents) {
            //get new thread name
            String threadName = agent.getClass().getSimpleName()
                    + " - " + pProblem.getClass().getSimpleName();
            //create and run an thread for the agent iterator 
            new Thread(new Runnable() {
                @Override
                public void run() {
                    agent.run(pProblem);

                    finished(agent);
                }
            }, threadName).start();
        }
    }

    protected void finished(A agent) {
        pcs.firePropertyChange(FINISHED_PROP, 0, 0);
        if (isFinished()) {
            finished();
        }
    }

    public List<A> getAgents() {
        return agents;
    }

    public boolean isFinished() {
        boolean ret = true;
        for (A agent : agents) {
            ret &= agent.isFinished();
        }

        return ret;
    }

    protected void finished() {
        pcs.firePropertyChange(FINISHED_ALL_PROP, 0, 0);
    }

    public boolean existValidPlan(Problem<M> pProblem) {
        Policy policy = policyGenerator.run(pProblem);
        boolean ret = true;
        M model = pProblem.getModel();
        for (int i = 0; i < model.getAgents(); i++) {
            //create a new simple agent iterator
            final Agent agent = new Agent(i);
            agent.init(pProblem, policy);
            //find the plan for the newly created problem
            //with the preservation goal changed
            agent.run(pProblem);
            //get the resulting plan
            final Plan plan = agent.getPlan();
            //save in ret if a plan was generated
            ret &= plan != null && !plan.isEmpty();
        }
        return ret;
    }

    public PolicyGenerator<M> getPolicyGenerator() {
        return policyGenerator;
    }
}
