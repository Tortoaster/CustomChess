package com.tortoaster.customchess.chess;

public class Move {
	
	private boolean repeating, jumping;
	
	private int dX, dY;
	
	public Move(int dX, int dY) {
		this(dX, dY, false, false);
	}
	
	public Move(int dX, int dY, boolean repeating) {
		this(dX, dY, repeating, false);
	}
	
	public Move(int dX, int dY, boolean repeating, boolean jumping) {
		this.dX = dX;
		this.dY = dY;
		this.repeating = repeating;
		this.jumping = jumping;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Move) {
			Move m = (Move) o;
			
			return getDeltaX() == m.getDeltaX() && getDeltaY() == m.getDeltaY();
		}
		
		return false;
	}
	
	public int getDeltaX() {
		return dX;
	}
	
	public int getDeltaY() {
		return dY;
	}
	
	public boolean isRepeating() {
		return repeating;
	}
	
	public boolean isJumping() {
		return jumping;
	}
}
