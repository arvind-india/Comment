package ru.commenthere.comment.adapter;

import java.sql.SQLException;
import java.util.List;

import ru.commenthere.comment.R;
import ru.commenthere.comment.model.Comment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {
	
	private Context context;
	private List<Comment> comments;
	private LayoutInflater inflater;
;

	public CommentsAdapter(Context context, List<Comment> comments) {
		this.context = context;
		this.comments = comments;
				
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return comments == null ? 0 : comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return comments.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (convertView == null) {
			view = inflater.inflate(R.layout.comment_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.comment_icon);
			holder.title = (TextView) view.findViewById(R.id.comment_title);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final Comment comment = comments.get(position);	
				
		holder.title.setText(comment.getComment());
		if (comment.getIsLike() == 1){
			holder.icon.setImageResource(R.drawable.fon2_foto_like);	
		} else{
			holder.icon.setImageResource(R.drawable.fon2_foto_unlike1);				
		}

			
		return view;
	}

	private class ViewHolder {
		public ImageView icon;
		public TextView title;
	}
}
