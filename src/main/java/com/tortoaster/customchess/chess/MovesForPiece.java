package com.tortoaster.customchess.chess;

import com.tortoaster.customchess.chess.piece.Piece;

import java.util.List;

public class MovesForPiece {
	
	private Piece piece;
	private List<Position> moves;
	
	public MovesForPiece(Piece piece, List<Position> moves) {
		this.piece = piece;
		this.moves = moves;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public List<Position> getMoves() {
		return moves;
	}
}
