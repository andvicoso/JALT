package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.emast.model.action.Action;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.propositional.Expression;
import org.emast.model.state.State;

/**
 *
 * @author anderson
 */
public class ERGQLearningBlockExpression extends ERGQLearningStopBadExpression {

    private final Map<State, Action> blocked;

    public ERGQLearningBlockExpression(ERGQTable q, double threshold, Set<Expression> avoid,
            Map<State, Action> blocked) {
        super(q, threshold, avoid);
        this.blocked = blocked;
    }

    @Override
    protected Action getAction(State state) {
        Action action = null;
        Collection<Action> valid = new HashSet<Action>();
        Map<Action, Double> values = model.getTransitionFunction().getActionValues(model.getActions(), state);

        for (Action act : values.keySet()) {
            if (values.get(act) > 0) {
                valid.add(act);
            }
        }

        if (!valid.isEmpty() && !blocked.keySet().containsAll(valid)) {
            do {
                action = super.getAction(state);
            } while (blocked.containsKey(state) && blocked.get(state).equals(action));
        }

        return action;
    }
}
