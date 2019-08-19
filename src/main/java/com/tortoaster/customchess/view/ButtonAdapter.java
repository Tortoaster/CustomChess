package com.tortoaster.customchess.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tortoaster.customchess.R;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter {
	
	public class ButtonViewHolder extends RecyclerView.ViewHolder {
		
		private final Button button;
		
		public ButtonViewHolder(View view) {
			super(view);
			
			button = view.findViewById(R.id.button);
		}
		
		public Button getButton() {
			return button;
		}
	}
	
	private final List<String> strings;
	
	private final View.OnClickListener listener;
	
	public ButtonAdapter(List<String> strings, View.OnClickListener listener) {
		this.strings = strings;
		this.listener = listener;
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button_list_row, parent, false);
		
		view.findViewById(R.id.button).setOnClickListener(listener);
		
		return new ButtonViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		((ButtonViewHolder) holder).getButton().setText(strings.get(position));
	}
	
	@Override
	public int getItemCount() {
		return strings.size();
	}
}
