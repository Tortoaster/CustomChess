package com.tortoaster.customchess.chess;

import android.graphics.Color;

public enum Team {
	BLACK(1),
	WHITE(-1);
	
	private int coefficient;
	
	Team(int coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * These getters return the coefficient, the other team and this teams color.
	 */

	public int getCoefficient() {
		return coefficient;
	}
	
	public Team getOtherTeam() {
		if(this == BLACK) return WHITE;
		return BLACK;
	}
	
	public int getColor() {
		if(this == BLACK) return Color.BLACK;
		return Color.WHITE;
	}
}