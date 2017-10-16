package org.jalt.model.converter;

import java.util.Set;

import org.jalt.model.function.PropositionFunction;
import org.jalt.model.model.ERG;
import org.jalt.model.model.Grid;
import org.jalt.model.model.MDP;
import org.jalt.model.model.impl.ERGGridModel;
import org.jalt.model.model.impl.ERGModel;
import org.jalt.model.propositional.Expression;
import org.jalt.model.propositional.Proposition;

/**
 *
 * @author andvicoso
 */
public class ToERG {

	public ERG convert(MDP pModel, Set<Proposition> pPropositions, PropositionFunction pPropositionFunction, Expression pGoal, Expression pPreservGoal) {
		ERG model;
		if (pModel instanceof Grid) {
			final Grid grid = (Grid) pModel;
			model = new ERGGridModel(grid.getRows(), grid.getCols());
		} else {
			model = new ERGModel();
		}

		model.setActions(pModel.getActions());
		model.setStates(pModel.getStates());
		model.setPropositions(pPropositions);
		model.setPropositionFunction(pPropositionFunction);
		model.setGoal(pGoal);
		model.setPreservationGoal(pPreservGoal);

		return model;
	}
}
