package ru.commenthere.comment.task;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.net.ConnectionProtocol;
import ru.commenthere.comment.utils.AppUtils;
import android.content.Context;
import android.net.Uri;

public class CreateNoteTask extends CustomAsyncTask<Object, String, Boolean> {

	private Context context;
	private ConnectionProtocol protocol;

	public CreateNoteTask(Context mContext) {
		super(mContext);
		this.protocol = new ConnectionProtocol(AppContext.API_URL);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		try {
			Note note = (Note) params[0];
			String path = (String) params[1];
			String content = AppUtils.encodeFileToBase64(context, path);
			// String content =AppUtils.readFile(path);
			result = protocol.createNote(note, content);
		} catch (Exception e) {
			result = false;
			errorMessage = e.getMessage();
			if (e.getCause() != null) {
				errorMessage = errorMessage + ": " + e.getCause().toString();
			}
		}
		return result;
	}

}
