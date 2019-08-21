package com.tortoaster.customchess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.piece.Kind;
import com.tortoaster.customchess.chess.piece.Piece;

import java.util.Arrays;

public class BoardEditorView extends View {
	
	private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private boolean erasing;
	
	private float tileSize;
	
	@ColorInt
	private int lightColor, darkColor;
	
	private Piece[][] pieces;
	
	private Piece selected;
	
	public BoardEditorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		
		lightColor = getResources().getColor(R.color.very_light);
		darkColor = getResources().getColor(R.color.light);
		
		pieces = new Piece[1][1];
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		for(int y = 0; y < pieces[0].length; y++) {
			for(int x = 0; x < pieces.length; x++) {
				if((x + y) % 2 == 0) PAINT.setColor(lightColor);
				else PAINT.setColor(darkColor);
				
				canvas.drawRect(x * tileSize, (y + Board.PIECE_OFFSET) * tileSize, (x + 1) * tileSize, (y + Board.PIECE_OFFSET + 1) * tileSize, PAINT);
				
				if(pieces[x][y] != null) canvas.drawBitmap(pieces[x][y].getBitmap(), null, new RectF(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize), PAINT);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) (event.getX() / tileSize);
		int y = (int) (event.getY() / tileSize - Board.PIECE_OFFSET);
		
		if(x >= 0 && x < pieces.length && y >= 0 && y < pieces[0].length) {
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
				erasing = pieces[x][y] != null;
				
				if(erasing) pieces[x][y] = null;
				else pieces[x][y] = selected.copy();
			} else {
				if(erasing) pieces[x][y] = null;
				else if(pieces[x][y] == null) pieces[x][y] = selected.copy();
			}
			
			postInvalidate();
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		tileSize = Math.min((float) w / pieces.length, h / (pieces[0].length + Board.PIECE_OFFSET));
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float tileSize = Math.min((float) MeasureSpec.getSize(widthMeasureSpec) / pieces.length, MeasureSpec.getSize(heightMeasureSpec) / (pieces[0].length + Board.PIECE_OFFSET));
		
		setMeasuredDimension((int) (pieces.length * tileSize), (int) ((pieces[0].length + Board.PIECE_OFFSET) * tileSize));
	}
	
	public void setWidth(int width) {
		Piece[][] resized = new Piece[width][pieces[0].length];
		
		for(int y = 0; y < resized[0].length; y++) {
			for(int x = 0; x < Math.min(resized.length, pieces.length); x++) {
				resized[x][y] = pieces[x][y];
			}
		}
		
		pieces = resized;
		
		requestLayout();
	}
	
	public void setHeight(int height) {
		Piece[][] resized = new Piece[pieces.length][height];
		
		for(int x = 0; x < resized.length; x++) {
			resized[x] = Arrays.copyOf(pieces[x], height);
		}
		
		pieces = resized;
		
		requestLayout();
	}
	
	public void setSelected(Piece selected) {
		this.selected = selected;
	}
	
	public void setPieces(Piece[][] pieces) {
		this.pieces = pieces;
	}
	
	public Piece[][] getPieces() {
		return pieces;
	}
}
