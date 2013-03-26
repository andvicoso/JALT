package org.emast.erg.generic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.function.reward.RewardFunctionProposition;
import org.emast.model.model.impl.ERGGridModel;
import org.emast.model.propositional.Expression;
import org.emast.model.propositional.Proposition;
import org.emast.util.CollectionsUtils;
import static org.emast.util.DefaultTestProperties.*;
/**
 *
 * @author Anderson
 */
public class GenericERGProblem extends ERGGridModel {

    private final Proposition finalProp;

    public GenericERGProblem(final int pRows, final int pCols, final int pAgents, final int pPropositions,
            final int pObstacles, final double pBadReward, final double pOtherwise) {
        super(pRows, pCols);
        setAgents(pAgents);
        //set props
        HashSet<Proposition> badRewarders = new HashSet<Proposition>(pObstacles);
        Set<Proposition> props = new HashSet<Proposition>(pPropositions);
        setPropositions(props);
        fillPropsAndBadRewarders(pPropositions, pObstacles, props, badRewarders);
        //final goal
        finalProp = new Proposition(FINAL_GOAL);
        setGoal(new Expression(finalProp));
        //add bad reward to bad prop
        Map<Proposition, Double> rws = CollectionsUtils.createMap(badRewarders, pBadReward);
        //add good reward to final prop
        rws.put(finalProp, -pBadReward);

        RewardFunctionProposition rf = new RewardFunctionProposition(this, rws, pOtherwise);
        //set two random propositions as preservation goal  
        //setPreservationGoal(new Expression(BinaryOperator.AND, badRewarders).negate());
        //set bad reward function

        setRewardFunction(rf);
    }

    public Proposition getFinalProp() {
        return finalProp;
    }

    private void fillPropsAndBadRewarders(final int pPropositions, final int pObstacles,
            Set<Proposition> props, HashSet<Proposition> badRewarders) {
        char initProp = 'a';
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
    }
}