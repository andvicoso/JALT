package org.emast.model.algorithm.planning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.agent.Agent;
import org.emast.model.agent.PropReputationAgent;
import org.emast.model.agent.factory.AgentFactory;
import org.emast.model.algorithm.planning.propositionschooser.PropositionsChooser;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.model.propositional.operator.BinaryOperator;
import org.emast.model.solution.Plan;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;

/**
 *
 * @author Anderson
 */
public class ERGExecutor implements PolicyGenerator<ERG>, PropertyChangeListener {

    private final PropositionsChooser chooser;
    private final int maxIterations;
    private final PolicyGenerator<ERG> policyGenerator;
    private final AgentFactory agentFactory;

    public ERGExecutor(PolicyGenerator<ERG> pPolicyGenerator, AgentFactory pAgentFactory,
            PropositionsChooser pPropositionBadReward, int pMaxIterations) {
        chooser = pPropositionBadReward;
        maxIterations = pMaxIterations;
        policyGenerator = pPolicyGenerator;
        agentFactory = pAgentFactory;
    }

    @Override
    public String printResults() {
        return "";
    }

    @Override
    public synchronized Policy run(final Problem<ERG> pProblem) {
        Problem<ERG> problem = pProblem;
        ERG model = problem.getModel();
        int iterations = 1;
        //start main loop
        do {
            Log.info("\nITERATION " + iterations + ":\n");
            //vars
            List<PropReputationAgent> agents = agentFactory.createAgents(model.getAgents());
            Planner<ERG, PropReputationAgent> planner = createPlanner(agents);
            //run problem
            planner.run(pProblem);
            //wait to be awakened from a planner notification (when it finished running all agents)
            try {
                if (!planner.isFinished()) {
                    wait();
                }
            } catch (InterruptedException ex) {
                Log.debug("Executou falhou: Erro de thread ");
                return null;
            }
            //results
            Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //get results for each agent
            for (PropReputationAgent agent : agents) {
                reps.add(agent.getPropositionsReputation());
            }
            //choose "bad" propositions
            Collection<Proposition> props = chooser.choose(reps);
            //verify the need to change the preservation goal
            if (!props.isEmpty()) {
                changePreservationGoal(pProblem, props);
            }
        } while (iterations++ < maxIterations);
        //run problem again with the combined preserv. goals
        //to get the policy
        return policyGenerator.run(pProblem);
    }

    public boolean existValidPlan(Problem<ERG> pProblem) {
        Policy policy = policyGenerator.run(pProblem);
        boolean ret = true;
        ERG model = pProblem.getModel();
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

    protected boolean changePreservationGoal(Problem<ERG> pProblem, Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and bad reward props
        Expression newPreservGoal = createNewPreservationGoal(originalPreservGoal, pProps);
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
            if (existValidPlan(newProblem)) {
                //set the preservation goal to the current problem
                pProblem.getModel().setPreservationGoal(newPreservGoal);
                //confirm the goal modification
                Log.info("Changed preservation goal from {"
                        + originalPreservGoal + "} to {" + newPreservGoal + "}");
                return true;
            }
        }
        return false;
    }

    private Expression createNewPreservationGoal(Expression pOriginalPreservGoal,
            Collection<Proposition> pProps) {
        Expression exp = new Expression(pOriginalPreservGoal);
        for (Proposition prop : pProps) {
            //create expression for each bad reward proposition
            Expression e = new Expression(BinaryOperator.AND, prop);
            //negate it
            e = e.negate();
            //add to the returned exp
            exp.add(e, BinaryOperator.AND);
        }
        return exp;
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

    @Override
    public synchronized void propertyChange(PropertyChangeEvent pEvt) {
        if (Planner.FINISHED_ALL_PROP.equals(pEvt.getPropertyName())) {
            notifyAll();
        }
    }

    private Planner<ERG, PropReputationAgent> createPlanner(List<PropReputationAgent> pAgents) {
        Planner<ERG, PropReputationAgent> planner = new Planner<ERG, PropReputationAgent>(policyGenerator, pAgents);
        //listen to changes of planner properties
        planner.getPropertyChangeSupport().addPropertyChangeListener(this);

        return planner;
    }
}
