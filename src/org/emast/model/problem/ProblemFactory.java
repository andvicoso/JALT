package org.emast.model.problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.emast.model.model.ERG;
import org.emast.model.model.impl.MDPModel;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public abstract class ProblemFactory {

    private List<State> usedStates;
    private static final Random random = new Random();

    public ProblemFactory() {
        usedStates = new ArrayList<State>();
    }

    protected State getRandomEmptyState(final MDPModel pModel) {
        State st;
        do {
            st = getRandomState(pModel);
        } while (usedStates.contains(st));
        //save state as already used
        usedStates.add(st);

        return st;
    }

    protected static <O> O getRandom(final Collection<O> pObjects) {
        int r = random.nextInt(pObjects.size());
        O obj = null;

        if (pObjects instanceof List) {
            obj = ((List<O>) pObjects).get(r);
        } else {
            int i = 0;
            for (O o : pObjects) {
                obj = o;
                if (i == r) {
                    break;
                }
            }
        }

        return obj;
    }

    public static <M extends MDPModel & ERG> State getRandomEmptyPropState(final M pModel) {
        State st;

        do {
            st = getRandomState(pModel);
        } while (pModel.getPropositionFunction().getPropositionsForState(st) != null);

        return st;
    }

    public static List<State> getRandomStates(final MDPModel pModel, int pNum) {
        final List<State> states = new ArrayList<State>();
        for (int i = 0; i < pNum; i++) {
            states.add(getRandomState(pModel));
        }
        return states;
    }

    public List<State> getRandomEmptyStates(final MDPModel pModel, int pNum) {
        final List<State> states = new ArrayList<State>();
        for (int i = 0; i < pNum; i++) {
            states.add(getRandomEmptyState(pModel));
        }
        return states;
    }

    protected static State getRandomState(final MDPModel pModel) {
        final int index = random.nextInt(pModel.getStates().size());
        return (State) pModel.getStates().toArray()[index];
    }

    public List<State> getUsedStates() {
        return usedStates;
    }

    public static Random getRandom() {
        return random;
    }
}
