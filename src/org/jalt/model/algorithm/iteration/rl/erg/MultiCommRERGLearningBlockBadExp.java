package org.jalt.model.algorithm.iteration.rl.erg;

import static org.jalt.util.DefaultTestProperties.BAD_EXP_VALUE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jalt.infra.log.Log;
import org.jalt.model.algorithm.Algorithm;
import org.jalt.model.algorithm.iteration.rl.ReinforcementLearning;
import org.jalt.model.algorithm.reachability.PPFERG;
import org.jalt.model.algorithm.stoppingcriterium.StopOnAvoidableExpression;
import org.jalt.model.algorithm.stoppingcriterium.StoppingCriteria;
import org.jalt.model.algorithm.table.QTable;
import org.jalt.model.algorithm.table.erg.ERGQTable;
import org.jalt.model.chooser.BadExpressionChooser;
import org.jalt.model.chooser.Chooser;
import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.transition.BlockedGridTransitionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.impl.GridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.solution.Policy;
import org.jalt.model.state.State;
import org.jalt.model.test.MainTest;
import org.jalt.util.CalcUtils;
import org.jalt.util.DefaultTestProperties;
import org.jalt.util.erg.ERGLearningUtils;

/**
 * 
 * @author andvicoso
 */

public abstract class MultiCommRERGLearningBlockBadExp implements Algorithm<ERG, Policy>,
		MultiERGLearning {
	protected final Set<Expression> avoid = new HashSet<Expression>();
	protected final Set<State> blocked = new HashSet<>();
	protected final Chooser<Expression> expFinder = new BadExpressionChooser(BAD_EXP_VALUE, avoid);

	protected void initilize(ERG model) {
		avoid.clear();
		blocked.clear();
		// POG
		if (model instanceof GridModel) {
			GridModel gridModel = (GridModel) model;
			int rows = gridModel.getRows();
			int cols = gridModel.getCols();
			model.setTransitionFunction(new BlockedGridTransitionFunction(rows, cols, blocked));
		}// else//TODO:
	}

	@Override
	public Policy run(Problem<ERG> prob, Map<String, Object> params) {
		int iteration = 0;
		ERG model = prob.getModel();
		ERGQTable q;
		Expression badExp;
		Policy policy;
		// initialize
		initilize(model);
		boolean valid = false;
		// start main loop
		do {
			iteration++;
			// Log.info("\nITERATION " + iteration + ":");
			// 1. RUN QLEARNING UNTIL A BAD REWARD EXPRESSION IS FOUND

			List<Policy> policies = new ArrayList<Policy>(learnings.size());
			int i = 0;
			for (ReinforcementLearning<ERG> learning : learnings) {
				// create and save q table for each agent
				params.put(QTable.NAME + "_" + i,
						new ERGQTable(model.getStates(), model.getActions()));

				runThread(prob, params, learning, policies, i++);
			}

			int it = 0;
			while (policies.size() < learnings.size()) {
				if (it++ > MAX_IT)
					throw new RuntimeException("Tired of waiting agent/thread " + it
							+ " to finish.");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			q = (ERGQTable) params.get(QTable.NAME + "_" + 0);//FIXME
			// 2. GET BAD EXPRESSION FROM QLEARNING ITERATIONS
			badExp = expFinder.chooseOne(q.getExpsValues());
			// valid expression: not null and not empty
			valid = isValid(badExp);
			// 3. CHANGE THE Q VALUE FOR STATES THAT WERE VISITED IN
			// QLEARNING EXPLORATION AND HAVE THE FOUND EXPRESSION
			if (valid) {
				Log.info("Found bad expression: " + badExp);
				// avoid bad exp
				avoid.add(badExp);
				// Log.info("Avoid: " + avoid);
				populateBlocked(model, badExp);
				// run vi again->new environment to compare
				MainTest.runVI(prob, params);
			}
		} while (valid);

		if (!avoid.isEmpty()) {
			policy = extractPolicyPPFERG(params, prob, model, q);
		} else {
			policy = q.getPolicy();
		}

		// Log.info("Preservation goal:" + model.getPreservationGoal());
		// Log.info("\nQTable: \n" + q.toString(model));

		return policy;
	}

	protected void populateBlocked(ERG model, Expression toBlock) {
		// mark as blocked all states that contains one of the "avoid"
		// expressions
		PropositionFunction pf = model.getPropositionFunction();
		for (State state : model.getStates()) {
			Expression exp = pf.getExpressionForState(state);
			// toBlock.evaluate(pf.getPropositionsForState(state))
			if (toBlock.equals(exp)) {
				blocked.add(state);
			}
		}
	}

	protected boolean isValid(Expression exp) {
		return exp != null && !exp.isEmpty();
	}

	public String sprintResults() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nBad exp reward param: ").append(BAD_EXP_VALUE);

		return sb.toString();
	}

	protected StoppingCriteria getStopCriteria() {
		return new StoppingCriteria(new StopOnAvoidableExpression(BAD_EXP_VALUE, avoid),
				DefaultTestProperties.DEFAULT_STOPON);
	}

	protected Policy extractPolicyPPFERG(Map<String, Object> pParameters, Problem<ERG> pProb,
			ERG model, ERGQTable q) {
		Policy policy;
		// 4. CREATE NEW MODEL AND PROBLEM FROM AGENT EXPLORATION
		model = ERGLearningUtils.createModel(model, q, avoid);
		// create problem
		Problem<ERG> prob = new Problem<>(model, pProb.getInitialStates(), pProb.getFinalStates());
		// q learning policy
		// Log.info(prob.toString(policy));
		// q learning policy - best (greater q values) actions
		// Log.info(prob.toString(policy.optimize()));
		// 5. CREATE PPFERG ALGORITHM
		final PPFERG<ERG> ppferg = new PPFERG<ERG>();
		// 6. GET THE VIABLE POLICIES FROM PPFERG EXECUTED OVER THE NEW
		// MODEL
		policy = ppferg.run(prob, pParameters);
		// after ppferg
		// Log.info(prob.toString(policy));
		// 7. GET THE FINAL POLICY FROM THE PPFERG VIABLE POLICIES
		//policy = new Policy(ERGLearningUtils.optmize(policy, q));
		// after optimize
		// Log.info(prob.toString(policy));
		return policy;
	}

	private static final int MAX_IT = 600;
	private List<ReinforcementLearning<ERG>> learnings;

	public MultiCommRERGLearningBlockBadExp(List<ReinforcementLearning<ERG>> learnings) {
		this.learnings = learnings;
		// replace the default stopping criterium for all learning algorithms
		for (ReinforcementLearning<ERG> learning : learnings) {
			learning.setStoppingCriterium(getStopCriteria());
		}
	}

	@Override
	public String getName() {
		return getClass().getSimpleName() + " (" + learnings.get(0).getName() + ")";
	}

	protected void runThread(final Problem<ERG> pProblem, final Map<String, Object> pParameters,
			final ReinforcementLearning<ERG> learning, final List<Policy> policies, final int agent) {

		new Thread() {
			@Override
			public void run() {
				// Log.info("Started thread " + i);
				Map<String, Object> map = new HashMap<String, Object>(pParameters);
				map.put(ReinforcementLearning.AGENT_NAME, agent);

				Policy p = learning.run(pProblem, map);
				synchronized (policies) {
					policies.add(p);
				}
				// Log.info("Finished thread " + i);
			}
		}.start();
	}

	@Override
	public List<ReinforcementLearning<ERG>> getLearnings() {
		return learnings;
	}

	@Override
	public String printResults() {
		StringBuilder sb = new StringBuilder(sprintResults());

		sb.append("\nLearning algorithm: ").append(learnings.get(0).getClass().getSimpleName());
		for (ReinforcementLearning<ERG> learning : learnings) {
			sb.append(learning.printResults());
		}

		sb.append(printMeanResults());

		return sb.toString();
	}

	private String printMeanResults() {
		double steps = 0;
		double episodes = 0;
		Collection<Integer> epis = new ArrayList<Integer>();

		for (ReinforcementLearning<ERG> learning : learnings) {
			steps += learning.getMeanSteps();
			episodes += learning.getIterations();
			epis.add(learning.getIterations());
		}

		steps = steps / learnings.size();
		episodes = episodes / learnings.size();
		double episodes_std_dev = CalcUtils.getStandardDeviation(episodes, epis);

		StringBuilder sb = new StringBuilder();
		sb.append("\nMulti Means: ");
		sb.append("\nEpisodes: ").append(episodes);
		sb.append("\nEpisodes (std dev): ").append(episodes_std_dev);
		sb.append("\nSteps (mean): ").append(steps);

		return sb.toString();
	}

}
