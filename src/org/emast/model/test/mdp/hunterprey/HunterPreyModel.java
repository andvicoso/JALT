package org.emast.model.test.mdp.hunterprey;

import org.emast.model.model.impl.GridModel;

/**
 * 
 * @author anderson
 */
public class HunterPreyModel extends GridModel {

	public HunterPreyModel(final int pRows, final int pCols, final int pAgents) {
		super(pRows, pCols);
		setAgents(pAgents);
	}
}
