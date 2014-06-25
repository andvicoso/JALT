package org.jalt.util.grid;

import static org.jalt.util.DefaultTestProperties.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.function.reward.RewardFunctionProposition;
import org.jalt.model.model.ERG;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.problem.Problem;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.model.state.State;
import org.jalt.util.CollectionsUtils;

/**
 * 
 * @author andvicoso
 */
public class ERGGridModelReader {

	public Problem<ERG> read(String filename) throws IOException {
		PropositionFunction pf = new PropositionFunction();
		Set<Proposition> props = new HashSet<Proposition>();
		Set<Proposition> badProps = new HashSet<Proposition>();

		Set<State> finalStates = new HashSet<State>();
		Map<Integer, State> initialStates = new HashMap<Integer, State>();

		Scanner s = new Scanner(new BufferedReader(new FileReader(filename)));
		String[] line = s.nextLine().split("\\|");
		String size = line[line.length - 1];

		int cols = Integer.parseInt(size.trim()) + 1;
		int rows = 0;

		while (s.hasNext()) {
			line = s.nextLine().split("\\|");

			for (int i = 2; i < line.length; i++) {
				String data = line[i];
				State st = GridUtils.STATES_CACHE[rows][i - 2];

				if (!data.trim().isEmpty()) {
					if (data.contains(FINAL_GOAL)) {
						finalStates.add(st);
					}

					String[] dts = data.split(" ");
					Set<Proposition> propsState = new HashSet<Proposition>();

					for (int j = 0; j < dts.length; j++) {
						String dt = dts[j];
						if (!dt.trim().isEmpty()) {
							try {
								int agent = Integer.parseInt(dt);
								initialStates.put(agent, st);
							} catch (NumberFormatException e) {
								char first = dt.charAt(0);
								Proposition p = new Proposition(dt);
								propsState.add(p);

								if (Character.isLetter(first) && Character.isUpperCase(first)) {
									badProps.add(p);
								}

								//if (!p.getName().equals(FINAL_GOAL)) {
									props.add(p);
								//}
							}
						}
					}

					if (!propsState.isEmpty()) {
						pf.add(st, propsState);
					}
				}
			}
			rows++;
		}

		s.close();

		Expression finalGoal = new Expression(FINAL_GOAL);
		//TODO: change to Map<Expression, Double> (andvicoso)
		Map<Proposition, Double> rewardMap = CollectionsUtils.createMap(badProps, BAD_REWARD);
		//add good reward to goal propositions
		for (Proposition goodProp : finalGoal.getPropositions()) {
			rewardMap.put(goodProp, GOOD_REWARD);
		}
		//create model
		ERGGridModel model = new ERGGridModel(rows, cols);
		model.setAgents(initialStates.size());
		model.setPropositionFunction(pf);
		model.setPropositions(props);
		model.setRewardFunction(new RewardFunctionProposition<ERGGridModel>(model, rewardMap,
				OTHERWISE));
		model.setGoal(finalGoal);
		//create problem
		return new Problem<ERG>(model, initialStates, finalStates);
	}
}
