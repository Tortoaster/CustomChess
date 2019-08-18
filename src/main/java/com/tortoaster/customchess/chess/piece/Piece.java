package com.tortoaster.customchess.chess.piece;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;

import java.io.FileNotFoundException;

public class Piece implements Comparable<Piece> {
	
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Board board;
	private int x, y, drawX1, drawY1, drawX2, drawY2, drawSize, moves = 0;
	private Team team;
	private Kind kind;
	
	/**
	 * This constructor should only be used for predefined (non-custom) pieces that do not have
	 * their own class. Use createPiece as a more rigid alternative.
	 *
	 * @param x     the piece's x-coordinate
	 * @param y     the piece's y-coordinate
	 * @param team  the piece's color
	 * @param kind  the kind of piece (e.g. Knight, Rook...)
	 * @param board the board this piece is on
	 */
	public Piece(int x, int y, Team team, Kind kind, Board board) {
		this.x = x;
		this.y = y;
		this.team = team;
		this.kind = kind;
		this.board = board;
	}
	
	/**
	 * @return a new piece with custom/overridden attributes if applicable
	 */
	public static Piece createPiece(int x, int y, Team team, String name, Board board, Context context) {
		if(name.isEmpty()) return null;
		
		switch(name) {
			case "pawn":
				return new Pawn(x, y, team, board);
			case "king":
				return new King(x, y, team, board);
		}
		
		for(Kind k : Kind.values())
			if(k.getName() != null && k.getName().equals(name))
				return new Piece(x, y, team, k, board);
		
		try {
			context.openFileInput("p_" + name + ".txt");
			
			Piece p = new CustomPiece(x, y, team, name, board, context);
			p.loadImage(context);
			
			return p;
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void update() {
	}
	
	public void onMove(Position to) {
		for(Piece p : board.getPieces()) p.update();
		
		moves++;
	}
	
	public void setPosition(int x, int y, int size) {
		drawX1 = x;
		drawY1 = y;
		drawX2 = x + size;
		drawY2 = y + size;
		
		drawSize = size;
	}
	
	public void draw(Canvas canvas) {
		if(kind.getBitmap(team) != null) canvas.drawBitmap(kind.getBitmap(team), null, new Rect(drawX1, drawY1, drawX2, drawY2), PAINT);
	}
	
	@Override
	public int compareTo(@NonNull Piece p) {
		return p.getY() < y ? 1 : p.getY() > y ? -1 : 0;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		int delta = (x - this.x) * drawSize;
		
		drawX1 += delta;
		drawX2 += delta;
		
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		int delta = (y - this.y) * drawSize;
		
		drawY1 += delta;
		drawY2 += delta;
		
		this.y = y;
	}
	
	public void loadImage(Context context) {
		kind.loadImage(context);
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public void setKind(Kind kind) {
		this.kind = kind;
	}
	
	public int getMoves() {
		return moves;
	}
	
	public void setMoves(int moves) {
		this.moves = moves;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Rect getRect() {
		return new Rect(drawX1, drawY1, drawX2, drawY2);
	}
	
	public void setRect(Rect rect) {
		drawX1 = rect.left;
		drawY1 = rect.top;
		drawX2 = rect.right;
		drawY2 = rect.bottom;
		
		drawSize = drawX2 - drawX1;
	}
	
	public Move[] getMoveDirections() {
		return kind.getMoveDirections();
	}
	
	public Move[] getAttackDirections() {
		return kind.getAttackDirections();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public String getName() {
		return kind.getName();
	}
	
	public boolean isRoyal() {
		return kind.isRoyal();
	}
	
	public int getValue() {
		return kind.getValue();
	}
	
	public boolean isCapturable() {
		return kind.isCapturable();
	}
}