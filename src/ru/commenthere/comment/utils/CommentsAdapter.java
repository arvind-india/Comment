package ru.commenthere.comment.utils;

import java.util.List;

import ru.commenthere.comment.model.Comment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CommentsAdapter extends BaseAdapter {

	private List<Comment> commentsList;
	
	
	public void setDataList(List<Comment> comments) {
		commentsList = comments;
	}
	
	@Override
	public int getCount() {
		return commentsList == null ? 0 : commentsList.size();
	}

	@Override
	public Object getItem(int position) {
		return commentsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static class ViewHandler {
		public ImageView image;
		
	}

}
