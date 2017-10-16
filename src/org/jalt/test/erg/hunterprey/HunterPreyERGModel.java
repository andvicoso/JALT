package org.jalt.test.erg.hunterprey;

import java.util.Set;

import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;
import org.jalt.util.CollectionsUtils;

/**
 *
 * @author andvicoso
 */
public class HunterPreyERGModel extends ERGGridModel {

	public HunterPreyERGModel(final int pRows, final int pCols) {
		super(pRows, pCols);
		// set props
		String[] propss = { "hole", "wall", "prey" };
		Set<Proposition> props = CollectionsUtils.createSet(Proposition.class, propss);
		setPropositions(props);

		setGoal(new Expression("prey"));
		setPreservationGoal(new Expression("!hole & !wall"));
	}
}
