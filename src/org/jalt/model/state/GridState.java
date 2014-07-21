package org.jalt.model.state;

import org.jalt.util.grid.GridUtils;
import org.jalt.util.grid.distancemeasure.CityBlock;
import org.jalt.util.grid.distancemeasure.DistanceMeasure;

/**
 * 
 * @author andvicoso
 */
public class GridState extends State {
	private final int row;
	private final int col;

	public GridState(int row, int col) {
		super(GridUtils.getGridStateName(row, col));
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GridState) {
			GridState gs = (GridState) obj;
			return row == gs.row && col == gs.col;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + this.row;
		hash = 97 * hash + this.col;
		return hash;
	}

	public boolean isNeighbour(GridState g) {
		int difr = row - g.row;
		int difc = col - g.col;
		return (Math.abs(difr) + Math.abs(difc)) == 1;
	}
}
