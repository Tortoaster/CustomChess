package com.tortoaster.customchess.chess;

import java.util.ArrayList;
import java.util.List;

public class Move {
	
	private boolean repeating, jumping;
	
	private int dX, dY;
	
	public Move(int dX, int dY) {
		this(dX, dY, false, false);
	}
	
	public Move(int dX, int dY, boolean repeating) {
		this(dX, dY, false, repeating);
	}
	
	public Move(int dX, int dY, boolean jumping, boolean repeating) {
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
	
	public static List<Move> translateData(String data) {
		List<Move> moves = new ArrayList<>();
		
		if(!data.isEmpty()) {
			String[] lines = data.split(", ");
			
			for(String m : lines) {
				String[] numbers = m.split(" ");
				
				moves.add(new Move(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]) != 0, Integer.parseInt(numbers[3]) != 0));
			}
		}
		
		return moves;
	}
}
