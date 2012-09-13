package org.emast.model.algorithm.reachability;

import java.util.*;
import net.sourceforge.jeval.EvaluationException;
import org.emast.model.action.Action;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.model.MDP;
import org.emast.model.model.SRG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;
import org.emast.util.ModelUtils;

/**
 * Algoritmo de planejamento probabilistico forte
 *
 * @author Anderson
 * @param <P> Simple reachability problem to be resolved
 */
public class PPF<M extends MDP & SRG> implements Algorithm<M, Policy> {

    protected M model;
    private double gama = 0.9;
    protected int iterations = 0;

    @Override
    public Policy run(Problem<M> pProblem) {
        model = pProblem.getModel();
        final Map<State, Double> values = new HashMap<State, Double>();
        Policy pi = new Policy();
        Policy pi2;
        //get the initial state for only one agent
        final State s0 = pProblem.getInitialStates().get(0);
        // get all the states that satisfies the goal
        final Collection<State> intension = intension(model.getGoal());
        // initialize pi and values
        for (final State state : intension) {
            pi.put(state, Action.TRIVIAL_ACTION);
            values.put(state, 1.d);
        }

        do {
            final Collection<State> c = pi.getStates();
            if (c.contains(s0)) {
                break;
            }

            pi2 = pi;
            final Set<Transition> prunedStrongImage = prune(getStrongImage(c), c);
            pi = choose(values, prunedStrongImage);
            pi.putAll(pi2);
            iterations++;
        } while (!pi.equals(pi2));

        return pi;
    }

    protected Policy choose(final Map<State, Double> pValues,
            final Collection<Transition> pPrune) {
        final Policy pi = new Policy();

        for (final State state : ModelUtils.getStates(pPrune)) {
            final Set<Action> pruneActions = ModelUtils.getActions(pPrune);
            final Map<Double, Action> q = new HashMap<Double, Action>();
            // search for the Qs values for state
            for (final Action actions : pruneActions) {
                double sum = 0;
                for (final State stateLine : model.getTransitionFunction().getReachableStates(model.getStates(),
                        state, actions)) {
                    final Double trans = model.getTransitionFunction().getValue(
                            state, stateLine, actions);
                    if (trans != null && pValues.get(stateLine) != null) {
                        sum += trans * pValues.get(stateLine);
                    }
                }

                double x = gama * sum;
                q.put(x, actions);
            }
            //if found something
            if (q.size() > 0) {
                // get the max value for q
                final Double max = Collections.max(q.keySet());
                final Action tActions = q.get(max);
                pValues.put(state, max);
                pi.put(state, tActions);
            }
        }

        return pi;
    }

    protected Collection<State> intension(final Expression pExpression) {
        try {
            return model.getPropositionFunction().intension(model.getStates(),
                    model.getPropositions(), pExpression);
        } catch (EvaluationException ex) {
        }
        return null;
    }

    /**
     * Para todas as transições da imagem forte, corta todas que não pertençam ao conjunto da cobertura
     *
     * @param pStrongImage
     * @param pC
     * @return
     */
    protected Set<Transition> prune(final Set<Transition> pStrongImage,
            final Collection<State> pC) {
        final Set<Transition> result = new HashSet<Transition>();
        for (final Transition transition : pStrongImage) {
            if (!pC.contains(transition.getState())) {
                result.add(transition);
            }
        }

        return result;
    }

    protected Set<Transition> getWeakImage(final Collection<State> pC) {
        final Set<Transition> result = new HashSet<Transition>();

        for (final State state : model.getStates()) {
            for (final Action actions : getActionsFrom(state)) {
                final Collection<State> reachableStates = model.getTransitionFunction().getReachableStates(
                        model.getStates(), state, actions);
                if (!Collections.disjoint(pC, reachableStates)) {
                    final Transition t = new Transition(state, actions);
                    if (!result.contains(t)) {
                        result.add(t);
                    } else {
                        result.remove(t);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Retorna as transições que alcançam o conjunto da cobertura
     *
     * @param pC
     * @return
     */
    protected Set<Transition> getStrongImage(final Collection<State> pC) {
        final Set<Transition> result = new HashSet<Transition>();

        for (final State state : model.getStates()) {
            for (final Action action : getActionsFrom(state)) {
                final Collection<State> reachableStates = model.getTransitionFunction().getReachableStates(
                        model.getStates(), state, action);
                if (pC.containsAll(reachableStates)) {
                    final Transition t = new Transition(state, action);
                    if (!result.contains(t)) {
                        result.add(t);
                    } else {
                        result.remove(t);
                    }
                }
            }
        }

        return result;
    }

    private Iterable<Action> getActionsFrom(final State pState) {
        return model.getTransitionFunction().getActionsFrom(model.getActions(), pState);
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);
        sb.append("\nGama: ").append(gama);

        return sb.toString();
    }
}