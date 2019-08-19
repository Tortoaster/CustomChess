package com.tortoaster.customchess.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Position;
import com.tortoaster.customchess.chess.Team;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesView extends View implements View.OnTouchListener {
	
	private static final int SIZE = 7, PIECE_X = 3, PIECE_Y = 3;
	
	private boolean jumping = false, repeating = false;
	
	private int tileSize;
	
	@ColorInt
	private final int lightColor, darkColor, normalColor, jumpingColor, repeatingColor, jumpingRepeatingColor;
	
	private List<Move> moves = new ArrayList<>();
	
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public PieceMovesView(Context context) {
		this(context, null);
	}
	
	public PieceMovesView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PieceMovesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		setOnTouchListener(this);
		
		lightColor = getResources().getColor(R.color.white);
		darkColor = getResources().getColor(R.color.darkWhite);
		normalColor = getResources().getColor(R.color.selected);
		jumpingColor = getResources().getColor(R.color.highlighted);
		repeatingColor = getResources().getColor(R.color.threatened);
		jumpingRepeatingColor = getResources().getColor(R.color.marked);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				if((x + y) % 2 == 0) paint.setColor(lightColor);
				else paint.setColor(darkColor);
				
				canvas.drawRect(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize, paint);
			}
		}
		
		for(Move m : moves) {
			int x = PIECE_X + m.getDeltaX();
			int y = PIECE_Y + m.getDeltaY();
			
			if(m.isJumping()) {
				if(m.isRepeating()) paint.setColor(jumpingRepeatingColor);
				else paint.setColor(jumpingColor);
			} else {
				if(m.isRepeating()) paint.setColor(repeatingColor);
				else paint.setColor(normalColor);
			}
			
			canvas.drawRect(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize, paint);
		}
		
		paint.setColor(darkColor);
		canvas.drawCircle((PIECE_X + 0.5f) * tileSize, (PIECE_Y + 0.5f) * tileSize, tileSize / 4f, paint);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int deltaX = (int) (event.getX() / tileSize) - PIECE_X;
		int deltaY = (int) (event.getY() / tileSize) - PIECE_Y;
		
		if(deltaX != 0 || deltaY != 0) {
			Move move = new Move(deltaX, deltaY, repeating, jumping);
			
			moves.remove(move);
			moves.add(move);
			
			postInvalidate();
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		tileSize = w / SIZE;
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
	
	public void setData(String data) {
		moves = translateData(data);
	}
	
	public String getData() {
		StringBuilder data = new StringBuilder();
		
		for(Move m: moves) {
			data.append(m.getDeltaX()).append(' ').append(m.getDeltaY()).append(' ').append(m.isJumping() ? 1 : 0).append(' ').append(m.isRepeating() ? 1 : 0).append(", ");
		}
		
		return data.toString();
	}
	
	public static List<Move> translateData(String data) {
		List<Move> moves = new ArrayList<>();
		
		String[] lines = data.split(", ");
		
		for(String m : lines) {
			String[] numbers = m.split(" ");
			
			if(numbers.length == 4) moves.add(new Move(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]) != 0, Integer.parseInt(numbers[3]) != 0));
		}
		
		return moves;
	}
}
