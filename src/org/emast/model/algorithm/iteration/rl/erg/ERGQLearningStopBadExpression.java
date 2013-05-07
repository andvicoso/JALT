package org.emast.model.algorithm.iteration.rl.erg;

import java.util.Set;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.algorithm.iteration.rl.QLearning;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.chooser.base.MultiChooser;
import org.emast.model.model.ERG;
import org.emast.model.propositional.Expression;

/**
 *
 * @author anderson
 */
public class ERGQLearningStopBadExpression extends QLearning<ERG> {

    protected final double threshold;
    protected final Set<Expression> avoid;

    public ERGQLearningStopBadExpression(ERGQTable q, double threshold, Set<Expression> avoid) {
        super(q);
        this.threshold = threshold;
        this.avoid = avoid;
    }

    public Expression getBadExpression() {
        if (q != null) {
            MultiChooser<Expression> thresholdChooser =
                    new ThresholdChooser<Expression>(threshold, true);
            ERGQTable table = (ERGQTable) q;
            //choose all expressions with rewards mean above the threshold
            Set<Expression> exps = thresholdChooser.choose(table.getExpsValues());
            //remove all the current avoidable expressions
            exps.removeAll(avoid);

            if (!exps.isEmpty()) {
                return exps.iterator().next();
            }
        }
        return null;
    }

    @Override
    protected boolean isStopEpisodes(QTable lastq) {
        return super.isStopEpisodes(lastq) || getBadExpression() != null;
    }
}
