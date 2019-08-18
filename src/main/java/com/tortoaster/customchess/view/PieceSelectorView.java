package com.tortoaster.customchess.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.piece.Piece;
import com.tortoaster.customchess.chess.Team;

import java.util.ArrayList;
import java.util.List;

public class PieceSelectorView extends View {
	
	private static Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);
	private static Bitmap SELECT = generateRadialGradient();
	private float size;
	private List<Piece> pieces = new ArrayList<>();
	private Piece selected;
	
	/**
	 * The PieceSelectorView is an elegant container of pieces that can be selected by the user.
	 */
	public PieceSelectorView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieceSelectorView, 0, 0);
		
		try {
			size = a.getDimension(R.styleable.PieceSelectorView_size, 100);
		} finally {
			a.recycle();
		}
		
		PAINT.setColor(getResources().getColor(R.color.white) & 0x00FFFFFF | 0x40000000);
	}
	
	/**
	 * @return a white radial gradient to indicate the selected piece.
	 */
	private static Bitmap generateRadialGradient() {
		RadialGradient gradient = new RadialGradient(200, 200, 200, 0xFFFFFFFF, 0x00FFFFFF, android.graphics.Shader.TileMode.CLAMP);
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		Paint p = new Paint();
		
		p.setDither(true);
		p.setShader(gradient);
		
		c.drawCircle(200, 200, 200, p);
		
		return bitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int i = 0; i < pieces.size(); i++) {
			Piece p = pieces.get(i);
			
			p.draw(canvas);
			
			if(p == selected) {
				canvas.drawBitmap(SELECT, null, new RectF(size * i, size * 0.5F, size * (i + 1), size * 1.5F), PAINT);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			selected = pieces.get((int) (event.getX() / size));
			
			invalidate();
		}
		
		return true;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int desiredWidth = (int) size * pieces.size() + getPaddingLeft() + getPaddingRight();
		int desiredHeight = (int) size + getPaddingTop() + getPaddingBottom();
		
		setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec), measureDimension(desiredHeight, heightMeasureSpec));
	}
	
	private int measureDimension(int desiredSize, int measureSpec) {
		int result;
		
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		
		if(specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = desiredSize;
			if(specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		
		return result;
	}
	
	/**
	 * Add piece p to the display.
	 */
	public void addPiece(Piece p) {
		if(pieces.isEmpty()) selected = p;
		
		p.setPosition((int) (pieces.size() * size), 0, (int) size);
		pieces.add(p);
		
		invalidate();
	}
	
	/**
	 * Change the color of all pieces on display
	 *
	 * @param team the new color of all pieces
	 */
	public void setTeam(Team team) {
		for(Piece p : pieces) {
			p.setTeam(team);
		}
		
		invalidate();
	}
	
	/**
	 * @return the piece selected by the user.
	 */
	public Piece getSelected() {
		return selected;
	}
}
