package org.emast.erg.rover;

import java.util.Arrays;
import java.util.List;
import org.emast.model.agent.behaviour.CollectiveBehaviour;
import org.emast.model.agent.behaviour.IndividualBehaviour;
import org.emast.model.agent.behaviour.collective.ChangePreservGoal;
import org.emast.model.agent.behaviour.individual.reward.CommRewardBehaviour;
import org.emast.model.agent.behaviour.individual.reward.PropRepRewardBehaviour;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.planning.AgentGroup;
import org.emast.model.algorithm.planning.PolicyGenerator;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.comm.MessageManager;
import org.emast.model.model.ERG;
import org.emast.model.planning.propositionschooser.CombinePropsChooser;
import org.emast.model.planning.propositionschooser.PropositionsChooser;
import org.emast.model.planning.rewardcombinator.MeanRewardCombinator;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.RandomProblemGenerator;

/**
 *
 * @author Anderson
 */
public class RoverExecutorTest {

    private static Algorithm createAlgorithm() {
        int maxIterations = 1;
        double badRewardValue = -20;

        PolicyGenerator<ERG> pg = new PPFERG<ERG>();
        List<CollectiveBehaviour<ERG>> behaviours = createCollectiveBehaviours(badRewardValue);
        List<IndividualBehaviour<ERG>> agentBehaviours = createIndividualBehaviours(badRewardValue);

        return new AgentGroup<ERG>(pg, behaviours, agentBehaviours, maxIterations);//new Planner(pg, factory.createAgents(agents));//
    }

    private static Problem createProblem() {
        int rows = 10;
        int cols = 10;
        int size = rows * cols;
        int obstacles = (int) (0.3 * size);
        int agents = (int) (0.15 * size);

        RoverProblemFactory factory = new RoverProblemFactory(rows, cols, agents, obstacles);
        RandomProblemGenerator rpg = new RandomProblemGenerator(factory);

        return rpg.run();//FileUtils.fromFile("problems/RoverModel/problem9.emast");//
    }

    public static void main(String[] args) {
        new Test(createProblem(), createAlgorithm()).run();
    }

    private static List<IndividualBehaviour<ERG>> createIndividualBehaviours(double pBadRewardThreshold) {
        double badMsgValue = -20;
        double messageCost = -1;

        IndividualBehaviour<ERG> propRepRewardBehaviour =
                new PropRepRewardBehaviour(pBadRewardThreshold);
        IndividualBehaviour<ERG> commRewardBehaviour =
                new CommRewardBehaviour(messageCost, badMsgValue, new MessageManager(false));

        return Arrays.asList(propRepRewardBehaviour, commRewardBehaviour);
    }

    private static List<CollectiveBehaviour<ERG>> createCollectiveBehaviours(double badRewardValue) {
        PropositionsChooser chooser = new CombinePropsChooser(new MeanRewardCombinator(), badRewardValue);
        CollectiveBehaviour<ERG> change = new ChangePreservGoal(chooser);

        return Arrays.asList(change);
    }
}
