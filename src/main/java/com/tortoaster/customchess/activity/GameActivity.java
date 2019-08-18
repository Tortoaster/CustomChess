package com.tortoaster.customchess.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.view.Chess;
import com.tortoaster.customchess.chess.player.ComputerPlayer;
import com.tortoaster.customchess.chess.player.HumanPlayer;
import com.tortoaster.customchess.chess.piece.Kind;
import com.tortoaster.customchess.chess.piece.King;
import com.tortoaster.customchess.chess.piece.Pawn;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Team;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {
	
	private Chess chess;
	private ProgressBar thinkingProcess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		thinkingProcess = (ProgressBar) findViewById(R.id.thinking_process);
		chess = (Chess) findViewById(R.id.chess);
		
		Intent intent = getIntent();
		
		int strength = intent.getIntExtra("strength", 2);
		
		loadBoard(intent.getStringExtra("board"));
		
		if(intent.getBooleanExtra("p1c", false)) {
			ComputerPlayer com1 = new ComputerPlayer(chess.getBoard(), Team.WHITE, strength);
			//Thread thread = new Thread(com1);
			chess.setWhitePlayer(com1);
			chess.getWhitePlayer().nextMove(chess.getBoard().getPieces());
			//thread.start();
		} else chess.setWhitePlayer(new HumanPlayer(Team.WHITE));
		
		if(intent.getBooleanExtra("p2c", false)) {
			ComputerPlayer com2 = new ComputerPlayer(chess.getBoard(), Team.BLACK, strength);
			//Thread thread = new Thread(com2);
			chess.setBlackPlayer(com2);
			//thread.start();
		} else chess.setBlackPlayer(new HumanPlayer(Team.BLACK));
		
	}
	
	/**
	 * Loads a board setup from a file.
	 *
	 * @param name the name of the file to be loaded
	 */
	private void loadBoard(String name) {
		if(name == null) name = "";
		
		FileInputStream fis = null;
		StringBuilder builder = new StringBuilder();
		
		try {
			fis = openFileInput("b_" + name + ".txt");
			InputStreamReader fus = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(fus);
			String line;
			
			while((line = br.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		String content = builder.toString();
		
		if(content.isEmpty()) {
			
			List<Piece> standard = new ArrayList<>();
			Board board = new Board(8, 8, standard, chess);
			
			standard.add(new Piece(0, 7, Team.WHITE, Kind.ROOK, board));
			standard.add(new Piece(1, 7, Team.WHITE, Kind.KNIGHT, board));
			standard.add(new Piece(2, 7, Team.WHITE, Kind.BISHOP, board));
			standard.add(new Piece(3, 7, Team.WHITE, Kind.QUEEN, board));
			standard.add(new King(4, 7, Team.WHITE, board));
			standard.add(new Piece(5, 7, Team.WHITE, Kind.BISHOP, board));
			standard.add(new Piece(6, 7, Team.WHITE, Kind.KNIGHT, board));
			standard.add(new Piece(7, 7, Team.WHITE, Kind.ROOK, board));
			
			standard.add(new Pawn(0, 6, Team.WHITE, board));
			standard.add(new Pawn(1, 6, Team.WHITE, board));
			standard.add(new Pawn(2, 6, Team.WHITE, board));
			standard.add(new Pawn(3, 6, Team.WHITE, board));
			standard.add(new Pawn(4, 6, Team.WHITE, board));
			standard.add(new Pawn(5, 6, Team.WHITE, board));
			standard.add(new Pawn(6, 6, Team.WHITE, board));
			standard.add(new Pawn(7, 6, Team.WHITE, board));
			
			standard.add(new Piece(0, 0, Team.BLACK, Kind.ROOK, board));
			standard.add(new Piece(1, 0, Team.BLACK, Kind.KNIGHT, board));
			standard.add(new Piece(2, 0, Team.BLACK, Kind.BISHOP, board));
			standard.add(new Piece(3, 0, Team.BLACK, Kind.QUEEN, board));
			standard.add(new King(4, 0, Team.BLACK, board));
			standard.add(new Piece(5, 0, Team.BLACK, Kind.BISHOP, board));
			standard.add(new Piece(6, 0, Team.BLACK, Kind.KNIGHT, board));
			standard.add(new Piece(7, 0, Team.BLACK, Kind.ROOK, board));
			
			standard.add(new Pawn(0, 1, Team.BLACK, board));
			standard.add(new Pawn(1, 1, Team.BLACK, board));
			standard.add(new Pawn(2, 1, Team.BLACK, board));
			standard.add(new Pawn(3, 1, Team.BLACK, board));
			standard.add(new Pawn(4, 1, Team.BLACK, board));
			standard.add(new Pawn(5, 1, Team.BLACK, board));
			standard.add(new Pawn(6, 1, Team.BLACK, board));
			standard.add(new Pawn(7, 1, Team.BLACK, board));
			
			chess.setBoard(board);
			
		} else {
			
			String[] lines = content.split("\n");
			int width = lines[0].split(", ").length;
			int height = lines.length;
			List<Piece> pieces = new ArrayList<>();
			Board board = new Board(width, height, pieces, chess);
			
			for(int y = 0; y < height; y++) {
				String[] line = lines[y].split(", ");
				for(int x = 0; x < width; x++) {
					Team team = line[x].charAt(0) == 'b' ? Team.BLACK : Team.WHITE;
					Piece p = Piece.createPiece(x, y, team, line[x].substring(1), board, this);
					
					if(p != null) {
						pieces.add(p);
					}
				}
			}
			
			chess.setBoard(board);
			
		}
	}
	
	/**
	 * Shows a win message for the team that has won the game.
	 *
	 * @param team the team that has won
	 */
	public void showWin(Team team) {
		Toast.makeText(this, team + " won", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Will display a tie message if stalemate has been reached.
	 */
	public void showTie() {
		Toast.makeText(this, "tie", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Will close GameActivity and thus return to the start menu.
	 */
	public void finishGame(View view) {
		finish();
	}
	
	/**
	 * Undoes the last two moves when the undo button is pressed.
	 */
	public void undo(View view) {
		if(chess.getBoard().getUndos().size() >= 2 && !(chess.getBoard().getCurrentPlayer() instanceof ComputerPlayer)) {
			chess.getBoard().undo();
			chess.getBoard().undo();
			chess.getBoard().calculateAllMovesOfTurn();
			chess.getBoard().handlesCheck();
			chess.getBoard().setSelectedPiece(null);
			chess.invalidate();
		}
	}
	
	public ProgressBar getThinkingProcess() {
		return thinkingProcess;
	}
}
