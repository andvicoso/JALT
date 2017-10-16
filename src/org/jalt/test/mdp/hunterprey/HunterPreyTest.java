package org.jalt.test.mdp.hunterprey;

import java.io.IOException;
import java.util.Collections;

import org.jalt.model.algorithm.rl.td.QLearning;
import org.jalt.model.algorithm.stoppingcriterium.StopOnMaxDiffError;
import org.jalt.model.problem.Problem;
import org.jalt.model.solution.Policy;

/**
 * 
 * @author andvicoso
 */
public class HunterPreyTest {

	public static void main(final String[] pArgs) throws IOException {
		Problem<HunterPreyModel> prob = new HunterPreyProblemFactory(10, 10, 2, 2).create();
		QLearning<HunterPreyModel> rl = new QLearning<>();
		rl.setStoppingCriterium(new StopOnMaxDiffError());

		Policy p = rl.run(prob, Collections.EMPTY_MAP);

		System.out.println(prob.toString(p));
		System.out.println(p.toString());
	}
}
