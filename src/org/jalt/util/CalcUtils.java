package org.jalt.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author andvicoso
 */
public class CalcUtils {

	public static double getStandardDeviation(double mean, Collection<? extends Number> values) {
		double sum = 0;
		for (Number n : values) {
			double v = n.doubleValue();
			double vm = (v - mean);
			sum += (vm * vm) / values.size() - 1;
		}

		return values.isEmpty() ? 0 : Math.sqrt(sum);
	}

	public static double getMean(Collection<? extends Number> steps) {
		double sum = 0;
		for (Number v : steps) {
			sum += v.doubleValue();
		}

		return steps.isEmpty() ? 0 : sum / steps.size();
	}

	public static long middleAverage(List<Long> timeSum) {
		long sum = 0;
		for (Long l : timeSum) {
			sum += l;
		}
		// remove the greatest and lowest value
		sum -= Collections.max(timeSum);
		sum -= Collections.min(timeSum);

		return sum;
	}
}
