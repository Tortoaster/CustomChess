package com.tortoaster.customchess.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Board;
import com.tortoaster.customchess.chess.player.Player;
import com.tortoaster.customchess.chess.piece.Kind;

public class Chess extends View implements View.OnTouchListener {
	
	private final int lightColor, darkColor, selectedColor, highlightedColor, endangeredColor, markedColor;
	
	private Player whitePlayer, blackPlayer;
	
	private Board board;
	
	public Chess(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOnTouchListener(this);
		
		lightColor = getResources().getColor(R.color.white);
		darkColor = getResources().getColor(R.color.darkWhite);
		selectedColor = getResources().getColor(R.color.selected);
		highlightedColor = getResources().getColor(R.color.highlighted);
		endangeredColor = getResources().getColor(R.color.threatened);
		markedColor = getResources().getColor(R.color.marked);
		
		for(Kind k : Kind.values()) {
			k.loadImage(getContext());
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(board != null) board.touchEvent(event);
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(board != null) board.draw(canvas);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(board != null) board.setScreenSize(w, h);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
		
		setMeasuredDimension(size, size);
	}
	
	public void setBoard(Board board) {
		this.board = board;
		board.calculateAllMovesOfTurn();
	}
	
	public void setWhitePlayer(Player player) {
		whitePlayer = player;
	}
	
	public void setBlackPlayer(Player player) {
		blackPlayer = player;
	}
	
	public int getLightColor() {
		return lightColor;
	}
	
	public int getDarkColor() {
		return darkColor;
	}
	
	public int getSelectedColor() {
		return selectedColor;
	}
	
	public int getHighlightedColor() {
		return highlightedColor;
	}
	
	public int getEndangeredColor() {
		return endangeredColor;
	}
	
	public int getMarkedColor() {
		return markedColor;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	
	public Player getBlackPlayer() {
		return blackPlayer;
	}
}
