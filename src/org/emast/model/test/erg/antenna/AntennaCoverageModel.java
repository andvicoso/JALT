package org.emast.model.test.erg.antenna;

import java.util.Set;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;

/**
 * Problem that represents a grid with propositions. The agent must find the path to exit up,
 * avoid the obstacles and keep itself inside an antenna coverage signal.
 *
 * Grid Caption: 0-9: Initial agent x position h: hole s: stone w: water a: antenna c: antenna coverage u:
 * exit up d: exit down
 *
 * @author anderson
 */
public class AntennaCoverageModel extends ERGGridModel {

    public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set props
        String[] propsStr = {"hole", "stone", "water", "exit", "up", "down", "antenna", "coverage"};
        Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propsStr);
        setPropositions(props);
        //set goals
        setGoal(new Expression("up & exit"));
        setPreservationGoal(new Expression("!hole & !stone & coverage"));
        //set bad reward function
        setRewardFunction(new RewardFunctionProposition(this,
                CollectionsUtils.createMap(getBadRewardObstacles(), BAD_REWARD), OTHERWISE));
    }

    public static Set<Proposition> getBadRewardObstacles() {
        String[] props = {"water"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }
}
