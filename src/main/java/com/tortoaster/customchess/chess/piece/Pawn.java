package com.tortoaster.customchess.chess.piece;

import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.player.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {
	
	private static Move[] START_MOVES = new Move[]{new Move(0, 1), new Move(0, 2)};
	
	/**
	 * True if and only if the piece just made its starting move, meaning it could have been
	 * captured on its way there (en Passant).
	 */
	private boolean enPassantable;
	
	private Position startingPosition;
	
	public Pawn(int x, int y, Team team, Board board) {
		super(x, y, team, Kind.PAWN, board);
		startingPosition = new Position(x, y);
	}
	
	@Override
	public Move[] getMoveDirections() {
		if(getKind() == Kind.PAWN) {
			if(getMoves() == 0) return START_MOVES;
			
			List<Move> moves = new ArrayList<>();
			Collections.addAll(moves, super.getMoveDirections());
			
			for(Piece p : getBoard().getPieces()) {
				if(p.getY() == getY() && Math.abs(p.getX() - getX()) == 1 && p instanceof Pawn && ((Pawn) p).enPassantable) {
					moves.add(new Move((p.getX() - getX()) * getTeam().getCoefficient(), 1));
				}
			}
			
			return moves.toArray(new Move[0]);
		}
		
		return super.getMoveDirections();
	}
	
	@Override
	public void onMove(Position to) {
		super.onMove(to);
		
		if(getKind() == Kind.PAWN) {
			enPassantable = getMoves() == 1 && Math.abs(to.getY() - startingPosition.getY()) == 2;
			
			if(to.getX() - getX() != 0 && getBoard().getPiece(to.getX(), to.getY()) == null) {
				final Piece p = getBoard().getPiece(to.getX(), to.getY() - getTeam().getCoefficient());
				
				if(p != null) {
					getBoard().actionPerformed(new Action() {
						@Override
						public void revert() {
							getBoard().getPieces().add(p);
						}
					});
					
					getBoard().getPieces().remove(p);
				}
			}
			
			if(to.getY() == 0 || to.getY() == getBoard().getBoardHeight()) {
				setKind(Kind.QUEEN);
				
				getBoard().actionPerformed(new Action() {
					@Override
					public void revert() {
						setKind(Kind.PAWN);
					}
				});
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		enPassantable = false;
	}
}
