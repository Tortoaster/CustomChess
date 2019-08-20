package com.tortoaster.customchess.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
	
	private final FileAdapter adapter;
	
	private final Context context;
	
	public SwipeToDeleteCallback(Context context, FileAdapter adapter) {
		super(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		
		this.context = context;
		this.adapter = adapter;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		if(true) {
			adapter.remove(viewHolder.getAdapterPosition());
			adapter.notifyDataSetChanged();
		} else {
		
		}
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}
}