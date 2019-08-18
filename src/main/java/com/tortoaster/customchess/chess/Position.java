package com.tortoaster.customchess.chess;

public class Position {
	private int x;
	private int y;

	/**
	 * This is the position of a piece.
	 *
	 * @param x 	The x coordinate of the piece.
	 * @param y 	The y coordinate of the piece.
	 */

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return if the object is the same as this position.
	 */

	public boolean equals(Object o) {
		if(o instanceof Position) {
			Position p = (Position) o;
			return p.getX() == x && p.getY() == y;
		}
		
		return false;
	}

	/**
	 *	These getters return the x position and the y position.
	 */

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
