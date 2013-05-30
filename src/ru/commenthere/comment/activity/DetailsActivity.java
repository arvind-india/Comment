package ru.commenthere.comment.activity;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.model.Note;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.VideoView;

public class DetailsActivity extends ListActivity implements OnClickListener {

	private ImageView imageView;
	private VideoView videoView;

	private Button backButton;
	private Button downloadButton;
	private Button sendButton;

	private Note note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		initViews();
		parseParams();
	}

	private void parseParams() {
		note = (Note) getIntent().getSerializableExtra(AppContext.NOTE_KEY);
	}

	private void initViews() {

	}

	@Override
	public void onClick(View v) {

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
