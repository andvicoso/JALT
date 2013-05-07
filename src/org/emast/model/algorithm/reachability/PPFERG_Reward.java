package org.emast.model.algorithm.reachability;

import java.util.*;
import org.emast.infra.log.Log;
import org.emast.model.action.Action;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.model.transition.Transition;

public class PPFERG_Reward<M extends ERG> extends PPF<M> {

    /**
     * if is true, then the algorithm will stop when it finds a valid path to some agent's initial position. Else, it
     * will find all the paths for all valid states.
     */
    private final boolean stopWhenOneAgentFindPath;

    /**
     *
     * @param pStopWhenOneAgentFindPath if is true, then the algorithm will stop when it finds a valid path to some
     * agent's initial position. Else, it will find all the paths for all valid states.
     */
    public PPFERG_Reward(final boolean pStopWhenOneAgentFindPath) {
        stopWhenOneAgentFindPath = pStopWhenOneAgentFindPath;
    }

    public PPFERG_Reward() {
        this(false);
    }

    @Override
    public Policy run(Problem<M> pProblem, Object... pParameters) {
        //get model
        model = pProblem.getModel();
        //print initial msg
        String modelName = model.getClass().getSimpleName();
        Log.info("\nRunning PPFERG for " + modelName);
        //get the initial state for only one agent
        final Collection<State> preserveIntension = intension(model.getPreservationGoal());
        final Collection<State> goalsIntension = intension(model.getGoal());
        final Collection<State> initialStates = pProblem.getInitialStates().values();
        final Map<State, Double> values = new HashMap<State, Double>();
        Collection<State> c;
        Policy pi = new Policy();
        Policy pi2;
        //there isn't a state which satisfies the problem goal
        if (goalsIntension.isEmpty()) {
            return pi;
        }

        for (final State state : goalsIntension) {
            values.put(state, INITIAL_VALUE);
            pi.put(state, Action.TRIVIAL_ACTION, INITIAL_VALUE);
        }

        do {
            c = pi.getStates();
            //If the flag "stopWhenOneAgentFindPath" is true, then the algorithm
            //will stop when it finds a valid path to some agent's initial position.
            //Else, it will find all paths for all states.
            if (stopWhenOneAgentFindPath
                    && !Collections.disjoint(c, initialStates)) {
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
}
