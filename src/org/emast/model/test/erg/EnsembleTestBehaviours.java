package org.emast.model.test.erg;

import java.util.Arrays;
import java.util.List;
import org.emast.model.combinator.Combinator;
import org.emast.model.agent.behavior.Collective;
import org.emast.model.agent.behavior.Individual;
import org.emast.model.agent.behavior.collective.ChangePreservGoal;
import org.emast.model.agent.behavior.individual.reward.CommReward;
import org.emast.model.agent.behavior.individual.reward.PropRepReward;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.algorithm.ensemble.AgentEnsembleBehavior;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.combinator.MeanValueCombinator;
import org.emast.model.propositional.Proposition;
import static org.emast.util.DefaultTestProperties.*;

/**
 *
 * @author Anderson
 */
public class EnsembleTestBehaviours {

    public static Algorithm createAlgorithm() {
        double badRewardValue = -20;

        PolicyGenerator pg = new PPFERG();
        List<Collective> behaviors = createCollectiveBehaviors(pg, badRewardValue);
        List<Individual> agentBehaviors = createIndividualBehaviors(badRewardValue);

        return new AgentEnsembleBehavior(pg, behaviors, agentBehaviors);
    }

    private static List<Individual> createIndividualBehaviors(double pBadRewardThreshold) {
        Individual propRepRewardBehavior = new PropRepReward(pBadRewardThreshold);
        Individual commRewardBehavior = new CommReward(MSG_COST, BAD_MSG_VALUE, false);

        return Arrays.asList(propRepRewardBehavior);//, commRewardBehavior);
    }

    private static List<Collective> createCollectiveBehaviors(PolicyGenerator pg, double badRewardValue) {
        boolean acceptOnePath = true;
        MultiChooser<Proposition> chooser = new ThresholdChooser<Proposition>(badRewardValue, true);
        Combinator<Proposition> combinator = new MeanValueCombinator();
        Collective change = new ChangePreservGoal(pg, chooser, combinator, acceptOnePath);

        return Arrays.asList(change);
    }
}
