package com.tortoaster.customchess.chess.player;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reverters keep track of actions performed, so that they can easily be undone.
 */
public class Reverter {
	
	private Deque<Action> stack = new ArrayDeque<>();
	
	/**
	 * Adds an action on top of the stack
	 */
	public void add(Action action) {
		stack.addLast(action);
	}
	
	/**
	 * Reverts all actions on the stack, starting at the top.
	 */
	public void revert() {
		while(!stack.isEmpty()) {
			Action action = stack.removeLast();
			action.revert();
		}
	}
}
