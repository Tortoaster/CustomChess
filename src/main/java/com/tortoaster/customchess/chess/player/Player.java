package com.tortoaster.customchess.chess.player;

import com.tortoaster.customchess.chess.piece.Piece;

import java.util.List;

public interface Player {
	
	void nextMove(List<Piece> pieces);
}
