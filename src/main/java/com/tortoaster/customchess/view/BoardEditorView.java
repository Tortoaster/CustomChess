package com.tortoaster.customchess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.activity.BoardEditorActivity;
import com.tortoaster.customchess.chess.piece.Kind;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoardEditorView extends View {
	
	private static Paint PAINT = new Paint();
	
	private boolean eraser;
	
	private int lightColor, darkColor;
	private int boardWidth, boardHeight;
	private int viewWidth, viewHeight;
	private int horizontalMargin, verticalMargin;
	
	private float tileSize;
	
	private List<Piece> pieces = new ArrayList<>();
	
	private Team team = Team.WHITE;
	
	/**
	 * The BoardEditorView is used to make new initial setups of boards to play with.
	 */
	public BoardEditorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		
		lightColor = getResources().getColor(R.color.light);
		darkColor = getResources().getColor(R.color.dark);
		
		for(Kind k : Kind.values()) {
			k.loadImage(getContext());
		}
		
		boardWidth = boardHeight = 8;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		for(int y = 0; y < boardHeight; y++) {
			for(int x = 0; x < boardWidth; x++) {
				if((x + y) % 2 == 0)
					PAINT.setColor(lightColor);
				else
					PAINT.setColor(darkColor);
				
				canvas.drawRect(x * tileSize + horizontalMargin, y * tileSize + tileSize / 3 + verticalMargin, (x + 1) * tileSize + horizontalMargin, (y + 1) * tileSize + tileSize / 3 + verticalMargin, PAINT);
			}
		}
		
		for(Piece p : pieces) {
			p.draw(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		int x = (int) ((event.getX() - horizontalMargin) / tileSize);
		int y = (int) ((event.getY() - verticalMargin) / tileSize);
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			eraser = getPiece(x, y) != null;
		}
		
		if(x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
			if(eraser) {
				Piece p = getPiece(x, y);
				
				if(p != null) {
					pieces.remove(p);
					
					invalidate();
				}
			} else if(getPiece(x, y) == null) {
				Piece p = Piece.createPiece(x, y, team, ((BoardEditorActivity) getContext()).getSelectedPiece(), null, getContext());
				
				if(p != null) addPiece(p);
			}
		}
		
		return true;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		viewWidth = w;
		viewHeight = h;
		
		recalculate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec + (int) (tileSize / 3 * 2));
	}
	
	private void recalculate() {
		tileSize = (float) viewWidth / Math.max(boardWidth, boardHeight);
		
		requestLayout();
		
		horizontalMargin = (int) (viewWidth - boardWidth * tileSize) / 2;
		verticalMargin = (int) (viewHeight - boardHeight * tileSize) / 2;
		
		for(Piece p : pieces) {
			int temp = (int) (p.getY() * tileSize + verticalMargin);
			if(p.getName().equals("wall")) temp += tileSize / 3;
			p.setPosition((int) (p.getX() * tileSize + horizontalMargin), temp, (int) tileSize);
		}
		
		invalidate();
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public void setBoardWidth(int boardWidth) {
		if(this.boardWidth > boardWidth)
			for(Iterator<Piece> i = pieces.iterator(); i.hasNext(); )
				if(i.next().getX() >= boardWidth) i.remove();
		
		this.boardWidth = boardWidth;
		recalculate();
	}
	
	public void setBoardHeight(int boardHeight) {
		if(this.boardHeight > boardHeight)
			for(Iterator<Piece> i = pieces.iterator(); i.hasNext(); )
				if(i.next().getY() >= boardHeight) i.remove();
		
		this.boardHeight = boardHeight;
		recalculate();
	}
	
	/**
	 * Places a piece on the board.
	 *
	 * @param p the piece to be placed on the board.
	 */
	public void addPiece(Piece p) {
		pieces.add(p);
		
		int temp = (int) (p.getY() * tileSize + verticalMargin);
		if(p.getName().equals("wall")) temp += tileSize / 3;
		p.setPosition((int) (p.getX() * tileSize + horizontalMargin), temp, (int) tileSize);
		
		invalidate();
	}
	
	/**
	 * @return the piece found at the (x,y) coordinates, or null if there's no piece there.
	 */
	public Piece getPiece(int x, int y) {
		for(Piece p : pieces)
			if(p.getX() == x && p.getY() == y) return p;
		
		return null;
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
}
