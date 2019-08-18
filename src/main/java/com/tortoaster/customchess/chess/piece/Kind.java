package com.tortoaster.customchess.chess.piece;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Move;
import com.tortoaster.customchess.chess.Team;

import java.io.IOException;

public enum Kind {
	WALL("wall", 0, false, false, new Move[0], new Move[0]),
	KING("king", 12, true, true,
			new Move[]{
					new Move(1, 1),
					new Move(1, -1),
					new Move(-1, 1),
					new Move(-1, -1),
					new Move(1, 0),
					new Move(0, 1),
					new Move(-1, 0),
					new Move(0, -1)
			},
			new Move[]{
					new Move(1, 1),
					new Move(1, -1),
					new Move(-1, 1),
					new Move(-1, -1),
					new Move(1, 0),
					new Move(0, 1),
					new Move(-1, 0),
					new Move(0, -1)
			}
	),
	QUEEN("queen", 9, false, true,
			new Move[]{
					new Move(1, 1, true),
					new Move(1, -1, true),
					new Move(-1, 1, true),
					new Move(-1, -1, true),
					new Move(1, 0, true),
					new Move(0, 1, true),
					new Move(-1, 0, true),
					new Move(0, -1, true)
			},
			new Move[]{
					new Move(1, 1, true),
					new Move(1, -1, true),
					new Move(-1, 1, true),
					new Move(-1, -1, true),
					new Move(1, 0, true),
					new Move(0, 1, true),
					new Move(-1, 0, true),
					new Move(0, -1, true)
			}
	),
	KNIGHT("knight", 3, false, true,
			new Move[]{
					new Move(-1, 2, false, true),
					new Move(1, 2, false, true),
					new Move(2, 1, false, true),
					new Move(2, -1, false, true),
					new Move(1, -2, false, true),
					new Move(-1, -2, false, true),
					new Move(-2, -1, false, true),
					new Move(-2, 1, false, true)
			},
			new Move[]{
					new Move(-1, 2, false, true),
					new Move(1, 2, false, true),
					new Move(2, 1, false, true),
					new Move(2, -1, false, true),
					new Move(1, -2, false, true),
					new Move(-1, -2, false, true),
					new Move(-2, -1, false, true),
					new Move(-2, 1, false, true)
			}
	),
	BISHOP("bishop", 3, false, true,
			new Move[]{
					new Move(1, 1, true),
					new Move(1, -1, true),
					new Move(-1, 1, true),
					new Move(-1, -1, true)
			},
			new Move[]{new Move(1, 1, true),
					new Move(1, -1, true),
					new Move(-1, 1, true),
					new Move(-1, -1, true)
			}
	),
	ROOK("rook", 5, false, true,
			new Move[]{new Move(1, 0, true),
					new Move(0, 1, true),
					new Move(-1, 0, true),
					new Move(0, -1, true)
			},
			new Move[]{
					new Move(1, 0, true),
					new Move(0, 1, true),
					new Move(-1, 0, true),
					new Move(0, -1, true)
			}
	),
	PAWN("pawn", 1, false, true, new Move[]{new Move(0, 1)}, new Move[]{new Move(-1, 1), new Move(1, 1)}),
	CUSTOM(null, 0, false, false, new Move[0], new Move[0]);

	private boolean royal, capturable;

	private int value;

	private String name;

	private Move[] moveDirs, attackDirs;

	private Bitmap white, black;

	Kind(String name, int value, boolean royal, boolean capturable, Move[] moveDirs, Move[] attackDirs) {
		this.name = name;
		this.value = value;
		this.royal = royal;
		this.capturable = capturable;
		this.moveDirs = moveDirs;
		this.attackDirs = attackDirs;
	}

	/**
	 * Generates a bitmap.
	 *
	 * @param color 	The color of the bitmap.
	 * @return			The bitmap that is generated.
	 */
	private static Bitmap generateSquare(int color) {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		Paint p = new Paint();
		p.setColor(color);

		p.setDither(true);

		c.drawRect(0, 0, 400, 400, p);

		return bitmap;
	}

	/**
	 * Loads the image of the piece that is in the sprites folder.
	 *
	 * @param context
	 */

	public void loadImage(Context context) {
		if(this == CUSTOM) return;

		if(this == WALL) {
			white = black = generateSquare(context.getResources().getColor(R.color.darkBlack));
			return;
		}

		AssetManager assetManager = context.getAssets();

		try {
			white = BitmapFactory.decodeStream(assetManager.open("pieces/sprites/white_" + name + ".png"));
			black = BitmapFactory.decodeStream(assetManager.open("pieces/sprites/black_" + name + ".png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 *
	 * @return These getters return if the piece is royal, is capturable, the value of the piece,
	 * the directions for moves and attacks, the bitmap and the name.
	 */

	public boolean isRoyal() {
		return royal;
	}

	public int getValue() {
		return value;
	}

	public boolean isCapturable() {
		return capturable;
	}

	public Move[] getMoveDirections() {
		return moveDirs;
	}

	public Move[] getAttackDirections() {
		return attackDirs;
	}

	public Bitmap getBitmap(Team team) {
		if(team == Team.BLACK) return black;
		return white;
	}

	public String getName() {
		return name;
	}
}
