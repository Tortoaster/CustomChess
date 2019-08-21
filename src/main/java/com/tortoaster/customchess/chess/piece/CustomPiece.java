package com.tortoaster.customchess.chess.piece;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Team;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CustomPiece extends Piece {
	
	public static final String PREFIX = "c_";
	
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final String name;
	private int value;
	private boolean royal, capturable;
	private Move[] moves, attacks;
	private Bitmap light, dark;
	
	/**
	 * @param x     The x coordinate of the piece.
	 * @param y     The y coordinate of the piece.
	 * @param team  The team of the piece.
	 * @param name  The name of the piece.
	 * @param board The board the piece will be drawn on.
	 */
	public CustomPiece(int x, int y, Team team, String name, Board board, Context context) {
		super(x, y, team, Kind.CUSTOM, board);
		this.name = name;
		
		FileInputStream fis = null;
		StringBuilder builder = new StringBuilder();
		
		try {
			fis = context.openFileInput("p_" + name + ".txt");
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
		
		if(content.isEmpty()) return;
		
		String[] lines = content.split("\\|");
		
		value = Integer.parseInt(lines[0]);
		royal = Boolean.parseBoolean(lines[1]);
		capturable = Boolean.parseBoolean(lines[2]);
		String movesString = lines[3];
		String attacksString = lines[4];
		List<Move> listmoves = new ArrayList<>();
		List<Move> listattacks = new ArrayList<>();
		
		String[] movesList = {};
		String[] attacksList = {};
		if(!movesString.equals(""))
			movesList = movesString.split(", ");
		for(String move : movesList) {
			String[] values = move.split(" ");
			int readx = Integer.parseInt(values[0]);
			int ready = Integer.parseInt(values[1]);
			boolean r = !(values[2].equals("0"));
			boolean j = !(values[3].equals("0"));
			listmoves.add(new Move(readx, ready, r, j));
		}
		
		if(!attacksString.equals(""))
			attacksList = attacksString.split(", ");
		for(String attack : attacksList) {
			String[] values = attack.split(" ");
			int readx = Integer.parseInt(values[0]);
			int ready = Integer.parseInt(values[1]);
			boolean r = !(values[2].equals("0"));
			boolean j = !(values[3].equals("0"));
			listattacks.add(new Move(readx, ready, r, j));
		}
		
		int length = listmoves.size();
		moves = new Move[length];
		for(int i = 0; i < length; i++)
			moves[i] = listmoves.get(i);
		
		length = listattacks.size();
		attacks = new Move[length];
		for(int i = 0; i < length; i++)
			attacks[i] = listattacks.get(i);
		
	}
	
	public CustomPiece(int x, int y, Team team, Board board, String name, int value, boolean royal, boolean capturable, Move[] moves, Move[] attacks, Bitmap light, Bitmap dark) {
		super(x, y, team, Kind.CUSTOM, board);
		
		this.name = name;
		this.value = value;
		this.royal = royal;
		this.capturable = capturable;
		this.moves = moves;
		this.attacks = attacks;
		this.light = light;
		this.dark = dark;
	}
	
	@Override
	public Piece copy() {
		return new CustomPiece(getX(), getY(), getTeam(), getBoard(), name, value, royal, capturable, moves, attacks, light, dark);
	}
	
	/**
	 * Draws the picture of the custom piece on the canvas.
	 */
	public void draw(Canvas canvas) {
		if(getBitmap() != null)
			canvas.drawBitmap(getBitmap(), null, getRect(), PAINT);
	}
	
	/**
	 * Decodes the pictures that were saved with the same name as the custom piece.
	 */
	public void loadImage(Context context) {
		try {
			light = BitmapFactory.decodeStream(context.openFileInput("l_" + name.substring(PREFIX.length()) + ".png"));
			dark = BitmapFactory.decodeStream(context.openFileInput("d_" + name.substring(PREFIX.length()) + ".png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the bitmap that corresponds with the team color.
	 */
	public Bitmap getBitmap() {
		if(getTeam() == Team.LIGHT) return light;
		return dark;
	}
	
	public Bitmap getBitmap(Team team) {
		if(team == Team.LIGHT) return light;
		return dark;
	}
	
	/**
	 * These getters return the moves, attacks, name, if the piece is royal, capturable and the value of the piece.
	 */
	
	public Move[] getMoveDirections() {
		return moves;
	}
	
	public Move[] getAttackDirections() {
		return attacks;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isRoyal() {
		return royal;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isCapturable() {
		return capturable;
	}
}