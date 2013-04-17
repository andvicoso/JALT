package org.emast.model.algorithm;

import org.emast.infra.log.Log;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.util.Utils;

/**
 *
 * @author Anderson
 */
public abstract class DefaultAlgorithm<M extends MDP, R> implements Algorithm<M, R> {

    private long init;
    private long end;
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

    protected void initTime() {
        init = System.currentTimeMillis();
    }

    protected void endTime() {
        end = System.currentTimeMillis();
        long diff = end - init;
        Log.info("\nTime: " + Utils.toTimeString(diff));
    }
}
