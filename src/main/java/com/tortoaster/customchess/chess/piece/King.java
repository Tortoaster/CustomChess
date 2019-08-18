package com.tortoaster.customchess.chess.piece;

import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.player.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King extends Piece {

	public King(int x, int y, Team team, Board board) {
		super(x, y, team, Kind.KING, board);
	}

	/**
	 * Checks if the king do any Castling.
	 *
	 * @return the array that is altered if the king can Castle.
	 */
	@Override
	public Move[] getMoveDirections() {

		if(getMoves() == 0 && !getBoard().underAttack(getTeam(), getX(), getY())) {
			List<Piece> rooks = new ArrayList<>();

			for(Piece p : getBoard().getPieces()) {
				if(p.getTeam() == getTeam() && p.getKind() == Kind.ROOK) {
					rooks.add(p);
				}
			}

			if(!rooks.isEmpty()) {
				List<Move> moves = new ArrayList<>();
				Collections.addAll(moves, super.getMoveDirections());

				for(Piece r : rooks) {
					if(r.getMoves() == 0 && Math.abs(getX() - r.getX()) >= 3 && getY() == r.getY() && clear(Math.min(getX(), r.getX()), Math.max(getX(), r.getX()), getY())) {
						int x1 = Integer.signum(r.getX() - getX()) * getTeam().getCoefficient();
						int x2 = Integer.signum(r.getX() - getX()) * getTeam().getCoefficient() * 2;

						if(safe(x1, x2, getY(), r)) moves.add(new Move(x2, 0));
					}
				}

				return moves.toArray(new Move[0]);
			}
		}

		return super.getMoveDirections();
	}

    /**
     * Sets new x value
     * @param to the position this piece moves towards.
     */

	@Override
	public void onMove(Position to) {
		super.onMove(to);

		if(Math.abs(to.getX() - getX()) == 2) {
			int direction = Integer.signum(to.getX() - getX());

			for(int i = direction; i != (getBoard().getBoardWidth() - 2) * direction; i += direction) {
				final Piece rook = getBoard().getPiece(to.getX() + i, to.getY());

				if(rook != null) {

					final int previousX = rook.getX();
					final int previousY = rook.getY();

					getBoard().actionPerformed(new Action() {
						@Override
						public void revert() {
							rook.setX(previousX);
							rook.setY(previousY);
						}
					});

					rook.setX(to.getX() - direction);
					break;
				}
			}
		}
	}

	/**
	 * @return true if there are no pieces in the way for the king to castle
	 */
	private boolean clear(int x1, int x2, int y) {
		for(int i = x1 + 1; i < x2; i++)
			if(getBoard().getPiece(i, y) != null)
				return false;

		return true;
	}

	/**
	 * @return true if the king can castle without being checked
	 */
	private boolean safe(int x1, int x2, int y, Piece rook) {
		boolean unsafe;

		int c = rook.getX();
		int r = rook.getY();

		rook.setX(-1);
		rook.setY(-1);

		unsafe = getBoard().underAttack(getTeam(), x1, y);
		unsafe = getBoard().underAttack(getTeam(), x2, y) | unsafe;

		rook.setX(c);
		rook.setY(r);

		return !unsafe;
	}
}
