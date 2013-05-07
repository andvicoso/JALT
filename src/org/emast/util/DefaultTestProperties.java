package org.emast.util;

/**
 *
 * @author Anderson
 */
public final class DefaultTestProperties {

    public static final String FINAL_GOAL = "@";
    
    public static final int MAX_ITERATIONS = 3;
    
    public static final double GOOD_Q_VALUE = 30;
    public static final double GOOD_EXP_VALUE = 30;

    public static final double BAD_Q_VALUE = -30;
    public static final double BAD_REWARD = -30;
    public static final double BAD_EXP_VALUE = -2;//-15;
    public static final double BAD_MSG_VALUE = -20;
    
    public static final double OTHERWISE = -1;
    public static final double MSG_COST = -1;
    
    public static final double ERROR = 0.009;//0.0009
    
    public static final double ALPHA = 0.5;
    public static final double EPSILON = 0.1;
    public static final double GAMA =  0.9d;
}
