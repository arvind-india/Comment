package ru.commenthere.comment.activity;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.adapter.CommentsAdapter;
import ru.commenthere.comment.adapter.NotesAdapter;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.task.AddCommentTask;
import ru.commenthere.comment.task.CustomAsyncTask;
import ru.commenthere.comment.task.GetCommentsTask;
import ru.commenthere.comment.task.SendCodeTask;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

public class NoteDetailsActivity extends ListActivity implements OnClickListener, CustomAsyncTask.AsyncTaskListener {
	private RadioGroup radioPanel;
	private RadioButton likeButton;
	private RadioButton dislikeButton;
	
	private ImageView imageView;
	private VideoView videoView;
	

	private TextView descTextView;
	private EditText commentEditText;
	
	private Button backButton;
	private Button downloadButton;
	private Button sendButton;
	
	private GetCommentsTask getCommentsTask = null;
	private AddCommentTask addCommentTask = null;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions imageOptions;

	private Note note;
	
	private List<Comment> comments;
	private CommentsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		parseParams();
		initViews();
		initImageLoader();
		fillData();
	}

	private void parseParams() {
		note = (Note) getIntent().getSerializableExtra(AppContext.NOTE_KEY);
	}

	private void initViews() {
		radioPanel = (RadioGroup)findViewById(R.id.radio_panel);		
		likeButton = (RadioButton)findViewById(R.id.button_like);		
		dislikeButton = (RadioButton)findViewById(R.id.button_dislike);		
		
		imageView = (ImageView)findViewById(R.id.image_view);
		videoView = (VideoView)findViewById(R.id.video_view);
		
		descTextView  = (TextView)findViewById(R.id.desc_text);
		commentEditText = (EditText)findViewById(R.id.comment_edittext);
		
		backButton = (Button)findViewById(R.id.button_back);
		downloadButton = (Button)findViewById(R.id.button_download);
		sendButton = (Button)findViewById(R.id.button_send); 

		backButton.setOnClickListener(this);
		downloadButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
	}
	
	private void initImageLoader(){
		imageLoader = ImageLoader.getInstance();
		imageOptions = createImageOptions();
	}
	
	private void fillData(){
		if (note.getFileType()==AppContext.PHOTO_FILE_TYPE){
			videoView.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);			
			String photoUrl =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.PHOTOS_URL + note.getFileName();
			imageLoader.displayImage(photoUrl, imageView, imageOptions);
		}else{
			videoView.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.GONE);
			String videoUrl =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.VIDEOS_URL + note.getFileName();
			videoView.setVideoURI(Uri.parse(videoUrl));
		}
		
		descTextView.setText(note.getDescription());
		
		commentEditText.setEnabled(note.getIsCanSendComment() ==1);
		sendButton.setEnabled(note.getIsCanSendComment() ==1);
		
		processGetComments(note.getId());
		
	}
	
	private void fillCommentsList(){
		if (comments == null){
			return;
		}
		
		adapter = new CommentsAdapter(this, comments);
		getListView().setAdapter(adapter);
		
		
	}
	
	private void processGetComments(int noteId) {
		if (getCommentsTask == null) {
			getCommentsTask = new GetCommentsTask(this);
			getCommentsTask.setShowProgress(true);
			getCommentsTask.setAsyncTaskListener(this);
			getCommentsTask.execute(noteId);
		}
	}
	
	private boolean validate() {
		String comment = commentEditText.getText().toString().trim();
		if (TextUtils.isEmpty(comment)) {
			AppUtils.showAlert(this, "Заполните поле comment");
			return false;
		}

		return true;

	}
	
	private void processAddComment(Comment comment) {
		if (addCommentTask == null) {
			addCommentTask = new AddCommentTask(this);
			addCommentTask.setShowProgress(true);
			addCommentTask.setAsyncTaskListener(this);
			addCommentTask.execute(comment);
		}
	}
	
	private void sendComment(){
		if (validate()) {
			if (AppUtils.isOnline(this)) {
				Comment comment = new Comment();
				comment.setNoteId(note.getId());
				comment.setComment(commentEditText.getText().toString().trim());
				if(likeButton.isChecked()){
					comment.setIsLike(1);	
				}else if (dislikeButton.isChecked()){
					comment.setIsLike(0);				
				}
				
				processAddComment(comment);
			} else {
				AppUtils.showToast(this,
						"Отсутствует подключение к Интернету");
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_back){
			finish();
		} else if(v.getId() == R.id.button_download){
			downloadFile();			
		} else if (v.getId() == R.id.button_send){
			sendComment();			
		}

	}
	
	private void downloadFile(){
		String url = null;
		if (note.getFileType()==AppContext.PHOTO_FILE_TYPE){
			url =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.PHOTOS_URL + note.getFileName();
		}else{
			url =  note.getFileName().startsWith("http://") ? note.getFileName() : AppContext.VIDEOS_URL + note.getFileName();
		}
		
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.setDescription(note.getDescription());
		request.setTitle("Download file");
		// in order for this if to run, you must use the android 3.2 to compile your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    request.allowScanningByMediaScanner();
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		String fileName = note.getFileName();
	
		if (fileName.startsWith("http://")){
			fileName = fileName.substring( fileName.lastIndexOf('/')+1, fileName.length());
		}  																	
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

	@Override
	protected void onStop() {
		Application.getInstance().decForegroundActiviesCount();
		super.onStop();
	}

	@Override
	protected void onStart() {
		Application.getInstance().incForegroundActiviesCount();
		super.onStart();
	}
	
	private DisplayImageOptions createImageOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading().cacheInMemory().cacheOnDisc().build();
		return options;
	}

	@Override
	public void onBeforeTaskStarted(CustomAsyncTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(CustomAsyncTask<?, ?, ?> task) {
		if (task == getCommentsTask){
			if ((Boolean) task.getResult()) {
				comments = ((GetCommentsTask)task).getComments();
				fillCommentsList();
			} else {
				AppUtils.showAlert(this, task.getErrorMessage());
			}
			getCommentsTask = null;			
		} else if (task == addCommentTask){
			if ((Boolean) task.getResult()) {
				finish();
			} else {
				AppUtils.showAlert(this, task.getErrorMessage());
			}
			addCommentTask = null;
		}
		
	}

}
