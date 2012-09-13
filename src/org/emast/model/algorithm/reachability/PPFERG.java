package org.emast.model.algorithm.reachability;

import java.util.*;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;

public class PPFERG<M extends MDP & ERG> extends PPF<M> {

    private int iterations = 0;
    /**
     * if is true, then the algorithm will stop when it finds a valid path to some agent's initial position.
     * Else, it will find all the paths for all valid states.
     */
    private final boolean stopWhenOneAgentFindPath;

    /**
     *
     * @param pStopWhenOneAgentFindPath if is true, then the algorithm will stop when it finds a valid path to
     * some agent's initial position. Else, it will find all the paths for all valid states.
     */
    public PPFERG(final boolean pStopWhenOneAgentFindPath) {
        stopWhenOneAgentFindPath = pStopWhenOneAgentFindPath;
    }

    public PPFERG() {
        this(false);
    }

    @Override
    public Policy run(Problem<M> pProblem) {
        M model = pProblem.getModel();
        //get the initial state for only one agent
        final Collection<State> preserveIntension = intension(model.getPreservationGoal());
        final Collection<State> goalsIntension = intension(model.getGoal());
        Collection<State> c;
        final Map<State, Double> values = new HashMap<State, Double>();
        Policy pi = new Policy();
        Policy pi2;
        //there isn't a state which satisfies the problem goal
        if (goalsIntension.isEmpty()) {
            return pi;
        }

        for (final State state : goalsIntension) {
            values.put(state, 1d);
            pi.put(state, Action.TRIVIAL_ACTION);
        }

        do {
            c = pi.getStates();
            //If the flag "stopWhenOneAgentFindPath" is true, then the algorithm
            //will stop when it finds a valid path to some agent's initial position.
            //Else, it will find all paths for all states.
            if (stopWhenOneAgentFindPath
                    && !Collections.disjoint(c, pProblem.getInitialStates().values())) {
                break;
            }

            pi2 = pi;
            final Collection<Transition> strongImage = getStrongImage(c);
            final Collection<Transition> prunedStrongImage = prune(strongImage,
                    c, preserveIntension);
            pi = choose(values, prunedStrongImage);
            pi.putAll(pi2);
            iterations++;
        } while (!pi.equals(pi2) && !preserveIntension.equals(c));

        return pi;
    }

    protected Set<Transition> prune(final Collection<Transition> pStrongImage,
            final Collection<State> pS, final Collection<State> pI) {
        final Set<Transition> result = new HashSet<Transition>();

        for (final Transition t : pStrongImage) {
            if (!pS.contains(t.getState()) && pI.contains(t.getState())) {
                result.add(t);
            }
        }

        return result;
    }

    public boolean isStopWhenOneAgentFindPath() {
        return stopWhenOneAgentFindPath;
    }

    @Override
    public String printResults() {
        final StringBuilder sb = new StringBuilder();
        sb.append("\nIterations: ").append(iterations);

        return sb.toString();
    }
}
