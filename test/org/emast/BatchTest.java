package org.emast;

import org.emast.erg.MainTest;
import org.emast.model.test.Test;

/**
 *
 * @author Anderson
 */
public class BatchTest extends MainTest {

    private int n = 100;

    public BatchTest() {
    }

    public BatchTest(int n) {
        this.n = n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    @Override
    public void run() {
        Test test = new Test(getProblem(), getAlgorithms());
        for (int i = 0; i < n; i++) {
            test.run();
        }
    }

    public static void main(String[] args) {
        final BatchTest bt = new BatchTest();
        bt.run();
    }
}
