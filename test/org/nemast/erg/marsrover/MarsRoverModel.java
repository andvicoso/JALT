package org.nemast.erg.marsrover;

import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class MarsRoverModel extends ERGGridModel {

    public MarsRoverModel(final int pRows, final int pCols, final int pAgents) {
        super(pRows, pCols, pAgents);
        //set propositions
        String[] props = {"hole", "stone", "water", "exit"};
        setPropositions(CollectionsUtils.createSet(Proposition.class, props));
        //set goals
        setPreservationGoal(new Expression("!hole & !stone"));
        setGoal(new Expression("exit"));
        //reward
        setBadReward(-20d);
        setOtherwiseValue(-1d);
        setBadRewardProp(new Proposition("water"));
    }
}
