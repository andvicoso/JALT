package org.jalt.model.test.erg.antenna;

import java.util.Set;

import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.util.CollectionsUtils;

/**
 * Problem that represents a grid with propositions. The agent must find the
 * path to exit up, avoid the obstacles and keep itself inside an antenna
 * coverage signal.
 * 
 * Grid Caption: 0-9: Initial agent x position h: hole s: stone w: water a:
 * antenna c: antenna coverage u: exit up d: exit down
 * 
 * @author andvicoso
 */
public class AntennaCoverageModel extends ERGGridModel {

	public AntennaCoverageModel(final int pRows, final int pCols, final int pAgents) {
		super(pRows, pCols);
		setAgents(pAgents);
		// set props
		String[] propsStr = { "hole", "stone", "water", "exit", "up", "down", "antenna", "coverage" };
		Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propsStr);
		setPropositions(props);
		// set goals
		setGoal(new Expression("up & exit"));
		setPreservationGoal(new Expression("!hole & !stone & coverage"));
	}
}
