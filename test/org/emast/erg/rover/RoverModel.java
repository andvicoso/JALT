package org.emast.erg.rover;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.emast.model.BadReward;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class RoverModel extends ERGGridModel {

    private static final double BAD_REWARD = -20;

    public RoverModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols, pAgents);
        //set propositions
        setPropositions(getDefaultPropositions());
        //set goals
        setPreservationGoal(new Expression("!hole & !stone"));
        setGoal(new Expression("exit"));
        //set bad rewards
        final List<BadReward> list = new ArrayList<BadReward>();
        for (Proposition bdp : getBadRewardObstacles()) {
            list.add(new BadReward(bdp, BAD_REWARD));
        }

        setBadRewards(list);
        setOtherwiseValue(-1);
    }

    public static Set<Proposition> getBadRewardObstacles() {
        String[] props = {"water", "oil"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }

    public static Set<Proposition> getObstacles() {
        String[] props = {"hole", "stone", "water", "oil"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }

    public static Set<Proposition> getDefaultPropositions() {
        String[] props = {"hole", "stone", "water", "oil", "exit"};
        return CollectionsUtils.createSet(Proposition.class, props);
    }
}
