package org.emast.erg.antenna;

import java.util.HashMap;
import java.util.Map;
import org.emast.model.converter.ReinforcementConverter;
import org.emast.model.function.PropositionFunction;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.propositional.Proposition;
import org.emast.model.state.State;
import org.emast.util.GridUtils;

/**
 *
 * @author Anderson
 */
public class AntennaExamples {

    public AntennaExamples() {
    }

    public Problem getICAPS2013() {
        final AntennaCoverageModel model = new AntennaCoverageModel(5, 5, 2, 3);
        final PropositionFunction pf = new PropositionFunction();

        final Proposition hole = new Proposition("hole");
        final Proposition stone = new Proposition("stone");
        final Proposition water = new Proposition("water");
        final Proposition antenna = new Proposition("antenna");
        final Proposition coverage = new Proposition("coverage");
        final Proposition exit = new Proposition("exit");
        final Proposition up = new Proposition("up");
        //spread obstacles over the grid
        pf.add(GridUtils.createGridState(1, 0), hole);
        pf.add(GridUtils.createGridState(1, 1), hole);
        pf.add(GridUtils.createGridState(2, 1), water);
        pf.add(GridUtils.createGridState(0, 3), water);
        pf.add(GridUtils.createGridState(2, 4), stone);
        pf.add(GridUtils.createGridState(3, 1), stone);
        pf.add(GridUtils.createGridState(0, 4), exit);
        pf.add(GridUtils.createGridState(0, 4), up);
        //distribute antennas over the grid
        pf.add(GridUtils.createGridState(2, 0), antenna);
        pf.add(GridUtils.createGridState(3, 3), antenna);
        pf.add(GridUtils.createGridState(0, 3), antenna);

        model.setPropositionFunction(pf);

        AntennaCoverageProblemFactory.createAntennaCoverage(model.getStates(), pf, antenna, coverage, 3);

        final Map<Integer, State> initialStates = new HashMap<Integer, State>();
        initialStates.put(0, GridUtils.createGridState(4, 0));
        initialStates.put(1, GridUtils.createGridState(0, 0));

        model.setRewardFunction(ReinforcementConverter.convertRewardFunction(model,
                AntennaCoverageModel.BAD_REWARD,
                AntennaCoverageModel.getBadRewardObstacles()));

        return new Problem<ERG>(model, initialStates);
    }
}
