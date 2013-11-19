package org.emast.model.problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public abstract class ProblemFactory {

    private List<State> usedStates;
    private static final Random random = new Random();

    public ProblemFactory() {
    }

    protected State getRandomEmptyState(final MDP pModel) {
        State st;
        do {
            st = getRandomState(pModel);
        } while (usedStates.contains(st));
        //save state as already used
        usedStates.add(st);

        return st;
    }

    public static <M extends MDP & ERG> State getRandomEmptyPropState(final M pModel) {
        State st;

        do {
            st = getRandomState(pModel);
        } while (pModel.getPropositionFunction().getPropositionsForState(st) != null);

        return st;
    }

    public static List<State> getRandomStates(final MDP pModel, int pNum) {
        final List<State> states = new ArrayList<State>();
        for (int i = 0; i < pNum; i++) {
            states.add(getRandomState(pModel));
        }
        return states;
    }

    public List<State> getRandomEmptyStates(final MDP pModel, int pNum) {
        final List<State> states = new ArrayList<State>();
        for (int i = 0; i < pNum; i++) {
            states.add(getRandomEmptyState(pModel));
        }
        return states;
    }

    public static State getRandomState(final MDP pModel) {
        final int index = random.nextInt(pModel.getStates().size());
        return (State) pModel.getStates().toArray()[index];
    }

    protected abstract Problem<?> doCreate();

    public Problem<?> create() {
        usedStates = new ArrayList<State>();
        return doCreate();
    }

    public static Problem<MDP> create(Problem<?> pProblem, MDP pModel) {
        return new Problem<MDP>(pModel, pProblem.getInitialStates(), pProblem.getFinalStates());
    }
}
