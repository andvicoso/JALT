package org.emast;

import org.emast.erg.AgentGroupTest;
import org.emast.model.algorithm.Algorithm;
import org.emast.model.algorithm.reachability.PPFERG;
import org.emast.model.problem.Problem;
import org.emast.model.test.Test;
import org.emast.util.FileUtils;

/**
 *
 * @author Anderson
 */
public class MultipleTest {

    private static Algorithm createAgentGroup() {
        return AgentGroupTest.createAlgorithm();
    }

    private static Algorithm[] createAlgorithms() {
        return new Algorithm[]{new PPFERG(), createAgentGroup()};
    }

    private static Problem createProblem() {
        return FileUtils.fromFile(AgentGroupTest.CURRENT_PROBLEM);
    }

    public static void main(String[] args) {
        new Test(createProblem(), createAlgorithms()).run();
    }
}
