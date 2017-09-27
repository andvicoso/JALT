package org.jalt.model.test.erg.city;

import java.util.Set;

import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.util.CollectionsUtils;

/**
 *
 * @author andvicoso
 */
public class CityModel extends ERGGridModel {

	public CityModel(final int pRows, final int pCols, final int pAgents) {
		super(pRows, pCols);
		setAgents(pAgents);
		// create propositions
		String[] propss = { "hole", "wall", "semaphore", "exit", "bridge" };
		Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propss);
		setPropositions(props);
		// set expressions
		setGoal(new Expression("exit"));
		setPreservationGoal(new Expression("!semaphore & !bridge & !hole & !wall"));
	}
}
