package com.tortoaster.customchess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesView extends View implements View.OnTouchListener {
	
	private final int lightColor, darkColor, selectedColor, highlightedColor, endangeredColor, markedColor;
	private int horizontalMargin, verticalMargin, tileSize;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private int boardLength = 7;
	private int startX = 3;
	private int startY = 3;
	
	private List<Position> normalMoves = new ArrayList<>();
	private List<Position> jumpingMoves = new ArrayList<>();
	private List<Position> repeatingMoves = new ArrayList<>();
	private List<Position> jumpingRepeatingMoves = new ArrayList<>();
	
	private boolean jumping = false, repeating = false;
	
	/**
	 * The EditPieceMovesView allows the user to specify the walking- or attacking directions of
	 * pieces.
	 */
	public PieceMovesView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		
		setOnTouchListener(this);
		
		lightColor = getResources().getColor(R.color.white);
		darkColor = getResources().getColor(R.color.darkWhite);
		selectedColor = getResources().getColor(R.color.selected);
		highlightedColor = getResources().getColor(R.color.highlighted);
		endangeredColor = getResources().getColor(R.color.threatened);
		markedColor = getResources().getColor(R.color.marked);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int y = 0; y < boardLength; y++) {
			for(int x = 0; x < boardLength; x++) {
				
				if((x + y) % 2 == 0)
					paint.setColor(lightColor);
				else
					paint.setColor(darkColor);
				
				canvas.drawRect(x * tileSize + horizontalMargin, y * tileSize + verticalMargin, (x + 1) * tileSize + horizontalMargin, (y + 1) * tileSize + verticalMargin, paint);
				
				Position pos = new Position(x, y);
				
				if(normalMoves.contains(pos))
					paint.setColor(selectedColor);
				else if(jumpingMoves.contains(pos))
					paint.setColor(highlightedColor);
				else if(repeatingMoves.contains(pos))
					paint.setColor(endangeredColor);
				else if(jumpingRepeatingMoves.contains(pos))
					paint.setColor(markedColor);
				
				canvas.drawRect(x * tileSize + horizontalMargin, y * tileSize + verticalMargin, (x + 1) * tileSize + horizontalMargin, (y + 1) * tileSize + verticalMargin, paint);
				
			}
		}
		
		paint.setColor(darkColor);
		canvas.drawCircle((horizontalMargin + startX * tileSize + tileSize / 2), verticalMargin + startY * tileSize + tileSize / 2, tileSize / 4, paint);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			if(x >= horizontalMargin && x <= (horizontalMargin + boardLength * tileSize) && y >= verticalMargin && y <= (verticalMargin + boardLength * tileSize)) {
				x = (x - horizontalMargin) / tileSize;
				y = (y - verticalMargin) / tileSize;
				if(!(x == startX && y == startY)) {
					Position pos = new Position(x, y);
					
					if(normalMoves.contains(pos))
						normalMoves.remove(pos);
					else if(jumpingMoves.contains(pos))
						jumpingMoves.remove(pos);
					else if(repeatingMoves.contains(pos))
						repeatingMoves.remove(pos);
					else if(jumpingRepeatingMoves.contains(pos))
						jumpingRepeatingMoves.remove(pos);
					else if(repeating && jumping)
						jumpingRepeatingMoves.add(pos);
					else if(repeating)
						repeatingMoves.add(pos);
					else if(jumping)
						jumpingMoves.add(pos);
					else
						normalMoves.add(pos);
					
					invalidate();
				}
			}
		}
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		tileSize = Math.min(w / boardLength, h / boardLength);
		horizontalMargin = (w - tileSize * boardLength) / 2;
		verticalMargin = (h - tileSize * boardLength) / 2;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		
		setMeasuredDimension(size, size);
	}
	
	public void setJumping(boolean b) {
		jumping = b;
	}
	
	public void setRepeating(boolean b) {
		repeating = b;
	}
	
	/**
	 * Converts the textual representation of moves to their object-counterparts.
	 */
	public void getMovesFromString(String moves) {
		String[] movesList = {};
		if(!moves.equals(""))
			movesList = moves.split(", ");
		for(String move : movesList) {
			System.out.println(move);
			String[] values = move.split(" ");
			int x = Integer.parseInt(values[0]);
			int y = Integer.parseInt(values[1]);
			boolean r = !(values[2].equals("0"));
			boolean j = !(values[3].equals("0"));
			Position pos = new Position(x * Team.WHITE.getCoefficient() + startX, y * Team.WHITE.getCoefficient() + startY);
			if(r && j)
				jumpingRepeatingMoves.add(pos);
			else if(r)
				repeatingMoves.add(pos);
			else if(j)
				jumpingMoves.add(pos);
			else
				normalMoves.add(pos);
		}
	}
	
	/**
	 * Converts move objects to a textual representation of those moves.
	 */
	public String getMoves() {
		StringBuilder moves = new StringBuilder();
		for(Position pos : normalMoves) {
			int x = pos.getX();
			int y = pos.getY();
			x = (x - startX) * Team.WHITE.getCoefficient();
			y = (y - startY) * Team.WHITE.getCoefficient();
			moves.append(x).append(" ").append(y).append(" 0 0, ");
		}
		for(Position pos : repeatingMoves) {
			int x = pos.getX();
			int y = pos.getY();
			x = (x - startX) * Team.WHITE.getCoefficient();
			y = (y - startY) * Team.WHITE.getCoefficient();
			moves.append(x).append(" ").append(y).append(" 1 0, ");
		}
		for(Position pos : jumpingMoves) {
			int x = pos.getX();
			int y = pos.getY();
			x = (x - startX) * Team.WHITE.getCoefficient();
			y = (y - startY) * Team.WHITE.getCoefficient();
			moves.append(x).append(" ").append(y).append(" 0 1, ");
		}
		for(Position pos : jumpingRepeatingMoves) {
			int x = pos.getX();
			int y = pos.getY();
			x = (x - startX) * Team.WHITE.getCoefficient();
			y = (y - startY) * Team.WHITE.getCoefficient();
			moves.append(x).append(" ").append(y).append(" 1 1, ");
		}
		return moves.toString();
	}
}
