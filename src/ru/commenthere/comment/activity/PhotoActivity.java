package ru.commenthere.comment.activity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoActivity extends Activity {
	
	private String url;
	private ImageView imageView;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions imageOptions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		parseParams();
		initViews();
		initImageLoader();
		fillData();
	}

	private void fillData() {
		imageLoader.displayImage(url, imageView, imageOptions);		
	}

	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
		imageOptions = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading().cacheInMemory().cacheOnDisc().build();	
	}

	private void initViews() {
		imageView = (ImageView) findViewById(R.id.imageview);
	}

	private void parseParams() {
		url = getIntent().getStringExtra(AppContext.URL_KEY);				
	}

}
