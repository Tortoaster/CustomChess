package com.tortoaster.customchess.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tortoaster.customchess.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter {
	
	public interface RemoveListener {
		void removed(String name);
	}
	
	public class FileViewHolder extends RecyclerView.ViewHolder {
		
		private final Button button;
		
		public FileViewHolder(View view) {
			super(view);
			
			button = view.findViewById(R.id.name);
		}
		
		public Button getButton() {
			return button;
		}
	}
	
	private final String prefix, suffix;
	
	private final List<File> files;
	
	private final View.OnClickListener listener;
	
	private RemoveListener removeListener;
	
	public FileAdapter(File[] unfiltered, String prefix, String suffix, View.OnClickListener listener) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.listener = listener;
		
		files = new ArrayList<>();
		
		Arrays.sort(unfiltered);
		
		for(File f : unfiltered) {
			String name = f.getName();
			
			if(name.startsWith(prefix) && name.endsWith(suffix)) files.add(f);
		}
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_row, parent, false);
		
		view.findViewById(R.id.name).setOnClickListener(listener);
		
		return new FileViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		String name = files.get(position).getName();
		
		((FileViewHolder) holder).getButton().setText(name.substring(prefix.length(), name.length() - suffix.length()));
	}
	
	@Override
	public int getItemCount() {
		return files.size();
	}
	
	public void add(File file) {
		int index = -Collections.binarySearch(files, file) - 1;
		
		if(index >= 0) {
			files.add(index, file);
			
			notifyItemInserted(index);
		}
	}
	
	public boolean replace(File old, File replace) {
		int oldIndex = files.indexOf(old);
		
		files.remove(oldIndex);
		
		notifyItemRemoved(oldIndex);
		
		int newIndex = -Collections.binarySearch(files, replace) - 1;
		
		if(newIndex >= 0) {
			files.add(newIndex, replace);
			
			notifyItemInserted(newIndex);
		}
		
		return old.renameTo(replace);
	}
	
	public boolean remove(int index) {
		File file = files.remove(index);
		
		notifyItemRemoved(index);
		
		if(removeListener != null) {
			String name = file.getName();
			removeListener.removed(name.substring(prefix.length(), name.length() - suffix.length()));
		}
		
		return file.delete();
	}
	
	public void setRemoveListener(RemoveListener removeListener) {
		this.removeListener = removeListener;
	}
}
