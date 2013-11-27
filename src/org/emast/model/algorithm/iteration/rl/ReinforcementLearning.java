package org.emast.model.algorithm.iteration.rl;

import static org.emast.util.DefaultTestProperties.ALPHA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.emast.model.action.Action;
import org.emast.model.algorithm.PolicyGenerator;
import org.emast.model.algorithm.actionchooser.RandomActionChooser;
import org.emast.model.algorithm.actionchooser.ValuedObjectChooser;
import org.emast.model.algorithm.iteration.IterationAlgorithm;
import org.emast.model.algorithm.iteration.IterationValues;
import org.emast.model.algorithm.stoppingcriterium.StopOnError;
import org.emast.model.algorithm.stoppingcriterium.StoppingCriterium;
import org.emast.model.algorithm.table.QTable;
import org.emast.model.algorithm.table.QTableItem;
import org.emast.model.algorithm.table.erg.ERGQTable;
import org.emast.model.model.ERG;
import org.emast.model.model.MDP;
import org.emast.model.problem.Problem;
import org.emast.model.solution.Policy;
import org.emast.model.state.State;
import org.emast.util.CalcUtils;

/**
 * 
 * @author Anderson
 */
public abstract class ReinforcementLearning<M extends MDP> extends IterationAlgorithm<M, Policy>
		implements PolicyGenerator<M>, IterationValues {

	public static final String AGENT_NAME = "agent";
	/**
	 * The learning rate. The learning rate determines to what extent the newly acquired information
	 * will override the old information. A factor of 0 will make the agent not learn anything,
	 * while a factor of 1 would make the agent consider only the most recent information.
	 */
	private final double alpha = ALPHA;
	private List<Integer> steps;
	protected QTable<? extends QTableItem> q;
	private QTable<? extends QTableItem> lastq;
	private ValuedObjectChooser<Action> actionChooser = new RandomActionChooser();
	private StoppingCriterium stoppingCriterium = new StopOnError();

	@Override
	public Policy run(Problem<M> pProblem, Map<String, Object> pParameters) {
		init(pProblem, pParameters);
		return doRun(pProblem, pParameters);
	}

	protected void init(Problem<M> pProblem, Map<String, Object> pParameters) {
		episodes = 0;
		steps = new ArrayList<Integer>();
		model = pProblem.getModel();

		initializeQTable(pParameters);
	}

	protected void initializeQTable(Map<String, Object> pParameters) {
		if (q == null) {
			// try to find a table in the parameters
			q = (QTable<?>) pParameters.get(QTable.NAME);
			// not found, create
			if (q == null) {
				q = createQTable();
			}
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
					if (nextState != null) {
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
			} while (!isStopSteps(action, state, pProblem));
			// increment
			steps.add(currentSteps);
			episodes++;
			// Log.info("episodes: " + episodes + ". steps: " + steps);
		} while (!stoppingCriterium.isStop(this));

		return q.getPolicy(false);
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

		Collection<Action> actions = model.getTransitionFunction().getActionsFrom(
				model.getActions(), pState);
		// search for the Q v for each state
		for (Action action : actions) {
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
		// sb.append("\nAlpha: ").append(alpha);//TODO:descomentar em produção
		sb.append("\nSteps (mean): ").append(getMeanSteps());
		return sb.toString();
	}

	// Verify if agent is blocked (no action or state) or reached a final goal
	// state
	protected boolean isStopSteps(Action action, State state, Problem<M> pProblem) {
		return action == null || state == null || pProblem.getFinalStates().contains(state);
	}

	protected Map<Action, Double> getActionValues(State pState) {
		return model.getTransitionFunction().getActionValues(model.getActions(), pState);
	}

	protected abstract double computeQ(State state, Action action, double reward, State nextState);

	protected void updateQ(State state, Action action, double reward, State nextState) {
		double qValue = computeQ(state, action, reward, nextState);
		q.updateQ(model, qValue, state, action, reward, nextState);
	}

	public double getMeanSteps() {
		return CalcUtils.getMean(steps);
	}

	public double getStdDevSteps() {
		return CalcUtils.getStandardDeviation(getMeanSteps(), steps);
	}

	@Override
	public Map<State, Double> getLastValues() {
		return lastq.getStateValue();
	}

	@Override
	public Map<State, Double> getCurrentValues() {
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

	public double getAlpha() {
		return alpha;
	}

	public QTable<? extends QTableItem> getLastQTable() {
		return lastq;
	}
}
