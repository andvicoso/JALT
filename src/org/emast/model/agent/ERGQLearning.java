package org.emast.model.agent;

import java.util.Map;
import org.emast.model.action.Action;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.model.ERG;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CollectionsUtils;

/**
 *
 * @author anderson
 */
public class ERGQLearning extends QLearning<ERG> {

    private Policy policy;

    @Override
    protected Action getAction(State state) {
        Map<Action, Double> values = policy.get(state);
        return values == null ? null : CollectionsUtils.draw(values);
    }

    @Override
    public Policy run(Problem<ERG> pProblem, Object... pParameters) {
        policy = (Policy) pParameters[0];
        return super.run(pProblem, pParameters);
    }
}
