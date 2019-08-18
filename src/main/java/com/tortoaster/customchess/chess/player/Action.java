package com.tortoaster.customchess.chess.player;

/**
 * Actions are implemented as pieces of code of the inverse, e.g. when a piece moves up, revert
 * would be implemented as a piece moving down again, so that all actions can be undone easily.
 */
public interface Action {
	
	void revert();
}
