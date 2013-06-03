package ru.commenthere.comment.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.R;
import ru.commenthere.comment.model.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewCommentsAdapter extends BaseAdapter {

	private Context context;
	private List<Comment> newComments;
	private ImageLoader loader;
	private DisplayImageOptions options;
	private LayoutInflater inflanter;
	
	public NewCommentsAdapter(Context context, List<Comment> comments) {
		this.context = context;
		this.newComments = comments;
		loader = ImageLoader.getInstance();
		options = createImageOptions();
		inflanter = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
		
	@Override
	public int getCount() {
		return newComments == null ? 0 : newComments.size();
	}

	@Override
	public Object getItem(int position) {
		return newComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return newComments.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (convertView == null) {
			view = inflanter.inflate(R.layout.new_comment_item, null);
			holder = new ViewHolder();
			holder.likeImage = (ImageView) view.findViewById(R.id.new_comment_icon);
			holder.title = (TextView) view.findViewById(R.id.new_comment_title);
			holder.icon = (ImageView) view.findViewById(R.id.new_comment_image);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final Comment comment = newComments.get(position);
		String url = comment.getFilePreviewUrl().startsWith("http") ? comment.getFilePreviewUrl()
				: AppContext.PHOTOS_URL + comment.getFilePreviewUrl();
					
		loader.displayImage(url, holder.icon, options);
		holder.title.setText(comment.getComment());
		if (comment.getIsLike() == 1){
			holder.likeImage.setImageResource(R.drawable.fon2_foto_like);	
		} else{
			holder.likeImage.setImageResource(R.drawable.fon2_foto_unlike1);				
		}
		return view;
	}
		
	private DisplayImageOptions createImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading().cacheInMemory().cacheOnDisc().build();
		return options;
	}

	private class ViewHolder {
		public ImageView icon;
		public TextView title;
		public ImageView likeImage;
	}
}
