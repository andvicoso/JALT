package org.emast.erg;

import java.util.Arrays;
import java.util.List;
import org.emast.erg.rover.RoverProblemFactory;
import org.emast.model.agent.behaviour.Collective;
import org.emast.model.agent.behaviour.Individual;
import org.emast.model.agent.behaviour.collective.ChangePreservGoal;
import org.emast.model.agent.behaviour.individual.reward.CommReward;
import org.emast.model.agent.behaviour.individual.reward.PropRepReward;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.planning.AgentGroup;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.planning.propositionschooser.CombinePropsRewardChooser;
import org.emast.model.planning.propositionschooser.PropositionsChooser;
import org.emast.model.planning.rewardcombinator.MeanRewardCombinator;
import org.emast.model.problem.Problem;
import org.emast.model.problem.ProblemFactory;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;
import org.emast.util.RandomProblemGenerator;

/**
 *
 * @author Anderson
 */
public class AgentGroupTest {

    public static final String CURRENT_PROBLEM = "problems/RoverModel/82_problem.emast";

    public static void main(String[] args) {
        new Test(createProblem(), createAlgorithm()).run();
    }

    public static Algorithm createAlgorithm() {
        int maxIterations = 3;
        double badRewardValue = -20;

        PolicyGenerator pg = new PPFERG();
        List<Collective> behaviours = createCollectiveBehaviours(pg, badRewardValue);
        List<Individual> agentBehaviours = createIndividualBehaviours(badRewardValue);

        return new AgentGroup(pg, behaviours, agentBehaviours, maxIterations);//new Planner(pg, factory.createAgents(agents));//
    }

    public static Problem createProblem() {
        ProblemFactory factory = RoverProblemFactory.createDefaultFactory();
        RandomProblemGenerator rpg = new RandomProblemGenerator(factory);

        return FileUtils.fromFile(CURRENT_PROBLEM);
    }

    private static List<Individual> createIndividualBehaviours(double pBadRewardThreshold) {
        double badMsgValue = -20;
        double messageCost = -1;

        Individual propRepRewardBehaviour = new PropRepReward(pBadRewardThreshold);
        Individual commRewardBehaviour = new CommReward(messageCost, badMsgValue, false);

        return Arrays.asList(propRepRewardBehaviour, commRewardBehaviour);
    }

    private static List<Collective> createCollectiveBehaviours(PolicyGenerator pg, double badRewardValue) {
        boolean acceptOnePath = true;
        PropositionsChooser chooser = new CombinePropsRewardChooser(new MeanRewardCombinator(), badRewardValue);
        Collective change = new ChangePreservGoal(pg, chooser, acceptOnePath);

        return Arrays.asList(change);
    }
}
