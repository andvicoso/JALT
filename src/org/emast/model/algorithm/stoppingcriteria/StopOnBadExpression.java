package org.emast.model.algorithm.stoppingcriteria;

import java.util.Set;
import org.emast.model.algorithm.iteration.rl.AbstractRLearning;
import org.emast.model.chooser.NotInChooser;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.chooser.ThresholdChooser;
import org.emast.model.propositional.Expression;

/**
 *
 * @author anderson
 */
public class StopOnBadExpression implements StoppingCriterium {

    private final NotInChooser chooser;
    private final StoppingCriterium delegate;

    public StopOnBadExpression(double threshold, Set<Expression> avoid) {
        this.chooser = new NotInChooser(new ThresholdChooser<Expression>(threshold, true), avoid);
        this.delegate = new StopOnError();
    }

    @Override
    public boolean isStopEpisodes(IterationValues iterationValues) {
        boolean exp = false;
        if (iterationValues instanceof AbstractRLearning) {
            final AbstractRLearning alg = (AbstractRLearning) iterationValues;
            if (alg.getQTable() instanceof ERGQTable) {
                final ERGQTable q = (ERGQTable) alg.getQTable();
                exp = chooser.chooseOne(q.getExpsValues()) != null;
            }
        }

        return delegate.isStopEpisodes(iterationValues) || exp;
    }
}
