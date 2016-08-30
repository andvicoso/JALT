package org.jalt.model.test.mdp.ai;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jalt.model.action.Action;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

public class InvertedPendulum {

	public static void main(String[] args) {
		new Rn().run(new InvertedPendulum(), null);
	}
}

class Rn {
	private static final int MAX_EP = 10000;
	// Constants
	private static final double cartMass = 1.;
	private static final double poleMass = 0.1;
	private static final double poleLength = 1.;
	private static final double forceMag = 10.;
	private static final double tau = 0.02;
	private static final double fricCart = 0.00005;
	private static final double fricPole = 0.005;
	private static final double totalMass = cartMass + poleMass;
	private static final double halfPole = 0.5 * poleLength;
	private static final double poleMassLength = halfPole * poleMass;
	private static final double fourthirds = 4. / 3.;
	/**
	 * Discount factor The discount factor determines the importance of future
	 * rewards. A factor of 0 will make the agent "opportunistic" by only
	 * considering current rewards, while a factor approaching 1 will make it
	 * strive for a long-term high reward. If the discount factor meets or
	 * exceeds 1, the values may diverge.
	 */
	private static final double GAMA = 0.9;
	/**
	 * The learning rate. The learning rate determines to what extent the newly
	 * acquired information will override the old information. A factor of 0
	 * will make the agent not learn anything, while a factor of 1 would make
	 * the agent consider only the most recent information.
	 */
	private double ALPHA = 0.9;
	// attr
	private final Set<State> states;
	private final Action left = new Action("left");
	private final Action right = new Action("right");
	private final List<Action> actions = Arrays.asList(left, right);
	protected double[][] q;

	private double pos, posDot, angle, angleDot;

	private int episodes;
	private int steps;

	public Rn() {
		states = createStates();

		q = new double[states.size()][2];

		for (int i = 0; i < states.size(); i++) {
			q[i][0] = 0;
			q[i][1] = 0;
		}
	}

	private Set<State> createStates() {
		final Set<State> sts = new HashSet<State>();
		// velocity ranges
		Range x1 = new Range(-2.4, -0.8);
		Range x2 = new Range(-0.8, 0.8);
		Range x3 = new Range(0.8, 2.4);
		// velocity dot ranges
		Range xd1 = new Range(-Double.MAX_VALUE, -0.5);
		Range xd2 = new Range(-0.5, 0.5);
		Range xd3 = new Range(0.5, Double.MAX_VALUE);
		// angle ranges
		Range t1 = new Range(-90, -6);
		Range t2 = new Range(-6, -1);
		Range t3 = new Range(-1, 0);
		Range t4 = new Range(0, 1);
		Range t5 = new Range(1, 6);
		Range t6 = new Range(6, 90);
		// angle dot ranges
		Range td1 = new Range(-Double.MAX_VALUE, -50);
		Range td2 = new Range(-50, 50);
		Range td3 = new Range(50, Double.MAX_VALUE);

		for (Range rangex : Arrays.asList(x1, x2, x3)) {
			for (Range rangexd : Arrays.asList(xd1, xd2, xd3)) {
				for (Range ranget : Arrays.asList(t1, t2, t3, t4, t5, t6)) {
					for (Range rangetd : Arrays.asList(td1, td2, td3)) {
						sts.add(new PoleState(rangex, rangexd, ranget, rangetd));
					}
				}
			}
		}
		return sts;
	}

	private Set<State> createStatesUniform() {
		final Set<State> sts = new HashSet<State>();
		// velocity ranges
		Range x1 = new Range(-2.4, -0.8);
		Range x2 = new Range(-0.8, 0.8);
		Range x3 = new Range(0.8, 2.4);
		// velocity dot ranges
		Range xd1 = new Range(-10, -3);
		Range xd2 = new Range(-3, 3);
		Range xd3 = new Range(3, 10);
		// angle ranges
		Range t1 = new Range(-90, -60);
		Range t2 = new Range(-60, -30);
		Range t3 = new Range(-30, 0);
		Range t4 = new Range(0, 30);
		Range t5 = new Range(30, 60);
		Range t6 = new Range(60, 90);
		// angle dot ranges
		Range td1 = new Range(-10, -3);
		Range td2 = new Range(-3, 3);
		Range td3 = new Range(3, 10);

		for (Range rangex : Arrays.asList(x1, x2, x3)) {
			for (Range rangexd : Arrays.asList(xd1, xd2, xd3)) {
				for (Range ranget : Arrays.asList(t1, t2, t3, t4, t5, t6)) {
					for (Range rangetd : Arrays.asList(td1, td2, td3)) {
						sts.add(new PoleState(rangex, rangexd, ranget, rangetd));
					}
				}
			}
		}
		return sts;
	}
	
	private Set<State> createStatesDiscreteUniform() {
		final Set<State> sts = new HashSet<State>();
		// velocity ranges
		Range x1 = new Range(-2.4, -0.8);
		Range x2 = new Range(-0.8, 0.8);
		Range x3 = new Range(0.8, 2.4);
		// velocity dot ranges
		Range xd1 = new Range(-10, -3);
		Range xd2 = new Range(-3, 3);
		Range xd3 = new Range(3, 10);
		// angle ranges
		Range t1 = new Range(-90, -60);
		Range t2 = new Range(-60, -30);
		Range t3 = new Range(-30, 0);
		Range t4 = new Range(0, 30);
		Range t5 = new Range(30, 60);
		Range t6 = new Range(60, 90);
		// angle dot ranges
		Range td1 = new Range(-10, -3);
		Range td2 = new Range(-3, 3);
		Range td3 = new Range(3, 10);

		for (Range rangex : Arrays.asList(x1, x2, x3)) {
			for (Range rangexd : Arrays.asList(xd1, xd2, xd3)) {
				for (Range ranget : Arrays.asList(t1, t2, t3, t4, t5, t6)) {
					for (Range rangetd : Arrays.asList(td1, td2, td3)) {
						sts.add(new PoleState(rangex, rangexd, ranget, rangetd));
					}
				}
			}
		}
		return sts;
	}

