package com.tortoaster.customchess.chess.player;

import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Team;

import java.util.List;

public class HumanPlayer implements Player {
	
	private Team team;
	
	public HumanPlayer(Team team) {
		this.team = team;
	}
	
	public void nextMove(List<Piece> pieces) {
	}
}
