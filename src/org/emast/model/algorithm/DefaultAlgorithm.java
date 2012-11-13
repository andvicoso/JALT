package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;

/**
 *
 * @author Anderson
 */
public abstract class DefaultAlgorithm<M extends MDP, R> implements Algorithm<M, R> {

    private boolean debug = true;

    @Override
    public abstract R run(Problem<M> pProblem, Object... pParameters);

    protected void print(String pMsg) {
        if (debug) {
            Log.info(pMsg);
        }
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
