package ru.commenthere.comment.activity;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.R;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends Activity {
	
	private String url;
	
	private VideoView videoView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		parseParams();
		initViews();
		fillData();
	}

	private void fillData() {
		videoView.setVideoURI(Uri.parse(url));		
	}

	private void initViews() {
		videoView = (VideoView)findViewById(R.id.video_view);		
	}

	private void parseParams() {
		url = getIntent().getStringExtra(AppContext.URL_KEY);		
	}


}
