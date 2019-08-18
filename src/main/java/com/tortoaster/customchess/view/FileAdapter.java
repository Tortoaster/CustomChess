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
			
			button = view.findViewById(R.id.text);
		}
		
		public Button getButton() {
			return button;
		}
	}
	
	private int prefixLength, postfixLength;
	
	private final List<File> fileList;
	
	public FileAdapter(File[] files, String prefix, String postfix) {
		fileList = new ArrayList<>();
		prefixLength = prefix.length();
		postfixLength = postfix.length();
		
		for(File file: files) {
			String name = file.getName();
			if(name.startsWith(prefix) && name.endsWith(postfix)) this.fileList.add(file);
		}
	}
	
	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_row, parent, false);
		
		return new FileViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		File file = fileList.get(position);
		
		((FileViewHolder) holder).getButton().setText(file.getName().substring(prefixLength, file.getName().length() - postfixLength));
	}
	
	@Override
	public int getItemCount() {
		return fileList.size();
	}
}
