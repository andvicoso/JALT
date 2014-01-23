package org.emast.util;

import org.emast.model.algorithm.stoppingcriterium.StopOnMaxDiffError;
import org.emast.model.algorithm.stoppingcriterium.StopOnRMSError;
import org.emast.model.algorithm.stoppingcriterium.StopOnSumMeanError;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriterium;

/**
 * 
 * @author Anderson
 */
public final class DefaultTestProperties {

	public static final String FINAL_GOAL = "@";

	public static final int MAX_ITERATIONS = 100;

	public static final double GOOD_Q_VALUE = 30;
	public static final double GOOD_EXP_VALUE = 30;

	public static final double BAD_Q_PERCENT = 0.3;
	public static final double BAD_Q_VALUE = -30;
	public static final double BAD_REWARD = -30;
	public static final double BAD_EXP_VALUE = -15;
	public static final double BAD_MSG_VALUE = -20;

	public static final double OTHERWISE = -1;
	public static final double MSG_COST = -1;

	public static final double ALPHA = 0.1;
	public static final double EPSILON = 0.1;
	public static final double GAMA = 0.9d;

	public static final double ERROR = 0.009;// 0.09(ERG) and 0.009(MDP)

	public static final StoppingCriterium DEFAULT_STOPON = new StopOnMaxDiffError();
}