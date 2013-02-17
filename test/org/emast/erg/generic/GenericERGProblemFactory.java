package org.emast.erg.generic;

import java.util.*;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author And
 */
public class GenericERGProblemFactory extends ProblemFactory {

    public static ProblemFactory createDefaultFactory() {
        double obstaclesRatio = 0.2;
        //double agentsRatio = 0.02;
        int rows = 5;
        int cols = rows;
        int props = rows;
        int agents = 1;//(int) Math.ceil(rows * cols * agentsRatio);
        int numberOfBadProps = (int) Math.ceil(props * obstaclesRatio);
        int numberOfObstacles = (int) Math.ceil(rows * cols * obstaclesRatio);
        double badReward = -30;
        double otherwise = -1;

        return new GenericERGProblemFactory(rows, cols, agents, props, numberOfBadProps,
                numberOfObstacles, badReward, otherwise);
    }
    private final int rows;
    private final int cols;
    private final int agents;
    private final int numberOfBadProps;
    private final int numberOfObstacles;
    private final double badReward;
    private final double otherwiseReward;
    private final int numberOfPropositions;

    public GenericERGProblemFactory(final int pRows, final int pCols, final int pAgents, final int pPropositions,
            final int pBadProps, final int pNumberOfObstacles, final double pBadReward, final double pOtherwiseReward) {
        rows = pRows;
        cols = pCols;
        agents = pAgents;
        numberOfPropositions = pPropositions;
        numberOfBadProps = pBadProps;
        numberOfObstacles = pNumberOfObstacles;
        badReward = pBadReward;
        otherwiseReward = pOtherwiseReward;
    }

    @Override
    public Problem doCreate() {
        final GenericERGProblem model = new GenericERGProblem(rows, cols, agents, numberOfPropositions,
                numberOfBadProps, badReward, otherwiseReward);
        final PropositionFunction pf = new PropositionFunction();
        //spread obstacles over the grid
        for (int i = 0; i < numberOfObstacles; i++) {
            pf.add(getRandomEmptyState(model), CollectionsUtils.getRandom(model.getBadRewarders()));
        }
        //spread propositions over the grid
        for (Proposition prop : model.getPropositions()) {
            for (int j = 0; j < (Math.random() * rows * cols) / 2; j++) {
                State s = getRandomState(model);
                Set<Proposition> sprops = pf.getPropositionsForState(s);
                if (sprops == null) {
                    sprops = new HashSet<Proposition>();
                }
                sprops.add(prop);
                pf.removeAll(s);
                pf.add(s, sprops);
            }
        }
        model.setPropositionFunction(pf);
        //put final goal over the grid
        State finalState = CollectionsUtils.getRandom(model.getStates());
        pf.add(finalState, model.getFinalProp());
        //create initial states
        final List<State> initStates = getRandomEmptyStates(model, agents);

        return new Problem<ERG>(model, CollectionsUtils.asIndexMap(initStates), Collections.singleton(finalState));
    }
}
