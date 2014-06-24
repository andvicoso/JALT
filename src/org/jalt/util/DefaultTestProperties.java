package org.jalt.util;

import org.jalt.model.algorithm.stoppingcriterium.StopOnRMSError;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriterium;

/**
 * 
 * @author andvicoso
 */
public final class DefaultTestProperties {

	public static final String FINAL_GOAL = "@";

	public static final int MAX_ITERATIONS = 100;

	public static final double BAD_Q_PERCENT = 0.3;
	public static final double BAD_Q_VALUE = -30;
	public static final double BAD_REWARD = -30;
	public static final double BAD_EXP_VALUE = -15;
	public static final double BAD_MSG_VALUE = -20;
	
	public static final double GOOD_Q_VALUE = -BAD_Q_VALUE;
	public static final double GOOD_EXP_VALUE = -BAD_EXP_VALUE;
	public static final double GOOD_REWARD = -BAD_REWARD;

	public static final double OTHERWISE = -1;
	public static final double MSG_COST = -1;

	public static final double ALPHA = 0.1;
	public static final double EPSILON = 0.1;
	public static final double GAMA = 0.9;

	public static final double ERROR = 0.09;// 0.09(ERG) and 0.009(MDP)

	public static final StoppingCriterium DEFAULT_STOPON = new StopOnRMSError();
}