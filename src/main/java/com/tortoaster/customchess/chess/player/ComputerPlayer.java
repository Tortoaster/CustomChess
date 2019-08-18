package com.tortoaster.customchess.chess.player;

import com.tortoaster.customchess.activity.GameActivity;
import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.MovesForPiece;
import com.tortoaster.customchess.chess.piece.Kind;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComputerPlayer implements Player {
	
	public static final int DEFAULT_PC_STRENGTH = 3;
	
	private int strength;
	private Team color;
	private Board board;
	private List<EvalAndMove> currentMoves = new ArrayList<>();
	
	private Lock lock = new ReentrantLock();
	public Condition finishedMove = lock.newCondition();
	
	/**
	 * The computer is an AI bot that tries to play chess at a reasonable level
	 *
	 * @param board    The current instantiation of the board being used.
	 * @param color    The color the computer has been assigned.
	 * @param strength The strength (a.k.a. search depth) of the computer.
	 */
	public ComputerPlayer(Board board, Team color, int strength) {
		this.board = board;
		this.color = color;
		this.strength = strength;
	}
	
	/**
	 * Method in between making a move. For convenience when other types of moves need to be done
	 * (random mode for example).
	 *
	 * @param pieces The current list of pieces on the board.
	 */
	
	public void nextMove(final List<Piece> pieces) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				lock.lock();
				try {
					nextMoveGood(pieces);
				} finally {
					lock.unlock();
				}
			}
		});
		thread.start();
	}

	/**
	 * This method is the start of the move. It calculates for every current move possible it's
	 * evaluation. Then it shuffles the list and makes the move with the highest evaluation. It
	 * updates the board so that another player can make its turn.
	 *
	 * @param pieces The current list of pieces on the board.
	 */

	private void nextMoveGood(List<Piece> pieces) {
		currentMoves.clear();
		
		//List with for every move its evaluation.
		//Makes from inconvenient 2 dimensional list a 1 dimensional list for convenience.
		board.setTurn(color);
		List<MoveAndPiece> allMoves = fromMovesToGoodMoves(board.calculateAllMovesOfTurn());
		final int size = allMoves.size();
		for(int i = size - 1; i >= 0; i--) {
			board.performMove(allMoves.get(i).getPiece(), allMoves.get(i).getMove());
			double eval = intMiniMax(pieces, 1, color.getOtherTeam(), Double.MIN_VALUE, Double.MAX_VALUE);
			MoveAndPiece map = new MoveAndPiece(allMoves.get(i).getMove(), allMoves.get(i).getPiece());
			currentMoves.add(new EvalAndMove(eval, map));
			board.undo();
			final int progress = ((size - i) * 100) / size;
			new Thread(new Runnable() {
				@Override
				public void run() {
					((GameActivity) board.getChess().getContext()).getThinkingProcess().setProgress(progress);
				}
			}).start();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				((GameActivity) board.getChess().getContext()).getThinkingProcess().setProgress(0);
			}
		}).start();
		
		//Shuffle currentMoves
		Collections.shuffle(currentMoves);
		EvalAndMove bestMove = null;
		
		for(EvalAndMove eam : currentMoves) {
			if(bestMove == null || eam.getEvaluation() > bestMove.getEvaluation()) {
				bestMove = eam;
			}
		}
		
		//Checkmate
		if(bestMove == null) return;
		//Log.e(bestMove.getMap().getPiece().getKind() + "", bestMove.getMap().getMove().getX() + ", " + bestMove.getMap().getMove().getY());
		Piece pieceToMove = bestMove.getMap().getPiece();
		Position newPosition = bestMove.getMap().getMove();
		
		board.performMove(pieceToMove, newPosition);
		board.setTurn(color);
		board.getChess().invalidate();
		board.nextPlayer();
	}
	
	/**
	 * This is the minimax part of the algorithm. It alternates between min and max (Black and
	 * White) to check what move is best for what player. It is recursive and stops when the maximum
	 * depth is reached.
	 *
	 * @param pieces The current list of pieces that needs to be evaluated.
	 * @param depth  The current depth of the search.
	 * @param turn   Who's turn it is (Black or White).
	 * @param alpha  The alpha-value of the alpha-beta pruning.
	 * @param beta   The beta-value of the alpha-beta pruning.
	 *
	 * @return The evaluation of a specific move.
	 */
	
	private double intMiniMax(List<Piece> pieces, int depth, Team turn, double alpha, double beta) {
		if(board.isParalyzed())
			return Integer.MAX_VALUE;
		if(depth >= strength) {
			return quiescence(pieces, strength, turn, Double.MIN_VALUE, Double.MAX_VALUE);
		}
		
		// MAX turn
		if(turn == color) {
			board.setTurn(turn);
			List<MoveAndPiece> nextMoves = fromMovesToGoodMoves(board.calculateAllMovesOfTurn());
			//Initial bestValue.
			double bestValue = Double.MIN_VALUE;
			for(int i = nextMoves.size() - 1; i >= 0; i--) {
				// Move.
				//MoveAndPiece moveAndRemember = move(pieces, nextMoves, i);
				board.performMove(nextMoves.get(i).getPiece(), nextMoves.get(i).getMove());
				
				// Update evaluations and alpha.
				
				// Recursive call.
				double evaluation = intMiniMax(pieces, depth + 1, turn.getOtherTeam(), alpha, beta);
				alpha = Math.max(alpha, evaluation);
				bestValue = Math.max(bestValue, evaluation);
				
				// Undo move.
				board.undo();
				
				// Cut off branch according to alpha-beta pruning.
				if(beta <= alpha)
					break;
			}
			return bestValue;
			// MIN turn
		} else {
			board.setTurn(turn);
			List<MoveAndPiece> nextMoves = fromMovesToGoodMoves(board.calculateAllMovesOfTurn());
			// Initial bestValue.
			double bestValue = Double.MAX_VALUE;
			for(int i = nextMoves.size() - 1; i >= 0; i--) {
				
				// Move.
				board.performMove(nextMoves.get(i).getPiece(), nextMoves.get(i).getMove());
				//MoveAndPiece moveAndRemember = move(pieces, nextMoves, i);
				
				// Update evaluations and beta.
				
				// Recursive call.
				double evaluation = intMiniMax(pieces, depth + 1, turn.getOtherTeam(), alpha, beta);
				beta = Math.min(beta, evaluation);
				bestValue = Math.min(bestValue, evaluation);
				
				// Undo move.
				board.undo();
				
				// Cut off branch according to alpha-beta pruning.
				if(beta <= alpha)
					break;
			}
			return bestValue;
		}
	}
	
	/**
	 * This method is an extension of the minimax algorithm. With just minimax with a constant
	 * depth, it is prone to the horizon effect. When the computer looks to a depth and it is up a
	 * pawn for example, it doesn't see whether the queen might be captured the very next move. To
	 * fix this, the nodes that are not quiet will be expanding until a certain depth.
	 *
	 * @param pieces The current list of pieces.
	 * @param depth  The current depth of the quiescence search (different from depth of minimax
	 *               search).
	 * @param turn   Who's turn it is (Black or White).
	 * @param alpha  The current alpha value of the alpha-beta pruning.
	 * @param beta   The current beta value of the alpha-beta pruning.
	 */
	
	private double quiescence(List<Piece> pieces, int depth, Team turn, double alpha, double beta) {
		//Further expand not quiet nodes.
		List<MoveAndPiece> notQuietMoves = quietNode(pieces, turn);
		
		// Checks when to end the search.
		if(notQuietMoves.isEmpty() || depth == 0 || board.isParalyzed())
			return evaluate(pieces);
		if(turn == color) {
			board.setTurn(turn);
			double bestValue = Double.MIN_VALUE;
			for(int i = notQuietMoves.size() - 1; i >= 0; i--) {
				// Move.
				board.performMove(notQuietMoves.get(i).getPiece(), notQuietMoves.get(i).getMove());
				//MoveAndPiece moveAndRemember = move(pieces, notQuietMoves, i);
				
				// Update bestValue and alpha.
				// Recursion.
				double evaluation = quiescence(pieces, depth - 1, turn.getOtherTeam(), alpha, beta);
				alpha = Math.max(alpha, evaluation);
				bestValue = Math.max(bestValue, evaluation);
				
				// Undo move.
				board.undo();
				
				// Break according to alpha-beta pruning.
				if(beta <= alpha)
					break;
			}
			return bestValue;
		} else {
			board.setTurn(turn);
			double bestValue = Double.MAX_VALUE;
			for(int i = notQuietMoves.size() - 1; i >= 0; i--) {
				
				board.performMove(notQuietMoves.get(i).getPiece(), notQuietMoves.get(i).getMove());
				//MoveAndPiece moveAndRemember = move(pieces, notQuietMoves, i);
				
				// Update bestValue and beta.
				// Recursion.
				double evaluation = quiescence(pieces, depth - 1, turn.getOtherTeam(), alpha, beta);
				beta = Math.min(beta, evaluation);
				bestValue = Math.min(bestValue, evaluation);
				
				// Undo move.
				board.undo();
				
				// Break according to alpha-beta pruning.
				if(beta <= alpha)
					break;
			}
			return bestValue;
		}
	}
	
	/**
	 * Determines if a node is quiet or not. Based on if any significant captures can take place the
	 * next move.
	 *
	 * @param pieces The current list of pieces.
	 * @param turn   Who's turn it is (Black or White).
	 *
	 * @return List of notQuietNodes. When empty, the state is quiet.
	 */
	private List<MoveAndPiece> quietNode(List<Piece> pieces, Team turn) {
		List<MoveAndPiece> notQuietNodes = new ArrayList<>();
		board.setTurn(turn);
		List<MoveAndPiece> moves = fromMovesToGoodMoves(board.calculateAllMovesOfTurn());
		for(MoveAndPiece map : moves) {
			//check if piece can capture piece
			for(Piece p : pieces) {
				if(p.getX() == map.getMove().getX() && p.getY() == map.getMove().getY() && map.getPiece() != p && p.getTeam() != turn) {
					//check if a piece with lower value captures piece with higher value.
					if(map.getPiece().getKind().getValue() >= p.getKind().getValue())
						notQuietNodes.add(map);
				}
			}
		}
		return notQuietNodes;
	}
	
	/**
	 * This is the evaluation function. You put in a position, and it tells you who is winning with
	 * a double value. Positive means that this computer is winning, negative means that the
	 * opponent of this computer is winning.
	 *
	 * @param pieces The current list of pieces.
	 *
	 * @return The evaluation of the board.
	 */
	private double evaluate(List<Piece> pieces) {
		double evaluation = 0;
		for(Piece p : pieces) {
			//Basic sum of value of pieces
			evaluation += color.getCoefficient() * p.getValue() * p.getTeam().getCoefficient();
			
			//Pawn further on the board
			if(p.getKind() == Kind.PAWN) {
				int distance;
				if(p.getTeam() == Team.BLACK) {
					distance = p.getY() - 1;
				} else {
					distance = board.getBoardHeight() - p.getY() - 2;
				}
				evaluation += ((2.0 * distance * color.getCoefficient() * p.getTeam().getCoefficient()) / (double) (board.getBoardHeight()));
			}
		}
		return evaluation;
	}
	
	/**
	 * Make from List<MovesForPiece> a List<MoveAndPiece> to make the entire class operate much
	 * better. This effectively turns a 2 dimensional list (which caused trouble due to changing the
	 * list in run-time) into a 1 dimensional list.
	 *
	 * @param moves All the pieces in the list, with in that a list for every piece with its
	 *              possible moves.
	 *
	 * @return All pieces and moves that belong to each other in 1 list.
	 */
	private List<MoveAndPiece> fromMovesToGoodMoves(List<MovesForPiece> moves) {
		List<MoveAndPiece> goodMoves = new ArrayList<>();
		for(MovesForPiece mfp : moves) {
			for(Position pos : mfp.getMoves()) {
				goodMoves.add(new MoveAndPiece(pos, mfp.getPiece()));
			}
		}
		return goodMoves;
	}
	
	public double getEvaluation(List<Piece> pieces) {
		return evaluate(pieces);
	}
	
	public Lock getLock() {
		return lock;
	}
	
	/**
	 * Helper class to create something that can keep track of a move and a piece.
	 */
	private class MoveAndPiece {
		private Position move;
		private Piece piece;
		
		MoveAndPiece(Position move, Piece piece) {
			this.move = move;
			this.piece = piece;
		}
		
		public Position getMove() {
			return move;
		}
		
		public Piece getPiece() {
			return piece;
		}
	}
	
	/**
	 * Helper class to create something that can keep track of a move and a piece and an
	 * evaluation.
	 */
	private class EvalAndMove {
		private double evaluation;
		private MoveAndPiece map;
		
		EvalAndMove(double evaluation, MoveAndPiece map) {
			this.evaluation = evaluation;
			this.map = map;
		}
		
		public double getEvaluation() {
			return evaluation;
		}
		
		public MoveAndPiece getMap() {
			return map;
		}
	}
}


