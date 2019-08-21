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
	
	private class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		
		private final Button button;
		
		private final FileListener listener;
		
		private File file;
		
		private String prefix, suffix;
		
		private FileViewHolder(View view, FileListener listener) {
			super(view);
			
			this.listener = listener;
			
			view.setOnClickListener(this);
			
			button = view.findViewById(R.id.name);
		}
		
		@Override
		public void onClick(View v) {
			listener.fileSelected(file, prefix, suffix);
		}
		
		public void setFile(File file, String prefix, String suffix) {
			this.file = file;
			this.prefix = prefix;
			this.suffix = suffix;
			
			String name = file.getName();
			
			button.setText(name.substring(prefix.length(), name.length() - suffix.length()));
		}
	}
	
	public interface FileListener {
		void fileSelected(File file, String prefix, String suffix);
		
		void fileRemoved(File file, String prefix, String suffix);
	}
	
	private final String prefix, suffix;
	
	private final List<File> files;
	
	private final FileListener listener;
	
	public FileAdapter(File[] unfiltered, String prefix, String suffix, FileListener listener) {
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
		
		return new FileViewHolder(view, listener);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		((FileViewHolder) holder).setFile(files.get(position), prefix, suffix);
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
		
		if(listener != null) listener.fileRemoved(file, prefix, suffix);
		
		return file.delete();
	}
}
