package org.emast.erg;

import org.emast.CurrentProblem;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.ensemble.AgentEnsemble;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class EnsembleTest {

    public static void main(String[] args) {
        new Test(CurrentProblem.create(), createAlgorithm()).run();
    }

    public static Algorithm createAlgorithm() {
        PolicyGenerator pg = new PPFERG();

        return new AgentEnsemble(pg);
    }
}
