package ru.commenthere.comment.adapter;

import java.util.List;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.R;
import ru.commenthere.comment.activity.NoteDetailsActivity;
import ru.commenthere.comment.activity.PhotoActivity;
import ru.commenthere.comment.activity.VideoActivity;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.model.Note;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NotesAdapter extends BaseAdapter {

	private List<Note> notesList;
	private Context cnt;
	private ViewHandler viewHandler;
	private ImageLoader imageLoader;
	private DisplayImageOptions imageOptions;

	public NotesAdapter(Context context, List<Note> notes) {
		cnt = context;
		notesList = notes;
		imageLoader = ImageLoader.getInstance();
		imageOptions = createImageOptions();
	}

	public void setDataList(List<Note> comments) {
		notesList = comments;
	}

	@Override
	public int getCount() {
		return notesList == null ? 0 : notesList.size();
	}

	@Override
	public Object getItem(int position) {
		return notesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		long id = 0;
		if (position >= 0 && position < notesList.size()) {
			id = notesList.get(position).getId();
		}
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflateListItem();
			convertView.setTag(viewHandler);
		}
		viewHandler = (ViewHandler) convertView.getTag();
		final Note note = notesList.get(position);
		String imageUrl =  note.getFileName().startsWith("http") ? note.getFileNamePreview() 
				: AppContext.PHOTOS_URL + note.getFileNamePreview();

		imageLoader.displayImage(imageUrl, viewHandler.image,
				imageOptions);
		
		viewHandler.image.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (note  != null){
					if (note.getFileType()==AppContext.PHOTO_FILE_TYPE){
						String photoUrl =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.PHOTOS_URL + note.getFileName();
						Intent intent = new Intent(cnt, PhotoActivity.class);
						intent.putExtra(AppContext.URL_KEY, photoUrl);
						cnt.startActivity(intent);
					}else{
						String videoUrl =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.VIDEOS_URL + note.getFileName();
						Intent intent = new Intent(cnt, VideoActivity.class);
						intent.putExtra(AppContext.URL_KEY, videoUrl);
						cnt.startActivity(intent);
					}
				}
			}
		});
		
		viewHandler.firstName.setText(note.getDescription());
		viewHandler.likesAmount.setText(String.valueOf(note.getLikes()));
		viewHandler.dislikesAmount.setText(String.valueOf(note.getDislikes()));
		convertView.setTag(viewHandler);
		return convertView;
	}

	private View inflateListItem() {
		LayoutInflater inflanter = (LayoutInflater) cnt
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = (RelativeLayout) inflanter.inflate(R.layout.note_item,
				null);
		viewHandler = new ViewHandler();
		viewHandler.image = (ImageView) layout.findViewById(R.id.note_image);
		viewHandler.firstName = (TextView) layout
				.findViewById(R.id.note_first_name);
		viewHandler.likesAmount = (TextView) layout
				.findViewById(R.id.note_likes_amount);
		viewHandler.dislikesAmount = (TextView) layout
				.findViewById(R.id.note_dislikes_amount);
		return layout;
	}

	private DisplayImageOptions createImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading().cacheInMemory().cacheOnDisc().build();
		return options;
	}

	private static class ViewHandler {
		public ImageView image;
		public TextView firstName;
		public TextView status;
		public TextView likesAmount;
		public TextView dislikesAmount;
	}
}
