package com.tortoaster.customchess.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
	
	private int previousDirection;
	
	private RecyclerView.ViewHolder previousHolder;
	
	private final FileAdapter adapter;
	
	private final Context context;
	
	public SwipeToDeleteCallback(Context context, FileAdapter adapter) {
		super(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		
		this.context = context;
		this.adapter = adapter;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		if(viewHolder == previousHolder && direction != previousDirection) {
			adapter.remove(viewHolder.getAdapterPosition());
		} else {
			previousHolder = viewHolder;
			previousDirection = direction;
			
			adapter.notifyDataSetChanged();
			
			Toast.makeText(context, "Now swipe the other direction to delete", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}
}