	public Set<Integer> getValues() {
		Set<Integer> s = new TreeSet<Integer>();
		for (int i = 0; i < states.size(); i++) {
			if (q[i][0] != 0 || q[i][1] != 0)
				s.add(i);
		}
		return s;
	}

	public void init() {
		// Initialize pole state.
		pos = 0.;
		posDot = 0.;
		angle = 0.;
		angleDot = 0.;
	}

	public double getReward() {
		return isTerminal() ? -1.0 : 0;
	}

	private boolean isTerminal() {
		return Math.abs(pos) > 2.4 || Math.abs(angle) > 12;
	}

	public synchronized void updateState(int action) {
		// Update the state of the pole;
		// First calc derivatives of state variables
		double force = forceMag * action;
		double sinangle = Math.sin(angle);
		double cosangle = Math.cos(angle);
		double angleDotSq = angleDot * angleDot;
		double common = (force + poleMassLength * angleDotSq * sinangle - fricCart
				* (posDot < 0 ? -1 : 0))
				/ totalMass;
		double angleDDot = (9.8 * sinangle - cosangle * common - fricPole * angleDot
				/ poleMassLength)
				/ (halfPole * (fourthirds - poleMass * cosangle * cosangle / totalMass));
		double posDDot = common - poleMassLength * angleDDot * cosangle / totalMass;

		// Now update state.
		pos += posDot * tau;
		posDot += posDDot * tau;
		angle += angleDot * tau;
		angleDot += angleDDot * tau;
	}

	private State getState() {
		for (State state : states) {
			PoleState s = (PoleState) state;
			if (s.in(pos, posDot, angle, angleDot))
				return s;
		}
		return null;
	}

	protected void updateQ(State state, Action action, double reward) {
		q[index(state)][actions.indexOf(action)] = computeQ(state, action, reward);
	}

	private int index(State state) {
		PoleState ps = (PoleState) state;
		return ps.getIndex();
	}

	public double computeQ(State state, Action action, double reward) {
		// get current q value
		double cq = q[index(state)][actions.indexOf(action)];
		// compute the right side of the equation
		double value = reward + (GAMA * getMax(state)) - cq;
		// compute new q value
		double newq = cq + ALPHA * value;

		return newq;
	}

	protected double getMax(State pState) {
		double max = 0;
		// search for the Q v for each state
		for (Action action : actions) {
			double value = q[index(pState)][actions.indexOf(action)];
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	private Action getNextAction(State pState) {
		if (pState == null)
			return null;

		double value0 = q[index(pState)][0];
		double value1 = q[index(pState)][1];

		if (value0 > value1)
			return left;
		else if (value1 > value0)
			return right;

		return CollectionsUtils.getRandom(actions);
	}

	protected void run(InvertedPendulum pProblem, Map<String, Object> pParameters) {
		// runPoleThread();
		episodes = 0;
		double totalSteps = 0;
		double maxxd = 0;
		double maxtd = 0;
		double minxd = 0;
		double mintd = 0;

		do {
			init();
			steps = 0;
			// get initial state
			State state = getState();
			Action action;
			// environment iteration loop
			do {
				// get action for state
				action = getNextAction(state);
				if (action != null) {
					// update pole vars
					updateState(action.equals(left) ? -1 : 1);
					// get reward for current state and action
					double reward = getReward();
					// update q value for state and action
					updateQ(state, action, reward);
					// go to next state
					state = getState();
					
					maxxd = Math.max(maxxd, posDot);
					maxtd = Math.max(maxtd, angleDot);
					minxd = Math.min(maxxd, posDot);
					mintd = Math.min(maxtd, angleDot);

					steps++;
					if (isTerminal())
						break;
				}else
					break;
				// Thread.sleep(50);
			} while (true);
			totalSteps += steps;
			// System.out.println(String.format(
			// "Pole Felt. Iter=%d. X=%.4f Xdot=%.4f angle=%.4f angleDot=%.4f",
			// steps, pos,
			// posDot, angle, angleDot));
			episodes++;
			
			if(episodes%100==0 && ALPHA > 0.1)
				ALPHA-=0.1;

		} while (episodes < MAX_EP);
		System.out.println("Finished. Steps mean: " + (totalSteps / episodes));
		System.out.println(String.format("Iterations=%d", episodes));
	}

	// private void runPoleThread() {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// while (episodes < MAX_EP) {
	// try {
	// Thread.sleep(50);
	// updateState(0);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// }).start();
	// }
}

class PoleState extends State {

	private static int count;
	private int index;
	private Range rangex;
	private Range rangexd;
	private Range ranget;
	private Range rangetd;

	public PoleState(Range rangex, Range rangexd, Range ranget, Range rangetd) {
		index = count++;
		this.rangex = rangex;
		this.rangexd = rangexd;
		this.ranget = ranget;
		this.rangetd = rangetd;
		setName(index + "");
	}

	public boolean in(double pos, double posDot, double angle, double angleDot) {
		return rangex.in(pos) && rangexd.in(posDot) && ranget.in(angle) && rangetd.in(angleDot);
	}

	@Override
	public String toString() {
		return getName();
	}

	public int getIndex() {
		return index;
	}
}

class Range {
	double min;
	double max;

	public Range(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public boolean in(double v) {
		return min <= v && v < max;
	}

	@Override
	public String toString() {
		return "[min=" + min + ", max=" + max + "]";
	}

}
