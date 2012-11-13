package org.emast.erg.rover;

import java.util.Set;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class RoverModel extends ERGGridModel {

    public static final double BAD_REWARD = -20;
    private static final double OTHERWISE = -1;

    public RoverModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set propositions
        setPropositions(getDefaultPropositions());
        //set goals
        setPreservationGoal(createPreservationGoal());
        setGoal(createFinalGoal());
        //set bad reward function
        setRewardFunction(new RewardFunctionProposition(this,
                CollectionsUtils.createMap(getBadRewardObstacles(), BAD_REWARD), OTHERWISE));
    }

    public static Expression createFinalGoal() {
        return new Expression("exit");
    }

    public static Expression createPreservationGoal() {
        return new Expression("!hole & !stone");
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
