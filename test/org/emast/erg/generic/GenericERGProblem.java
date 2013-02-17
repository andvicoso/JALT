package org.emast.erg.generic;

import java.util.HashSet;
import java.util.Set;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author Anderson
 */
public class GenericERGProblem extends ERGGridModel {

    private final HashSet<Proposition> badRewarders;
    private final Proposition finalProp;

    public GenericERGProblem(final int pRows, final int pCols, final int pAgents, final int pPropositions,
            final int pObstacles, final double pBadReward, final double pOtherwise) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set props
        badRewarders = new HashSet<Proposition>(pObstacles);
        Set<Proposition> props = new HashSet<Proposition>(pPropositions);
        char initProp = 'b';
        for (int i = 0; i < pPropositions; i++) {
            char c = (char) (initProp + i);
            String cs = c + "";
            if (i < pObstacles) {
                cs = cs.toUpperCase();
            }

            final Proposition proposition = new Proposition(cs);
            props.add(proposition);

            if (i < pObstacles) {
                badRewarders.add(proposition);
            }
        }
        setPropositions(props);
        //set a random proposition as final goal 
        finalProp = new Proposition("a");//CollectionsUtils.getRandom(props);
        setGoal(new Expression(finalProp));
        //set two random propositions as preservation goal  
        //setPreservationGoal(new Expression(BinaryOperator.AND, badRewarders).negate());
        //set bad reward function
        setRewardFunction(new RewardFunctionProposition(this,
                CollectionsUtils.createMap(badRewarders, pBadReward), pOtherwise));
    }

    public HashSet<Proposition> getBadRewarders() {
        return badRewarders;
    }

    public Proposition getFinalProp() {
        return finalProp;
    }
}