package ru.commenthere.comment.task;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.net.ConnectionProtocol;
import android.content.Context;
import android.util.Log;

public class SendCodeTask extends CustomAsyncTask<Object, String, Boolean>{

	private ConnectionProtocol protocol;
	
	public SendCodeTask(Context mContext) {
		super(mContext);
		this.protocol= new ConnectionProtocol(AppContext.API_URL); 
	}


	@Override
	protected Boolean doInBackground(Object... params) {
		boolean result = false;
		try {
			String email= (String)params[0]; 
			result = protocol.sendCode(email);
		} catch (Exception e) {
			result = false;
			errorMessage = e.getMessage();
			if (e.getCause() != null){
				errorMessage= errorMessage + ": " +e.getCause().toString();
			}
		}
		return result;
	}
	
}
