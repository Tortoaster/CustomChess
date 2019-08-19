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
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter {
	
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
	
	private final String prefix, postfix;
	
	private final List<File> files;
	
	private final View.OnClickListener listener;
	
	public FileAdapter(File[] unfiltered, String prefix, String suffix, View.OnClickListener listener) {
		this.prefix = prefix;
		this.postfix = suffix;
		this.listener = listener;
		
		files = new ArrayList<>();
		
		for(File f: unfiltered) {
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
		
		((FileViewHolder) holder).getButton().setText(name.substring(prefix.length(), name.length() - postfix.length()));
	}
	
	@Override
	public int getItemCount() {
		return files.size();
	}
	
	public void add(File file) {
		files.add(file);
		notifyDataSetChanged();
	}
	
	public void remove(int index) {
		File file = files.remove(index);
		file.delete();
		notifyDataSetChanged();
	}
}
