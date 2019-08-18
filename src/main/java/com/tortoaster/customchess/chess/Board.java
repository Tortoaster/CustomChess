package com.tortoaster.customchess.chess;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.tortoaster.customchess.activity.GameActivity;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.player.Action;
import com.tortoaster.customchess.chess.player.ComputerPlayer;
import com.tortoaster.customchess.chess.player.Player;
import com.tortoaster.customchess.chess.player.Reverter;
import com.tortoaster.customchess.view.Chess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Board {
	
	private int boardWidth, boardHeight;
	private int horizontalMargin, verticalMargin;
	private int blockSize;
	
	private Piece selectedPiece;
	
	private Team turn = Team.WHITE;
	
	private List<Piece> lostWhitePieces = new ArrayList<>(), lostBlackPieces = new ArrayList<>();
	
	private Player currentPlayer;

	private List<Position> validMoves = new ArrayList<>();
	private List<Position> checkPosition = new ArrayList<>();
	private List<MovesForPiece> allMovesTurn = new ArrayList<>();
	private List<MovesForPiece> allAttacksEnemy = new ArrayList<>();
	private List<Position> previousMove = new ArrayList<>();
	
	private Deque<Reverter> undos = new ArrayDeque<>();
	
	/**
     *  booleans that keep track of whether a player is checked and whether they have any moves left
     */
	private boolean check, paralyzed;
	
	private List<Piece> pieces;
	
	private Chess chess;
	private int touchX, touchY;
	
	public Board(int boardWidth, int boardHeight, List<Piece> pieces, Chess chess) {
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.chess = chess;
		this.pieces = pieces;

		calculateAllMovesOfTurn();

		updatePlayer();
	}
	
	/**
	 * calculates the new blockSize and the new margins in case of a change in screen size.
	 *
	 * @param w the new width
	 * @param h the new height
	 */
	public void setScreenSize(int w, int h) {
		blockSize = Math.min(w / boardWidth, h / boardHeight);
		horizontalMargin = (w - boardWidth * blockSize) / 2;
		verticalMargin = (h - boardHeight * blockSize) / 2;
		
		for(Piece p : pieces) {
			int temp = p.getY() * blockSize + verticalMargin;
			if(!p.getName().equals("wall")) temp -= blockSize / 3;
			p.setPosition(p.getX() * blockSize + horizontalMargin, temp, blockSize);
		}
	}
	
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		
		for(int y = 0; y < boardHeight; y++) {
			for(int x = 0; x < boardWidth; x++) {
				Rect rect = new Rect(x * blockSize + horizontalMargin, y * blockSize + verticalMargin, (x + 1) * blockSize + horizontalMargin, (y + 1) * blockSize + verticalMargin);
				Position pos = new Position(x, y);
				
				if((x + y) % 2 == 0)
					paint.setColor(chess.getLightColor());
				else
					paint.setColor(chess.getDarkColor());
				
				canvas.drawRect(rect, paint);
				
				if(checkPosition.contains(pos)) {
					paint.setColor(chess.getEndangeredColor());
					canvas.drawRect(rect, paint);
				}
				if(previousMove.contains(pos)) {
					paint.setColor(chess.getMarkedColor());
					canvas.drawRect(rect, paint);
				}
				
				if(selectedPiece != null) {
					if(selectedPiece.getX() == x && selectedPiece.getY() == y) {
						paint.setColor(chess.getSelectedColor());
						canvas.drawRect(rect, paint);
					} else if(validMoves.contains(pos)) {
						if(getPiece(x, y) == null) {
							paint.setColor(chess.getHighlightedColor());
						} else {
							paint.setColor(chess.getEndangeredColor());
						}
						
						canvas.drawRect(rect, paint);
					}
				}
			}
		}
		
		for(int i = 0; i < pieces.size(); i++) {
			Piece p = pieces.get(i);
			if(p != null) p.draw(canvas);
		}
		
		for(int i = 0; i < lostWhitePieces.size(); i++) {
			Piece p = lostWhitePieces.get(i);
			if(p != null) p.draw(canvas);
		}
		
		for(int i = 0; i < lostBlackPieces.size(); i++) {
			Piece p = lostBlackPieces.get(i);
			if(p != null) p.draw(canvas);
		}
	}
	
	/**
	 * handles the given x and y of the touchEvent and makes sure the right things are done
	 *
	 * @param e the motion event detected
	 */
	public void touchEvent(MotionEvent e) {
		if(currentPlayer instanceof ComputerPlayer) return;
		
		if(e.getAction() == MotionEvent.ACTION_DOWN) {
			touchX = (int) e.getX();
			touchY = (int) e.getY();
		} else if(e.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) e.getX();
			int y = (int) e.getY();
			
			if(Math.abs(touchX - x) >= blockSize || Math.abs(touchY - y) >= blockSize) {
				if(selectedPiece == null) drag(touchX, touchY, x, y);
			} else {
				tap(touchX, touchY);
			}
		}
	}
	
	private void drag(int x1, int y1, int x2, int y2) {
		tap(x1, y1);
		tap(x2, y2);
	}

	private void tap(int x, int y) {
		
		x = (x - horizontalMargin) / blockSize;
		y = (y - verticalMargin) / blockSize;
		
		Piece piece = getPiece(x, y);
		Position pos = new Position(x, y);
		
		if(selectedPiece != null && validMoves.contains(pos)) {
			performMove(selectedPiece, pos);
			selectedPiece = null;
			
			chess.invalidate();
			
			nextPlayer();
		} else if(piece == selectedPiece || piece == null) {
			// deselects the piece
			selectedPiece = null;
			validMoves = new ArrayList<>();

		} else if(piece.getTeam() == turn) {
			// selects a piece
			selectedPiece = piece;
			
			for(MovesForPiece mfp : allMovesTurn) {
				if(mfp.getPiece() == selectedPiece)
					validMoves = mfp.getMoves();
			}
			
		}
		chess.invalidate();
	}
	
	/**
	 * Moves a piece to a new position, possibly by capturing the piece that's there.
	 *
	 * @param piece the piece to be moved
	 * @param to    the new position of piece
	 */
	public void performMove(final Piece piece, Position to) {
		undos.add(new Reverter());
		
		final List<Position> previousPreviousMoves = new ArrayList<>(previousMove);
		actionPerformed(new Action() {
			@Override
			public void revert() {
				previousMove = previousPreviousMoves;
			}
		});
		previousMove.clear();
		previousMove.add(new Position(piece.getX(), piece.getY()));
		previousMove.add(to);
		
		piece.onMove(to);
		
		final Piece victim = getPiece(to.getX(), to.getY());
		if(victim != null) {
			if(victim.getTeam() == Team.WHITE) {
				lostWhitePieces.add(victim);
				final Rect previousLocation = victim.getRect();
				victim.setPosition((int) ((lostWhitePieces.size() / 2.0) * blockSize), verticalMargin - blockSize - blockSize / 3, blockSize);
				actionPerformed(new Action() {
					@Override
					public void revert() {
						lostWhitePieces.remove(victim);
						victim.setRect(previousLocation);
					}
				});
			} else {
				lostBlackPieces.add(victim);
				final Rect previousLocation = victim.getRect();
				victim.setPosition((int) ((lostBlackPieces.size() / 2.0) * blockSize), verticalMargin + boardHeight * blockSize - blockSize / 3, blockSize);
				actionPerformed(new Action() {
					@Override
					public void revert() {
						lostBlackPieces.remove(victim);
						victim.setRect(previousLocation);
					}
				});
			}
			
			actionPerformed(new Action() {
				@Override
				public void revert() {
					pieces.add(victim);
				}
			});
			pieces.remove(victim);
		}
		
		final int previousX = piece.getX();
		final int previousY = piece.getY();
		actionPerformed(new Action() {
			@Override
			public void revert() {
				piece.setX(previousX);
				piece.setY(previousY);
				piece.setMoves(piece.getMoves() - 1);
			}
		});
		
		piece.setX(to.getX());
		piece.setY(to.getY());
	}
	
	/**
	 * If the game is not over, the other team gets the turn.
	 */
	public void nextPlayer() {
		turn = turn.getOtherTeam();
		
		Collections.sort(pieces);
		
		validMoves.clear();
		calculateAllMovesOfTurn();
		
		chess.invalidate();
		
		if(!handlesCheck()) updatePlayer();
	}
	
	/**
	 * Sets the current player to the player of the other team, and if the new player is a computer,
	 * their move is requested.
	 */
	private void updatePlayer() {
		if (turn == Team.WHITE) currentPlayer = chess.getWhitePlayer();
		if (turn == Team.BLACK) currentPlayer = chess.getBlackPlayer();

		if(currentPlayer instanceof ComputerPlayer) {
			chess.invalidate();
			currentPlayer.nextMove(pieces);
			chess.invalidate();
		}
	}
	
	/**
	 * returns the piece at the given coordinates, returns null if this spot is empty
	 *
	 * @param x the x of the board
	 * @param y the y of the board
	 *
	 * @return the piece at that position
	 */
	public Piece getPiece(int x, int y) {
		for(Piece p : pieces)
			if(p.getX() == x && p.getY() == y)
				return p;
		return null;
	}
	
	/**
	 * stores all moves for the player in allMovesTurn and all attacks of the enemy in
	 * allAttacksEnemy. also sets the booleans check and paralyzed
	 */
	public List<MovesForPiece> calculateAllMovesOfTurn() {
		paralyzed = true;
		allMovesTurn.clear();
		allAttacksEnemy.clear();
		checkPosition.clear();
		
		for(Piece p : pieces) {
			if(p.getTeam() == turn)
				allMovesTurn.add(new MovesForPiece(p, calculateLegalMoves(p, false)));
			else
				allAttacksEnemy.add(new MovesForPiece(p, calculateLegalMoves(p, true)));
		}
		
		check = checkForCheck(allAttacksEnemy, true);
		checkIfPlayerHasPieces();
		
		for(int i = allMovesTurn.size() - 1; i >= 0; i--) {
			MovesForPiece mfp = allMovesTurn.get(i);
			for(int i2 = mfp.getMoves().size() - 1; i2 >= 0; i2--) {
				Piece remember = getPiece(mfp.getMoves().get(i2).getX(), mfp.getMoves().get(i2).getY());
				int oldx = mfp.getPiece().getX();
				int oldy = mfp.getPiece().getY();
				int newx = mfp.getMoves().get(i2).getX();
				int newy = mfp.getMoves().get(i2).getY();
				for(Piece p : pieces) {
					if(p == mfp.getPiece()) {
						p.setX(newx);
						p.setY(newy);
					}
				}
				mfp.getPiece().setX(newx);
				mfp.getPiece().setY(newy);
				
				if(remember != null) {
					pieces.remove(remember);
				}
				
				List<MovesForPiece> allAttacksEnemyFuture = new ArrayList<>();
				for(Piece p : pieces)
					if(p.getTeam() != turn)
						allAttacksEnemyFuture.add(new MovesForPiece(p, calculateLegalMoves(p, true)));
				
				if(checkForCheck(allAttacksEnemyFuture, false))
					mfp.getMoves().remove(i2);
				else
					paralyzed = false;
				
				for(Piece p : pieces) {
					if(p == mfp.getPiece()) {
						p.setX(oldx);
						p.setY(oldy);
					}
				}
				mfp.getPiece().setX(oldx);
				mfp.getPiece().setY(oldy);
				
				if(remember != null)
					pieces.add(remember);
			}
		}
		return allMovesTurn;
	}
	
	private void checkIfPlayerHasPieces() {
		for(Piece p : pieces) {
			if(p.getTeam() == turn && p.isCapturable())
				return;
		}
		check = true;
	}
	
	/**
	 * returns true if a piece is checked
	 *
	 * @param givenList the used list of moves
	 * @param addToList if true, adds the position of the checked piece to checkPosition
	 *
	 * @return true if a piece is checked
	 */
	private boolean checkForCheck(List<MovesForPiece> givenList, boolean addToList) {
		for(Piece p : pieces)
			if(p.isRoyal() && positionInMovesForPieceList(new Position(p.getX(), p.getY()), givenList)) {
				if(addToList) checkPosition.add(new Position(p.getX(), p.getY()));
				return true;
			}
		return false;
	}
	
	/**
	 * sends messages according to check and paralyzed
	 *
	 * @return true if the game is over and false otherwise
	 */
	public boolean handlesCheck() {
		if(check && paralyzed) {
			((GameActivity) chess.getContext()).showWin(turn.getOtherTeam());
			return true;
		} else if(paralyzed) {
			((GameActivity) chess.getContext()).showTie();
			return true;
		}
		
		return false;
	}
	
	/**
	 * returns true if the given position is in the given MovesForPiece list
	 *
	 * @param position  the given position
	 * @param givenList the List which has to be searched through
	 *
	 * @return true if the given position is in the given MovesForPiece list
	 */
	private boolean positionInMovesForPieceList(Position position, List<MovesForPiece> givenList) {
		for(int i = givenList.size() - 1; i >= 0; i--)
			if(givenList.get(i).getMoves().contains(position))
				return true;
		
		return false;
	}
	
	/**
	 * calculates the new x position
	 *
	 * @param m       the move
	 * @param counter the amount of times the move is performed
	 * @param p       the given piece
	 *
	 * @return the new x
	 */
	
	private int calcNewX(Move m, int counter, Piece p) {
		return p.getX() + counter * m.getDeltaX() * p.getTeam().getCoefficient();
	}
	
	/**
	 * calculates the new y position
	 *
	 * @param m       the move
	 * @param counter the amount of times the move is performed
	 * @param p       the given piece
	 *
	 * @return the new y
	 */
	
	private int calcNewY(Move m, int counter, Piece p) {
		return p.getY() + counter * m.getDeltaY() * p.getTeam().getCoefficient();
	}
	
	/**
	 * returns true if the move of the selected piece is valid and inside the board
	 *
	 * @param m       the current move
	 * @param counter the amount that the move has to be performed
	 * @param p       the given piece
	 *
	 * @return true if it is a valid move
	 */
	private boolean onBoard(Move m, int counter, Piece p) {
		return calcNewX(m, counter, p) < boardWidth
				&& calcNewY(m, counter, p) < boardHeight
				&& calcNewX(m, counter, p) >= 0
				&& calcNewY(m, counter, p) >= 0;
	}
	
	/**
	 * returns true if there is no piece between a non-jumpable move of at least 2 blocks long, for
	 * example a pawn opener
	 *
	 * @param m       the given move
	 * @param counter the amount the move has to be performed
	 * @param p       the given piece
	 *
	 * @return returns true if there is no piece between the old and new positions of the move
	 */
	private boolean allowedBigMove(Move m, int counter, Piece p) {
		if(Math.abs(m.getDeltaX()) > 1 || Math.abs(m.getDeltaY()) > 1) {
			int newX = calcNewX(m, counter, p);
			int newY = calcNewY(m, counter, p);
			int stepX = Math.min(1, m.getDeltaX() * p.getTeam().getCoefficient());
			if(stepX < 0)
				stepX = Math.max(stepX, -1);
			int stepY = Math.min(1, m.getDeltaY() * p.getTeam().getCoefficient());
			if(stepY < 0)
				stepY = Math.max(stepY, -1);
			int oldX = calcNewX(m, counter - 1, p) + stepX;
			int oldY = calcNewY(m, counter - 1, p) + stepY;
			while(oldX != newX || oldY != newY) {
				if(getPiece(oldX, oldY) != null)
					return false;
				if(oldX != newX)
					oldX += stepX;
				if(oldY != newY)
					oldY += stepY;
			}
		}
		
		return true;
	}
	
	/**
	 * calculates all the possible positions of a given piece with a list of pieces. If
	 * onlyAttacking is true, it returns only the attack moves, if it is false it returns all
	 * attacks and moves. The function is not using check or paralyzed in their calculation.
	 *
	 * @param givenPiece    the given piece to calculate the moves for
	 * @param onlyAttacking true if the function only calculates attacks, false if both attacks and
	 *                      moves
	 *
	 * @return an list of possible positions
	 */
	private ArrayList<Position> calculateLegalMoves(Piece givenPiece, boolean onlyAttacking) {
		ArrayList<Position> legalMoves = new ArrayList<>();
		boolean attacking = onlyAttacking;
		do {
			Move[] dirs;
			if(attacking) dirs = givenPiece.getAttackDirections();
			else dirs = givenPiece.getMoveDirections();
			for(Move m : dirs) {
				if(m.isJumping()) {
					int counter = 1;
					while(onBoard(m, counter, givenPiece) && (m.isRepeating() || counter == 1)) {
						int x = calcNewX(m, counter, givenPiece);
						int y = calcNewY(m, counter, givenPiece);
						Piece p = getPiece(x, y);
						if((p == null || (p.getTeam() != givenPiece.getTeam() && p.isCapturable())) && attacking == (p != null)) {
							Position position = new Position(calcNewX(m, counter, givenPiece), calcNewY(m, counter, givenPiece));
							legalMoves.add(position);
						}
						if(getPiece(x, y) != null) break;
						counter++;
					}
				} else {
					int counter = 1;
					while(onBoard(m, counter, givenPiece) && (((getPiece(calcNewX(m, counter - 1, givenPiece), calcNewY(m, counter - 1, givenPiece)) == null) && m.isRepeating()) || counter == 1)
							&& allowedBigMove(m, counter, givenPiece)) {
						int x = calcNewX(m, counter, givenPiece);
						int y = calcNewY(m, counter, givenPiece);
						Piece p = getPiece(x, y);
						if((p == null || (p.getTeam() != givenPiece.getTeam() && p.isCapturable())) && attacking == (p != null)) {
							Position position = new Position(calcNewX(m, counter, givenPiece), calcNewY(m, counter, givenPiece));
							legalMoves.add(position);
						}
						counter++;
					}
				}
			}
			attacking = !attacking;
		} while(attacking);
		
		return legalMoves;
	}
	
	/**
	 * @return true if a piece of team team would be under attack if it stood at coordinates (x,y)
	 */
	public boolean underAttack(Team team, int x, int y) {
		for(Piece p : pieces) {
			if(p.getTeam() != team) {
				List<Position> positions = calculateLegalMoves(p, true);
				
				for(Position pos : positions)
					if(pos.getX() == x && pos.getY() == y)
						return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Stores the performed action for easy undoing.
	 *
	 * @param a the action that was performed
	 */
	public void actionPerformed(Action a) {
		undos.getLast().add(a);
	}
	
	/**
	 * Undoes the last move.
	 */
	public void undo() {
		undos.removeLast().revert();
	}
	
	public void setTurn(Team turn) {
		this.turn = turn;
	}
	
	public void setSelectedPiece(Piece p) {
		selectedPiece = p;
	}

	public boolean isParalyzed() {
		return paralyzed;
	}
	
	public int getBoardWidth() {
		return boardWidth;
	}
	
	public int getBoardHeight() {
		return boardHeight;
	}
	
	public Chess getChess() {
		return chess;
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	public Deque<Reverter> getUndos() {
		return undos;
	}
	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
}
