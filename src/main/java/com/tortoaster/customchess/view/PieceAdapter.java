package com.tortoaster.customchess.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tortoaster.customchess.R;
import com.tortoaster.customchess.chess.Team;
import com.tortoaster.customchess.chess.piece.Piece;

import java.util.List;

public class PieceAdapter extends RecyclerView.Adapter {
	
	private class PieceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private Piece piece;
		
		private final TextView name;
		
		private final ImageView lightImage, darkImage;
		
		private final PieceSelectListener listener;
		
		private PieceViewHolder(View view, PieceSelectListener listener) {
			super(view);
			
			this.listener = listener;
			
			view.setOnClickListener(this);
			
			name = view.findViewById(R.id.name);
			lightImage = view.findViewById(R.id.light_image);
			darkImage = view.findViewById(R.id.dark_image);
		}
		
		@Override
		public void onClick(View v) {
			listener.pieceSelected(piece);
		}
		
		public void setPiece(Piece piece) {
			this.piece = piece;
			
			name.setText(piece.getName());
			
			lightImage.setImageBitmap(piece.getBitmap(Team.LIGHT));
			darkImage.setImageBitmap(piece.getBitmap(Team.DARK));
			
			if(piece.getTeam() == Team.LIGHT) {
				lightImage.setVisibility(View.VISIBLE);
				darkImage.setVisibility(View.GONE);
			} else {
				lightImage.setVisibility(View.GONE);
				darkImage.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public interface PieceSelectListener {
		void pieceSelected(Piece piece);
	}
	
	private final List<Piece> pieces;
	
	private final PieceSelectListener listener;
	
	public PieceAdapter(List<Piece> pieces, PieceSelectListener listener) {
		this.pieces = pieces;
		this.listener = listener;
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.piece_list_column, parent, false);
		
		return new PieceViewHolder(view, listener);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		((PieceViewHolder) holder).setPiece(pieces.get(position));
	}
	
	@Override
	public int getItemCount() {
		return pieces.size();
	}
}
