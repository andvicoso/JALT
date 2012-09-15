package org.emast.model.algorithm.executor;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.planning.Planner;
import org.emast.model.algorithm.planning.agent.factory.AgentIteratorFactory;
import org.emast.model.algorithm.planning.agent.iterator.AgentIterator;
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
public class ERGExecutor<R> implements Algorithm<ERG, R> {

    private static final int MAX_ITERATIONS = 10;
    private Planner<ERG, R> planner;

    public ERGExecutor(Policy pInitialPolicy, AgentIteratorFactory pFactory) {
        planner = new Planner<ERG, R>(pInitialPolicy, pFactory);
    }

    public Planner getPlanner() {
        return planner;
    }

    @Override
    public String printResults() {
        return "";
    }

    @Override
    public R run(Problem<ERG> pProblem) {
        int count = 0;
        do {
            final Collection<Map<Proposition, Double>> reps = new ArrayList<Map<Proposition, Double>>();
            //run problem
            planner.run(pProblem);
            //get results for each agent iterator
            for (final AgentIterator<ERG> agentIt : planner.getIterators()) {
                Map<Proposition, Double> propsRep = agentIt.getPropositionLocalReputation();
                State initialState = agentIt.getInitialState();
                Double reward = agentIt.getTotalReward();
                Plan plan = agentIt.getPlan();
                Action initialAction = plan.get(0);
                reps.add(propsRep);
            }
            //combine propositions` reputations from agents
            Map<Proposition, Double> combined = combine(reps);
            //get "bad" propositions
            Collection<Proposition> props = getBadPropositions(combined);
            //verify the need to change the preservation goal
            if (!props.isEmpty()) {
                changePreservationGoal(pProblem, props);
            }
        } while (count++ < MAX_ITERATIONS);
        //run problem again with the final combined preserv. goals
        return planner.run(pProblem);
    }

    //TODO: combine them in a better way
    private Map<Proposition, Double> combine(final Collection<Map<Proposition, Double>> pReputations) {
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

        return new TreeMap<Proposition, Double>(new ValueComparator(result));
    }

    protected boolean changePreservationGoal(final Problem<ERG> pProblem, final Collection<Proposition> pProps) {
        ERG model = pProblem.getModel();
        //save the original preservation goal
        final Expression originalPreservGoal = model.getPreservationGoal();
        //get the new preservation goal, based on the original and the state
        final Expression newPropsExp = createNewPreservationGoal(originalPreservGoal, pProps);
        //copy the original preservation goal
        final Expression newPreservGoal = new Expression(originalPreservGoal.toString());
        //and join them with an AND operator
        newPreservGoal.add(newPropsExp, BinaryOperator.AND);

        try {
            //compare previous goal with the newly created
            if (!newPreservGoal.equals(originalPreservGoal)
                    && !originalPreservGoal.contains(newPropsExp)
                    && !originalPreservGoal.contains(newPropsExp.negate())) {
                //create a new cloned problem
                final Problem newProblem = cloneProblem(pProblem, newPreservGoal);
                //Execute the base algorithm (PPFERG) over the new problem (with the new preservation goal)
                final Policy p = getPlanner().run(pProblem);
                //if there isn`t a path to reach the final goal,
                if (canReachFinalGoal(p, newProblem)) {
                    //set the preservation goal to the current problem
                    pProblem.getModel().setPreservationGoal(newPreservGoal);
                    //confirm the goal modification
                    System.out.println("changed preservation goal from {"
                            + originalPreservGoal + "} to {" + newPreservGoal + "}");
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean canReachFinalGoal(final Policy pPolicy, final ERG pModel) {
        //create a new simple agent iterator
        final AgentIterator iterator = new AgentIterator(pModel, pPolicy, 0);//FIXME: 0
        try {
            //find the plan for the newly created problem
            //with the preservation goal changed
            iterator.run();
            //get the resulting plan
            final Plan plan = iterator.getPlan();
            return plan != null && !plan.isEmpty();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Expression createNewPreservationGoal(final Expression pOriginalPreservGoal,
            final Collection<Proposition> pProps) {
        Expression exp = new Expression(pProps, BinaryOperator.AND);
        //negate it
        exp = exp.negate();
        //join with the current
        exp.add(pOriginalPreservGoal, BinaryOperator.AND);

        return exp;
    }

    protected Collection<Proposition> getBadPropositions(final Map<Proposition, Double> pReputations) {
        Collection<Proposition> changedProps = new ArrayList<Proposition>();

        for (final Proposition proposition : pReputations.keySet()) {
            final Double rep = pReputations.get(proposition);
            if (rep < getProblem().getChangePreserveGoalThreshold()) {
                changedProps.add(proposition);
            }
        }

        return changedProps;
    }

    protected P cloneProblem(final P pProblem, final Expression pNewPreservGoal)
            throws CloneNotSupportedException {
        P newProblem = (P) pProblem.clone();
        //set new preservation goal
        newProblem.setPreservationGoal(pNewPreservGoal);

        return newProblem;
    }

    class ValueComparator implements Comparator {

        Map base;

        public ValueComparator(Map base) {
            this.base = base;
        }

        @Override
        public int compare(Object a, Object b) {
            if ((Double) base.get(a) < (Double) base.get(b)) {
                return 1;
            } else if ((Double) base.get(a) == (Double) base.get(b)) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
