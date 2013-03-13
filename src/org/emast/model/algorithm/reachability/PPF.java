package org.emast.model.algorithm.reachability;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.emast.model.action.Action;
import org.emast.model.agent.behavior.individual.reward.PropRepReward;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.exception.InvalidExpressionException;
import org.emast.model.function.transition.TransitionFunction;
import org.emast.model.model.MDP;
import org.emast.model.model.SRG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Expression;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;
import org.emast.util.ModelUtils;

/**
 * Strong probabilistic planning algorithm
 *
 * @author Anderson
 * @param <P> Simple reachability problem to be resolved
 */
public class PPF<M extends MDP & SRG> implements PolicyGenerator<M> {

    protected M model;
    private double gama = 0.9;
    protected int iterations = 0;
    protected static final Double INITIAL_VALUE = 1d;

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
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
            values.put(state, INITIAL_VALUE);
            pi.put(state, Action.TRIVIAL_ACTION, INITIAL_VALUE);
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
        final TransitionFunction tf = model.getTransitionFunction();

        for (final State state : ModelUtils.getStates(pPrune)) {
            final Map<Action, Double> q = new HashMap<Action, Double>();
            // search for the Qs values for state
            for (final Action action : getActions(pPrune, state)) {
                double sum = 0;
                for (final State reachableState : tf.getReachableStates(model.getStates(), state, action)) {
                    final double trans = tf.getValue(state, reachableState, action);
                    if (pValues.get(reachableState) != null) {
                        sum += trans * pValues.get(reachableState);
                    }
                }
                q.put(action, gama * sum);
            }
            //if found something
            if (q.size() > 0) {
                // get the max value for q
                final Double max = Collections.max(q.values());
                pValues.put(state, max);
                pi.put(state, q);
            }
        }

        return pi;
    }

    protected Collection<State> intension(final Expression pExpression) {
        try {
            return model.getPropositionFunction().intension(model.getStates(),
                    model.getPropositions(), pExpression);
        } catch (InvalidExpressionException ex) {
            Logger.getLogger(PropRepReward.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.emptyList();
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

    private Collection<Action> getActions(Collection<Transition> pPrune, State state) {
        Collection<Action> actions = new ArrayList<Action>();
        for (Transition t : pPrune) {
            if (t.getState().equals(state)) {
                actions.add(t.getAction());
            }
        }

        return actions;
    }
}
