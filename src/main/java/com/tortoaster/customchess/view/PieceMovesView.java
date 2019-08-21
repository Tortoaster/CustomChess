package com.tortoaster.customchess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Move;

import java.util.ArrayList;
import java.util.List;

public class PieceMovesView extends View {
	
	private static final int SIZE = 7, PIECE_X = 3, PIECE_Y = 3;
	
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private boolean jumping, repeating, erasing;
	
	private float tileSize;
	
	@ColorInt
	private final int lightColor, darkColor, normalColor, jumpingColor, repeatingColor, jumpingRepeatingColor;
	
	private List<Move> moves;
	
	public PieceMovesView(Context context) {
		this(context, null);
	}
	
	public PieceMovesView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public PieceMovesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		lightColor = getResources().getColor(R.color.very_light);
		darkColor = getResources().getColor(R.color.light);
		normalColor = getResources().getColor(R.color.selected);
		jumpingColor = getResources().getColor(R.color.highlighted);
		repeatingColor = getResources().getColor(R.color.threatened);
		jumpingRepeatingColor = getResources().getColor(R.color.marked);
		
		moves = Move.translateData("");
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				if((x + y) % 2 == 0) PAINT.setColor(lightColor);
				else PAINT.setColor(darkColor);
				
				canvas.drawRect(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize, PAINT);
			}
		}
		
		for(Move m : moves) {
			int x = PIECE_X + m.getDeltaX();
			int y = PIECE_Y + m.getDeltaY();
			
			if(m.isJumping()) {
				if(m.isRepeating()) PAINT.setColor(jumpingRepeatingColor);
				else PAINT.setColor(jumpingColor);
			} else {
				if(m.isRepeating()) PAINT.setColor(repeatingColor);
				else PAINT.setColor(normalColor);
			}
			
			canvas.drawRect(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize, PAINT);
		}
		
		PAINT.setColor(darkColor);
		
		canvas.drawCircle((PIECE_X + 0.5f) * tileSize, (PIECE_Y + 0.5f) * tileSize, tileSize / 4f, PAINT);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int deltaX = (int) (event.getX() / tileSize) - PIECE_X;
		int deltaY = (int) (event.getY() / tileSize) - PIECE_Y;
		
		if((deltaX != 0 || deltaY != 0) && Math.abs(deltaX) <= 3 && Math.abs(deltaY) <= 3) {
			Move move = new Move(deltaX, deltaY, repeating, jumping);
			
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				erasing = moves.contains(move);
				
				if(erasing) moves.remove(move);
				else moves.add(move);
			} else {
				if(erasing) moves.remove(move);
				else if(!moves.contains(move)) moves.add(move);
			}
			
			postInvalidate();
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		tileSize = (float) w / SIZE;
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
		moves = Move.translateData(data);
	}
	
	public String getData() {
		StringBuilder data = new StringBuilder();
		
		for(Move m : moves) {
			data.append(m.getDeltaX()).append(' ').append(m.getDeltaY()).append(' ').append(m.isJumping() ? 1 : 0).append(' ').append(m.isRepeating() ? 1 : 0).append(", ");
		}
		
		return data.toString();
	}
}
