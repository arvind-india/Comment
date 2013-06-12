package ru.commenthere.comment.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.dao.NoteDAO;
import ru.commenthere.comment.db.ORMDatabaseHelper;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.task.CreateNoteTask;
import ru.commenthere.comment.task.CustomAsyncTask;
import ru.commenthere.comment.task.SendCodeTask;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class SendActivity extends Activity implements OnClickListener,
		CustomAsyncTask.AsyncTaskListener {
	
	public static final String TAG ="SendActivity"; 

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	private Uri fileUri;

	private ImageButton takePhotoButton;
	private ImageButton takeVideoButton;
	private ImageButton sendButton;

	private ImageView imageView;
	private VideoView videoView;

	private EditText coomentEditText;

	private CreateNoteTask createNoteTask;

	private String comment;

	private int type;
	private int fileType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		parseParams();
		initViews();
	}

	private void parseParams() {
		type = getIntent().getIntExtra(AppContext.TYPE_KEY, -1);
	}

	private void initViews() {
		coomentEditText = (EditText) findViewById(R.id.comment);

		imageView = (ImageView) findViewById(R.id.image_view);
		videoView = (VideoView) findViewById(R.id.video_view);
		videoView.setMediaController(new MediaController(this));

		takePhotoButton = (ImageButton) findViewById(R.id.take_photo);
		takeVideoButton = (ImageButton) findViewById(R.id.take_video);
		sendButton = (ImageButton) findViewById(R.id.send);

		takePhotoButton.setOnClickListener(this);
		takeVideoButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
	}

	private void capturePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), String.format("photo%s.jpg",
				System.currentTimeMillis())));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void captureVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video
															// image quality to
															// high
		startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				fileType = AppContext.PHOTO_FILE_TYPE;
				imageView.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.GONE);
				if (data != null && data.getExtras().get("data") != null){
					Bitmap image = (Bitmap) data.getExtras().get("data");
					imageView.setImageBitmap(image);
					
				}else{
					//imageView.setImageURI(fileUri);

					Bitmap smallImage = getBitmap(fileUri);
						if (smallImage != null){
						try {
						       FileOutputStream out = new FileOutputStream(getPath(fileUri));
						       smallImage.compress(Bitmap.CompressFormat.PNG, 90, out);
							   imageView.setImageBitmap(smallImage);
						} catch (Exception e) {
						       e.printStackTrace();
						}
					}


				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Video captured and saved to fileUri specified in the Intent
				fileUri = data.getData();
				fileType = AppContext.VIDOE_FILE_TYPE;

				imageView.setVisibility(View.GONE);
				videoView.setVisibility(View.VISIBLE);
				videoView.setVideoURI(fileUri);
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
			} else {
				// Video capture failed, advise user
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.take_photo) {
			capturePhoto();
		} else if (v.getId() == R.id.take_video) {
			captureVideo();
		} else if (v.getId() == R.id.send) {
			if (validate()) {

				final Note note = new Note();
				note.setDescription(comment);
				note.setFileType(fileType);
				note.setType(type);
				if (type == AppContext.EVENT_TYPE) {
					note.setLatitude(Application.getInstance().getAppContext()
							.getLastLatitude());
					note.setLongitude(Application.getInstance().getAppContext()
							.getLastLongitude());
				}				
				note.setLocalFilePath(getPath(fileUri));
			
				if (AppUtils.isOnline(this)) {
					processCreateNote(note);
				} else {
					ORMDatabaseHelper dbh = Application.getInstance().getAppContext().getOrmDatabaseHelper();
					try {
						if (dbh == null || dbh.getNoteDAO() == null){
							return;
						}
						NoteDAO noteDAO = dbh.getNoteDAO();
						if (noteDAO.idExists(note.getId())){
							noteDAO.update(note);
						} else{
							noteDAO.create(note);
						}	
						AppUtils.showToast(this,"Отсутствует подключение к Интернету. Данные сохранены локльно.");
						finish();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	private boolean validate() {
		comment = coomentEditText.getText().toString().trim();
		if (TextUtils.isEmpty(comment)) {
			AppUtils.showAlert(this, "Заполните поле комментарий");
			return false;
		}
		if (fileUri == null) {
			AppUtils.showAlert(this, "Сделайте снимок или видео");
			return false;
		}

		return true;

	}

	private void processCreateNote(Note note) {
		if (createNoteTask == null) {


			createNoteTask = new CreateNoteTask(this);
			createNoteTask.setShowProgress(true);
			createNoteTask.setAsyncTaskListener(this);
			createNoteTask.execute(note, note.getLocalFilePath());
		}
	}

	public String getPath(Uri uri) {
		if (fileType == 2) {
			String[] projection = { MediaStore.Images.Media.DATA };
			android.database.Cursor cursor = managedQuery(uri, projection,
					null, null, null);
			if (cursor == null)
				return null;
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String s = cursor.getString(column_index);
			cursor.close();
			return s;
		} else {
			return uri.getPath();
		}
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

	@Override
	public void onBeforeTaskStarted(CustomAsyncTask<?, ?, ?> task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(CustomAsyncTask<?, ?, ?> task) {
		if ((Boolean) task.getResult()) {
			AppUtils.showToast(this, "Размещено успешно");
			finish();
		} else {
			AppUtils.showToast(this, "Попробуйте еще раз");
		}
		createNoteTask = null;

	}
	
	
	private Bitmap getBitmap(Uri uri) {

		InputStream in = null;
		    final int IMAGE_MAX_SIZE = 480; 
		    Bitmap bitmapPhoto = null;
		    try {
				in = getContentResolver().openInputStream(uri);
			    // Decode image size
			    BitmapFactory.Options o = new BitmapFactory.Options();
			    bitmapPhoto = BitmapFactory.decodeStream(in, null, o);
			    try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				int maxSize = IMAGE_MAX_SIZE;
				int h = bitmapPhoto.getHeight();
				int w = bitmapPhoto.getWidth();
				if( h > maxSize || w > maxSize) {
					int h1, w1;
					double ar = (double)w/(double)h;
					if(h > w) {
						h1 = maxSize;
						w1 = (int)(maxSize*ar);
					} else {
						w1 = maxSize;
						h1 = (int)(maxSize/ar);
					}			
					bitmapPhoto = Bitmap.createScaledBitmap(bitmapPhoto, w1, h1, true);
				}	
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}


			return bitmapPhoto;
		}
	}
