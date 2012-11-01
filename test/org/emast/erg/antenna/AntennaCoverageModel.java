package org.emast.erg.antenna;

import java.util.Collections;
import org.emast.model.BadReward;
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

    public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents,
            final int pAntennaSignalCityBlockRadius) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set props
        String[] props = {"hole", "stone", "water", "exit", "up", "down", "antenna", "coverage"};
        setPropositions(CollectionsUtils.createSet(Proposition.class, props));
        //set goals
        setGoal(new Expression("up & exit"));
        setPreservationGoal(new Expression("!hole & !stone & coverage"));
        //set bad rewards
        final BadReward badReward = new BadReward(new Proposition("water"), -30);
        setBadRewards(Collections.singleton(badReward));
        setOtherwiseValue(-1);
        //create antenna coverage
        AntennaCoverageProblemFactory.createAntennaCoverage(getStates(),
                getPropositionFunction(), new Proposition("antenna"),
                new Proposition("coverage"), pAntennaSignalCityBlockRadius, getPropositions());
    }
}
