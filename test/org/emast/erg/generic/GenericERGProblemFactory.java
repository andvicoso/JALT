package org.emast.erg.generic;

import java.util.*;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author And
 */
public class GenericERGProblemFactory extends ProblemFactory {

    public static final double CHANCE_OF_HAVING_PROP = 0.33;
    public static final int MAX_PROPS_PER_STATE = 4;
    private final int rows;
    private final int cols;
    private final int agents;
    private final double badReward;
    private final double otherwiseReward;
    private final int numberOfBadProps;
    private final int numberOfPropositions;

    public static ProblemFactory createDefaultFactory() {
        //double agentsRatio = 0.02;
        int rows = 10;
        int cols = rows;
        int props = (int) Math.ceil(rows / 5);
        int agents = 1;//(int) Math.ceil(rows * cols * agentsRatio);
        int numberOfBadProps = (int) Math.ceil(rows / 5);

        return new GenericERGProblemFactory(rows, cols, agents, props, numberOfBadProps,
                BAD_REWARD, OTHERWISE);
    }

    public GenericERGProblemFactory(final int pRows, final int pCols, final int pAgents, final int pPropositions,
            final int pBadProps, final double pBadReward, final double pOtherwiseReward) {
        rows = pRows;
        cols = pCols;
        agents = pAgents;
        numberOfPropositions = pPropositions;
        numberOfBadProps = pBadProps;
        badReward = pBadReward;
        otherwiseReward = pOtherwiseReward;
    }

    @Override
    public Problem doCreate() {
        final GenericERGProblem model = new GenericERGProblem(rows, cols, agents, numberOfPropositions,
                numberOfBadProps, badReward, otherwiseReward);
        final PropositionFunction pf = new PropositionFunction();
        model.setPropositionFunction(pf);
        spreadPropositions(model, pf);
        //put final goal over the grid in a state that doesn`t have a bad rewarder
        State finalState = findBestFinalState(model, pf);
        pf.add(finalState, model.getFinalProp());
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);

        return new Problem<ERG>(model, CollectionsUtils.asIndexMap(initStates), Collections.singleton(finalState));
    }

    private void spreadPropositions(final ERG model, final PropositionFunction pf) {
        //spread propositions over the grid
        for (State s : model.getStates()) {
            //chance of having some props
            if (CHANCE_OF_HAVING_PROP > Math.random()) {
                for (int i = 0; i < Math.random() * MAX_PROPS_PER_STATE; i++) {
                    Proposition prop = CollectionsUtils.getRandom(model.getPropositions());
                    Set<Proposition> sprops = pf.getPropositionsForState(s);
                    if (sprops == null) {
                        sprops = new HashSet<Proposition>();
                    }
                    sprops.add(prop);
                    pf.set(s, sprops);
                }
            }
        }
    }

    private State findBestFinalState(final ERG model, final PropositionFunction pf) {
        State finalState;
        do {
            finalState = CollectionsUtils.getRandom(model.getStates());
            Set<Proposition> propsState = pf.getPropositionsForState(finalState);
            if ((propsState != null && !hasBadProp(propsState)) || propsState == null) {
                break;
            }
        } while (true);
        return finalState;
    }

    private boolean hasBadProp(Set<Proposition> propsState) {
        if (propsState != null) {
            for (Proposition prop : propsState) {
                char v = prop.getName().charAt(0);
                if (Character.isUpperCase(v)) {
                    return true;
                }
            }
        }
        return false;
    }
}
