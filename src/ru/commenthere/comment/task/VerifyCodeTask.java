package ru.commenthere.comment.task;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.model.User;
import ru.commenthere.comment.net.ConnectionProtocol;
import android.content.Context;
import android.util.Log;

public class VerifyCodeTask extends CustomAsyncTask<Object, String, Boolean> {

	private ConnectionProtocol protocol;

	private User user;

	public VerifyCodeTask(Context mContext) {
		super(mContext);
		this.protocol = new ConnectionProtocol(AppContext.API_URL);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		try {
			String email = (String) params[0];
			String code = (String) params[1];
			user = protocol.verifyCode(email, code);
			result = (user != null);
		} catch (Exception e) {
			result = false;
			errorMessage = e.getMessage();
			if (e.getCause() != null) {
				errorMessage = errorMessage + ": " + e.getCause().toString();
			}
		}
		return result;
	}

	public User getUser() {
		return user;
	}

}
