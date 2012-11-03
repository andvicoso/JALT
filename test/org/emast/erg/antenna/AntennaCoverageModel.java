package org.emast.erg.antenna;

import java.util.Set;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 * Problem that represents a grid with holes, stones and antennas. The agent must find the path to exit up,
 * avoid the obstacles and keep itself inside an antenna coverage signal.
 *
 * Grid Caption: 0-9: Initial agent x position h: hole s: stone w: water a: antenna c: antenna coverage u:
 * exit up d: exit down
 *
 * @author anderson
 */
public class AntennaCoverageModel extends ERGGridModel {

    private static final int BAD_REWARD = -30;
    private static final double OTHERWISE = -1;

    public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents,
            final int pAntennaSignalCityBlockRadius) {
        super(pRows, pCols);
        setAgents(pAgents);
        //
        String[] brs = {"water"};
        Set<Proposition> br = CollectionsUtils.createSet(Proposition.class, brs);
        //set props
        String[] propss = {"hole", "stone", "water", "exit", "up", "down", "antenna", "coverage"};
        Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propss);
        setPropositions(props);
        //set goals
        setGoal(new Expression("up & exit"));
        setPreservationGoal(new Expression("!hole & !stone & coverage"));
        //set bad reward function
        setRewardFunction(new RewardFunctionProposition(this,
                CollectionsUtils.createMap(br, BAD_REWARD), OTHERWISE));
        //create antenna coverage
        AntennaCoverageProblemFactory.createAntennaCoverage(getStates(),
                getPropositionFunction(), new Proposition("antenna"),
                new Proposition("coverage"), pAntennaSignalCityBlockRadius, getPropositions());
    }
}
