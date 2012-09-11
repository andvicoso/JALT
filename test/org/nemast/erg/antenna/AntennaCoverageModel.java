package org.nemast.erg.antenna;

import org.emast.model.function.PropositionFunction;
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

    private PropositionFunction pf;

    public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents,
            final int pAntennaSignalCityBlockRadius) {
        super(pRows, pCols, pAgents);

        String[] props = {"hole", "stone", "water", "exit", "up", "down", "antenna", "coverage"};
        setPropositions(CollectionsUtils.createSet(Proposition.class, props));

        setGoal(new Expression("up & exit"));
        setPreservationGoal(new Expression("!hole & !stone & coverage"));

        setBadRewardProp(new Proposition("water"));
        setBadReward(-30d);
        setOtherwiseValue(-3d);

        AntennaCoverageProblemFactory.createAntennaCoverage(getStates(),
                getPropositionFunction(), new Proposition("antenna"),
                new Proposition("coverage"), pAntennaSignalCityBlockRadius, getPropositions());
    }
}
