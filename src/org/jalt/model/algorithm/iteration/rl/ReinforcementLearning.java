package org.jalt.model.algorithm.iteration.rl;

import static org.jalt.util.DefaultTestProperties.ALPHA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jalt.model.action.Action;
import org.jalt.model.algorithm.PolicyGenerator;
import org.jalt.model.algorithm.actionchooser.RandomChooser;
import org.jalt.model.algorithm.actionchooser.ValuedObjectChooser;
import org.jalt.model.algorithm.iteration.IterationAlgorithm;
import org.jalt.model.algorithm.iteration.IterationValues;
import org.jalt.model.algorithm.iteration.PolicyEvaluation;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.jalt.model.algorithm.table.QTable;
import org.jalt.model.algorithm.table.QTableItem;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.model.ERG;
import org.jalt.model.model.MDP;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.util.CalcUtils;
import org.jalt.util.CollectionsUtils;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.PolicyUtils;

/**
 * 
 * @author andvicoso
 */
public abstract class ReinforcementLearning<M extends MDP> extends IterationAlgorithm<M, Policy>
		implements PolicyGenerator<M>, IterationValues {

	public static final String AGENT_NAME = "agent";
	/**
	 * The learning rate. The learning rate determines to what extent the newly
	 * acquired information will override the old information. A factor of 0
	 * will make the agent not learn anything, while a factor of 1 would make
	 * the agent consider only the most recent information.
	 */
	private final double alpha = ALPHA;
	private List<Integer> steps;
	protected QTable<? extends QTableItem> q;
	private QTable<? extends QTableItem> lastq;
	private ValuedObjectChooser<Action> actionChooser = new RandomChooser<>();
	private StoppingCriterium stoppingCriterium = DefaultTestProperties.DEFAULT_STOPON;
	private int maxSteps;
	private Map<String, Object> parameters;
	Map<Action, Double> actionValues;
	private Problem<M> problem;

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		init(pProblem, pParameters);
		return doRun(pProblem, pParameters);
	}

	protected void init(Problem<M> pProblem, Map<String, Object> pParameters) {
		episodes = 0;
		steps = new ArrayList<Integer>();
		problem = pProblem;
		model = pProblem.getModel();
		parameters = pParameters;

		initializeActionValues();
		initializeQTable(pParameters);
	}

	private void initializeActionValues() {
		actionValues = new HashMap<Action, Double>();
		double value = 1d / model.getActions().size();
		for (final Action action : model.getActions()) {
			actionValues.put(action, value);
		}
	}

	protected void initializeQTable(Map<String, Object> pParameters) {
		// try to find a table in the parameters
		if (pParameters != null)
			q = (QTable<?>) pParameters.get(QTable.NAME);
		// not found, create
		if (q == null) {
			q = createQTable();
		}
	}

	protected QTable<? extends QTableItem> createQTable() {
		return model instanceof ERG ? new ERGQTable(model.getStates(), model.getActions())
				: new QTable<QTableItem>(model.getStates(), model.getActions());
	}

	protected Policy doRun(Problem<M> pProblem, Map<String, Object> pParameters) {
		// start the main loop
		do {
			int currentSteps = 0;
			lastq = q.clone();
			Integer agent = getAgent(pParameters);
			// get initial state
			State state = pProblem.getInitialStates().get(agent);
			Action action;
			// environment iteration loop
			do {
				// get action for state
				action = getNextAction(state);
				if (action != null) {
					// get next state
					State nextState = getNextState(state, action);
					// if nextState eq null, stay in the same state and try a
					// different action
					if (nextState != null && !state.equals(nextState)) {
						// get reward for current state and action
						double reward = model.getRewardFunction().getValue(state, action);
						// update q value for state and action
						updateQ(state, action, reward, nextState);
						// go to next state
						state = nextState;
					}
					currentSteps++;
				}
				// while there is a valid state to go to
			} while (!isStopSteps(pProblem, action, state, currentSteps));
			// increment
			if (currentSteps > maxSteps)
				maxSteps = currentSteps;
			steps.add(currentSteps);
			episodes++;
			// Log.info("episodes: " + episodes + ". steps: " + steps);

			// Log.info("\nBest-Values: \n" + new GridPrinter().toGrid(model,
			// getLastValues()));
			// Log.info("\nCurr-Values: \n" + new GridPrinter().toGrid(model,
			// getCurrentValues()));

		} while (!stoppingCriterium.isStop(this));

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

	protected int getAgent(Map<String, Object> pParameters) {
		return pParameters.containsKey(AGENT_NAME) ? (Integer) pParameters.get(AGENT_NAME) : 0;
	}

	protected State getNextState(State state, Action action) {
		return model.getTransitionFunction().getNextState(model.getStates(), state, action);
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
			String msg = "ERROR: Agent possibly blocked. State: %s. Action: %s. Steps: %d. Episodes: %d";

			throw new RuntimeException(String.format(msg, state, action, currentSteps, episodes));
		}
		return action == null || state == null || pProblem.getFinalStates().contains(state);
	}

	public double getMeanSteps() {
		return CalcUtils.getMean(steps);
	}

	public double getStdDevSteps() {
		return CalcUtils.getStandardDeviation(getMeanSteps(), steps);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<State, Double> getLastValues() {
		return (Map<State, Double>) parameters.get(PolicyUtils.BEST_VALUES_STR);// lastq.getStateValue();//
	}

	@Override
	public Map<State, Double> getCurrentValues() {
		Map<String, Object> map = CollectionsUtils.asMap(PolicyUtils.POLICY_STR, q.getPolicy(false));
		return new PolicyEvaluation<M>().run(problem, map);
		// return q.getStateValue();
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

	public double getAlpha() {
		return alpha;
	}

	public QTable<? extends QTableItem> getLastQTable() {
		return lastq;
	}
}
