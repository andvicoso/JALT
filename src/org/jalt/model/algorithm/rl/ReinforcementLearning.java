package org.jalt.model.algorithm.rl;

import static org.jalt.util.DefaultTestProperties.ALPHA;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jalt.infra.log.Log;
import org.jalt.model.action.Action;
import org.jalt.model.action.ActionPerformer;
import org.jalt.model.algorithm.PolicyGenerator;
import org.jalt.model.algorithm.actionchooser.EpsilonGreedy;
import org.jalt.model.algorithm.actionchooser.ValuedObjectChooser;
import org.jalt.model.algorithm.rl.dp.IterationAlgorithm;
import org.jalt.model.algorithm.rl.dp.IterationValues;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.jalt.model.algorithm.table.QTable;
import org.jalt.model.algorithm.table.QTableItem;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.model.ERG;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.model.state.StateDetector;
import org.jalt.util.CalcUtils;

/**
 * 
 * @author andvicoso
 */
public abstract class ReinforcementLearning<M extends MDP> extends IterationAlgorithm<M, Policy> implements PolicyGenerator<M>, IterationValues {

	public static final String AGENT_NAME = "agent";
	/**
	 * The learning rate. The learning rate determines to what extent the newly acquired information will override the
	 * old information. A factor of 0 will make the agent not learn anything, while a factor of 1 would make the agent
	 * consider only the most recent information.
	 */
	private final double alpha = ALPHA;
	private List<Integer> steps;
	private int maxSteps;

	protected QTable<? extends QTableItem> q;
	private QTable<? extends QTableItem> lastq;
	private ValuedObjectChooser<Action> actionChooser = new EpsilonGreedy<>();// new RandomChooser<>();
	private Map<Action, Double> actionValues;
	private List<Double> culmulativeRewards = new LinkedList<>();
	protected StateDetector<State> stateDetector;
	protected ActionPerformer<Action> actionPerformer;

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		init(pProblem);
		return doRun(pProblem);
	}

	protected void init(Problem<M> pProblem) {
		episodes = 0;
		steps = new LinkedList<Integer>();
		model = pProblem.getModel();

		initializeActionValues();
		if (q == null) {
			q = createQTable();
		}
	}

	private void initializeActionValues() {
		actionValues = new HashMap<Action, Double>();
		double value = 1d / model.getActions().size();
		for (final Action action : model.getActions()) {
			actionValues.put(action, value);
		}
	}

	protected QTable<? extends QTableItem> createQTable() {
		return model instanceof ERG ? new ERGQTable(model.getStates(), model.getActions()) : new QTable<QTableItem>(model.getStates(), model.getActions());
	}

	protected Policy doRun(Problem<M> pProblem) {
		// start the main loop
		do {
			int currentSteps = 0;
			lastq = q.clone();
			// get initial state
			State state = stateDetector.getCurrentState();
			Action action;
			double episodeReward = 0;
			// environment iteration loop
			do {
				// get action for state
				action = getNextAction(state);
				if (action != null) {
					actionPerformer.execute(action);
					// get next state
					State nextState = stateDetector.getCurrentState();
					// if nextState eq null, stay in the same state and try a
					// different action
					if (nextState != null) {
						// get reward for current state and action
						double reward = model.getRewardFunction().getValue(state, action);
						// update q value for state and action
						updateQ(state, action, reward, nextState);
						// go to next state
						state = nextState;
						episodeReward += reward;
					}
					currentSteps++;
				}
				// while there is a valid state to go to
			} while (!isStopSteps(pProblem, action, state, currentSteps));
			// increment
			if (currentSteps > maxSteps)
				maxSteps = currentSteps;
			culmulativeRewards.add(episodeReward);
			steps.add(currentSteps);
			episodes++;
			// Log.info("Episode: " + episodes + ". steps: " + steps);

			// Log.info("\nBest-Values: \n" + new GridPrinter().toGrid(model,
			// getLastValues()));
			// Log.info("\nCurr-Values: \n" + new GridPrinter().toGrid(model,
			// getCurrentValues()));

		} while (!stoppingCriterium.isStop(this));
		Log.info("Episodes: " + episodes);

		// Log.info("\nQTable: \n" + q.toString(model));

		// Log.info("\nV-Values: \n" + new GridPrinter().toGrid(model,
		// getLastValues()));

		return q.getPolicy(false);
	}

	protected abstract double computeQ(State state, Action action, double reward, State nextState);

	protected void updateQ(State state, Action action, double reward, State nextState) {
		double qValue = computeQ(state, action, reward, nextState);
		q.updateQ(model, qValue, state, action, reward, nextState);
	}

	protected Map<Action, Double> getActionValues(State pState) {
		return actionValues;
	}

	protected Action getNextAction(State state) {
		return actionChooser.choose(getActionValues(state), state);
	}

	protected double getMax(State pState) {
		Double max = null;
		// search for the Q v for each state
		for (Action action : model.getActions()) {
			Double value = q.getValue(pState, action);
			if (max == null || value > max) {
				max = value;
			}
		}

		if (max == null) {
			max = 0d;
		}

		return max;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder(super.printResults());
		// sb.append("\nAlpha: ").append(alpha);//TODO:descomentar em producao
		sb.append("\nSteps (max): ").append(maxSteps);
		sb.append("\nSteps (mean): ").append(getMeanSteps());
		return sb.toString();
	}

	// Verify if agent is blocked (no action or state) or reached a final goal
	private boolean isStopSteps(Problem<M> pProblem, Action action, State state, int currentSteps) {
		int states = pProblem.getModel().getStates().size();
		if (currentSteps > (states * states * 10)) {
			String msg = "ERROR: Agent possibly blocked. State: %s. Action: %s. Steps: %d";

			throw new RuntimeException(String.format(msg, state, action, currentSteps));
		}
		return action == null || state == null || pProblem.getFinalStates().contains(state);
	}

	public double getMeanSteps() {
		return CalcUtils.getMean(steps);
	}

	public double getStdDevSteps() {
		return CalcUtils.getStandardDeviation(getMeanSteps(), steps);
	}

	@Override
	public Map<State, Double> getLastValues() {
		// return (Map<State, Double>) parameters.get(PolicyUtils.BEST_VALUES_STR);
		return lastq.getStateValue();
	}

	@Override
	public Map<State, Double> getCurrentValues() {
		// Map<String, Object> map = CollectionsUtils.asMap(PolicyUtils.POLICY_STR, q.getPolicy(false).getBestPolicy());
		// return new PolicyEvaluation<M>().run(problem, map);
		return q.getStateValue();
	}

	public void setStoppingCriterium(StoppingCriterium stoppingCriterium) {
		this.stoppingCriterium = stoppingCriterium;
	}

	public StoppingCriterium getStoppingCriterium() {
		return stoppingCriterium;
	}

	public void setActionChooser(ValuedObjectChooser<Action> actionChooser) {
		this.actionChooser = actionChooser;
	}

	public ValuedObjectChooser<Action> getActionChooser() {
		return actionChooser;
	}

	public QTable<? extends QTableItem> getQTable() {
		return q;
	}

	public void setQTable(QTable<? extends QTableItem> q) {
		this.q = q;
	}

	public double getAlpha() {
		return alpha;
	}

	public QTable<? extends QTableItem> getLastQTable() {
		return lastq;
	}

}
