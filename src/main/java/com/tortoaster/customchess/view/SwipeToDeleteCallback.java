package com.tortoaster.customchess.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
	
	private FileAdapter adapter;
	
	public SwipeToDeleteCallback(FileAdapter adapter) {
		super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		
		this.adapter = adapter;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		adapter.remove(viewHolder.getAdapterPosition());
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}
}