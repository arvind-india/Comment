package ru.commenthere.comment.task;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.model.Comment;
import ru.commenthere.comment.net.ConnectionProtocol;
import android.content.Context;
import android.util.Log;

public class AddCommentTask extends CustomAsyncTask<Object, String, Boolean> {

	private ConnectionProtocol protocol;

	public AddCommentTask(Context mContext) {
		super(mContext);
		this.protocol = new ConnectionProtocol(AppContext.API_URL);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		try {
			Comment commnet = (Comment) params[0];
			result = protocol.addComment(commnet);
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
