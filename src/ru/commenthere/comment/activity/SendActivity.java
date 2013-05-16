package ru.commenthere.comment.activity;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.model.Note;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class SendActivity extends Activity implements OnClickListener {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

	private Uri fileUri;
	
	private Button takePhotoButton;
	private Button takeVideoButton;
	private Button sendButton;
	
	private ImageView imageView;
	private VideoView videoView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send);
		initViews();
	}

	private void initViews(){		
		
		imageView = (ImageView)findViewById(R.id.image_view);
		videoView = (VideoView)findViewById(R.id.video_view);
		videoView.setMediaController(new MediaController(this));
		
		takePhotoButton = (Button)findViewById(R.id.take_photo);
		takeVideoButton = (Button)findViewById(R.id.take_video);
		sendButton = (Button)findViewById(R.id.send);
		
		takePhotoButton.setOnClickListener(this);
		takeVideoButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
	}
	
	private void capturePhoto(){
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg")));
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);		
	}
	
	private void captureVideo(){
	    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
	    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            Toast.makeText(this, "Photo saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	            fileUri  = data.getData();
//	            if(fileUri == null){
//	            	Bitmap image = (Bitmap) data.getExtras().get("data");	
//	            	
//	            	ContentValues values = new ContentValues();
//	            	values.put(Images.Media.TITLE, "title");
//	            	values.put(Images.Media.BUCKET_ID, "test");
//	            	values.put(Images.Media.DESCRIPTION, "test Image taken");
//	            	values.put(Images.Media.MIME_TYPE, "image/jpeg");
//	            	fileUri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
//	            	OutputStream outstream = null;
//	        		try {
//	        			outstream = getContentResolver().openOutputStream(fileUri);
//	        			image.compress(Bitmap.CompressFormat.JPEG, 90, outstream);
//	        		}catch (FileNotFoundException e) {
//						e.printStackTrace();
//	        		}
//	        		finally{
//	        			try {
//							outstream.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}	        			
//	        		}
//	            }

	            imageView.setVisibility(View.VISIBLE);
	            videoView.setVisibility(View.GONE);
	            imageView.setImageURI(fileUri);
	        } else if (resultCode == RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }

	    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Video saved to:\n" +
	                     data.getData(), Toast.LENGTH_LONG).show();
	            fileUri  = data.getData();
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
		if (v.getId() == R.id.take_photo){
			capturePhoto();			
		}else if(v.getId() == R.id.take_video){
			captureVideo();			
		}else if(v.getId() == R.id.send){
			processCreateNote();
		}
		
	}

	private void processCreateNote() {
		Note note = new Note();
		
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
}
