package org.emast.util;

import java.util.Collection;

/**
 *
 * @author Anderson
 */
public class CalcUtils {

    public static double getStandardDeviation(double mean, Collection<? extends Number> values) {
        double sum = 0;
        for (Number n : values) {
            double v = n.doubleValue();
            sum += (v - mean) * (v - mean);
        }

        return values.isEmpty() ? 0 : Math.sqrt(sum / (values.size() - 1));
    }

    public static double getMean(Collection<? extends Number> steps) {
        double sum = 0;
        for (Number v : steps) {
            sum += v.doubleValue();
        }

        return steps.isEmpty() ? 0 : sum / steps.size();
    }
}
