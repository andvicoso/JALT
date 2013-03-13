package org.emast.erg;

import java.util.Arrays;
import java.util.List;
import org.emast.CurrentProblem;
import org.emast.model.agent.behavior.Collective;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.agent.behavior.collective.ChangePreservGoal;
import org.emast.model.agent.behavior.individual.reward.CommReward;
import org.emast.model.agent.behavior.individual.reward.PropRepReward;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.planning.chooser.CombineRewardChooser;
import org.emast.model.Chooser;
import org.emast.model.algorithm.ensemble.AgentEnsembleBehavior;
import org.emast.model.planning.rewardcombinator.MeanValueCombinator;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class EnsembleTestBehaviours {

    public static void main(String[] args) {
        new Test(CurrentProblem.create(), createAlgorithm()).run();
    }

    public static Algorithm createAlgorithm() {
        double badRewardValue = -20;

        PolicyGenerator pg = new PPFERG();
        List<Collective> behaviors = createCollectiveBehaviors(pg, badRewardValue);
        List<Individual> agentBehaviors = createIndividualBehaviors(badRewardValue);

        return new AgentEnsembleBehavior(pg, behaviors, agentBehaviors);
    }

    private static List<Individual> createIndividualBehaviors(double pBadRewardThreshold) {
        double badMsgValue = -20;
        double messageCost = -1;

        Individual propRepRewardBehavior = new PropRepReward(pBadRewardThreshold);
        Individual commRewardBehavior = new CommReward(messageCost, badMsgValue, false);

        return Arrays.asList(propRepRewardBehavior);//, commRewardBehavior);
    }

    private static List<Collective> createCollectiveBehaviors(PolicyGenerator pg, double badRewardValue) {
        boolean acceptOnePath = true;
        Chooser chooser = new CombineRewardChooser(new MeanValueCombinator(), badRewardValue);
        Collective change = new ChangePreservGoal(pg, chooser, acceptOnePath);

        return Arrays.asList(change);
    }
}
