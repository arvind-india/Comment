package ru.commenthere.comment.activity;

import java.util.ArrayList;
import java.util.List;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.adapter.NotesAdapter;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.service.LocationMonitoringService;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private Button exitButton;
	private ImageButton aButton;
	private ImageButton bButton;
	private ListView list;

	private NotesAdapter notesAdapter;
	private List<Note> notes;

	private int listType = AppContext.NOTES_LIST_TYPE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
		startLocationService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// TODO remove this code after real testing
		Intent serviceStop = new Intent(
				LocationMonitoringService.ACTION_STOP_SERVICE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(serviceStop);
	}

	private void initViews() {
		exitButton = (Button) findViewById(R.id.exit_button);
		aButton = (ImageButton) findViewById(R.id.a_button);
		bButton = (ImageButton) findViewById(R.id.b_button);
		list = (ListView) findViewById(R.id.main_list);

		getMockListData();

		exitButton.setOnClickListener(this);
		aButton.setOnClickListener(this);
		bButton.setOnClickListener(this);
		notesAdapter = new NotesAdapter(MainActivity.this, notes);
		list.setAdapter(notesAdapter);
		list.setOnItemClickListener(this);
	}

	private boolean startLocationService() {
		Intent serviceIntent = new Intent(MainActivity.this,
				LocationMonitoringService.class);
		return startService(serviceIntent) != null;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.exit_button) {
			finish();
		} else if (v.getId() == R.id.a_button) {
			showSendActivity(AppContext.PRIVATE_TYPE);
		} else if (v.getId() == R.id.b_button) {
			if (Application.getInstance().getAppContext().getLastLatitude() == 0
					|| Application.getInstance().getAppContext()
							.getLastLongitude() == 0) {

				AppUtils.showAlert(this,
						"Не определена текущая локация. Попробуйте позже.");
			} else {
				showSendActivity(AppContext.EVENT_TYPE);
			}
		}

	}

	private void showSendActivity(int type) {
		Intent intent = new Intent(this, SendActivity.class);
		intent.putExtra(AppContext.TYPE_KEY, type);
		startActivity(intent);
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

	private void getMockListData() {
		notes = new ArrayList<Note>();
		for (int i = 0; i < 20; i++) {
			Note note = new Note();
			note.setDescription("Some test description text...");
			note.setDislikes(i);
			note.setLikes(20 - i);
			note.setFileName("http://kinoman.triolan.com.ua/uploads/posts/2013-04/thumbs/1365855917_bezymyannyj.png");
			note.setFileNamePreview("http://kinoman.triolan.com.ua/uploads/posts/2013-04/thumbs/1365855917_bezymyannyj.png");
			note.setId(i);
			notes.add(note);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (listType == AppContext.NOTES_LIST_TYPE) {
			Note note = notes.get(position);
			Intent intent = new Intent(this, NoteDetailsActivity.class);
			intent.putExtra(AppContext.NOTE_KEY, note);
			startActivity(intent);
		}
	}
}
