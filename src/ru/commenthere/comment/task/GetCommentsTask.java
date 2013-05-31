package ru.commenthere.comment.task;

import java.util.List;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.net.ConnectionProtocol;
import android.content.Context;
import android.util.Log;

public class GetCommentsTask extends CustomAsyncTask<Object, String, Boolean> {

	private ConnectionProtocol protocol;
	private List<Comment> comments;

	public GetCommentsTask(Context mContext) {
		super(mContext);
		this.protocol = new ConnectionProtocol(AppContext.API_URL);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		try {
			Integer noteId = (Integer) params[0];
			comments = protocol.getComments(noteId);
			result = true;
		} catch (Exception e) {
			result = false;
			errorMessage = e.getMessage();
			if (e.getCause() != null) {
				errorMessage = errorMessage + ": " + e.getCause().toString();
			}
		}
		return result;
	}

	public List<Comment> getComments() {
		return comments;
	}

}
