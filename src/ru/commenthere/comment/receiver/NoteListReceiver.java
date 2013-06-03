package ru.commenthere.comment.receiver;

import java.util.ArrayList;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.activity.MainActivity;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.service.LocationMonitoringService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class NoteListReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(LocationMonitoringService.ACTION_NOTES_LIST_RECEIVED.
				equals(intent.getAction())) {
			Intent  mainIntent = new Intent(context, MainActivity.class);
			mainIntent.putExtras(intent.getExtras());
			context.startActivity(mainIntent);
		}
	}
}
