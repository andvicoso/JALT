package org.jalt.model.test.mdp.hunterprey;

import org.jalt.model.model.impl.GridModel;

/**
 * 
 * @author andvicoso
 */
public class HunterPreyModel extends GridModel {

	public HunterPreyModel(final int pRows, final int pCols, final int pAgents) {
		super(pRows, pCols);
		setAgents(pAgents);
	}
}
