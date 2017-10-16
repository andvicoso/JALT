package org.jalt.test.erg;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunctionProposition;
import org.jalt.model.function.reward.RewardFunctionState;
import org.jalt.model.function.transition.GridTransitionFunctionState;
import org.jalt.model.model.ERG;
import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.GridState;
import org.jalt.model.state.State;

import static org.jalt.util.DefaultTestProperties.*;

/**
 *
 * @author andvicoso
 */
public class SuttonDynaMazeTests {

    public static Problem<?> getERGFigure95Example() {
        int rows = 6;
        int cols = 9;

        final ERG model = new ERGGridModel(rows, cols);
        final State goalState = new GridState(0, 8);

        final PropositionFunction pf = new PropositionFunction();
        final Proposition wall = new Proposition("wall");
        final Proposition goal = new Proposition(FINAL_GOAL);

        final Set<Proposition> props = new HashSet<Proposition>();
        props.add(goal);
        props.add(wall);
        //spread obstacles over the grid
        for (State st : getObstacles()) {
            pf.add(st, wall);
        }
        pf.add(goalState, goal);

        final Map<Integer, State> initialStates = new HashMap<Integer, State>();
        initialStates.put(0, new GridState(2, 0));

        final Set<State> finalStates = new HashSet<State>();
        finalStates.add(goalState);

        final Map<Proposition, Double> map = new HashMap<Proposition, Double>();
        map.put(goal, 1d);
        //map.put(wall, -1d);

        model.setPropositions(props);
        model.setPropositionFunction(pf);
        model.setRewardFunction(new RewardFunctionProposition(model, map, 0));
        model.setGoal(new Expression(goal));
        //model.setPreservationGoal(new Expression(wall).negate());

        return new Problem(model, initialStates, finalStates);
    }

    public static Problem getMDPFigure95Example() {
        int rows = 6;
        int cols = 9;
        final State goalState = new GridState(0, 8);
        final Set<State> obstacles = getObstacles();

        final Map<Integer, ? extends State> initialStates = Collections.singletonMap(0, new GridState(2, 0));
        final Set<State> finalStates = Collections.singleton(goalState);
        final Map<State, Double> map = Collections.singletonMap(goalState, 1d);

        final MDP model = new GridModel(rows, cols);
        model.setTransitionFunction(new GridTransitionFunctionState(rows, cols, obstacles, finalStates));
        model.setRewardFunction(new RewardFunctionState(model, map, 0));

        return new Problem(model, initialStates, finalStates);
    }

    protected static Set<State> getObstacles() {
        final Set<State> obstacles = new HashSet<State>();
        obstacles.add(new GridState(1, 2));
        obstacles.add(new GridState(2, 2));
        obstacles.add(new GridState(3, 2));
        obstacles.add(new GridState(4, 5));
        obstacles.add(new GridState(0, 7));
        obstacles.add(new GridState(1, 7));
        obstacles.add(new GridState(2, 7));
        return obstacles;
    }
}
