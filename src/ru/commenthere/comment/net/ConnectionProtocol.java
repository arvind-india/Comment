package ru.commenthere.comment.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.commenthere.comment.AppContext;


import android.net.Uri;
import android.util.Log;



public class ConnectionProtocol {
	
	private static final String TAG = "ConnectionProtocol";
	
	
	private final static String SEND_CODE_ACTION = "send_code";
	
	private final static int OK_CODE = 0;
	
	private final static String ERROR_PARAM_NAME = "error";
	private final static String RESULT_PARAM_NAME = "result";
	private final static String ACTION_PARAM_NAME = "act";
	private final static String EMAIL_PARAM_NAME = "email";

	private ConnectionClient client;
	private String baseUrl;
	
	private long startTime;
	private long stopTime;
	
	public ConnectionProtocol(String baseUrl){
		this.baseUrl = baseUrl;
		this.client = new ConnectionClient();
	}
	
	public boolean sendCode(String email) throws ConnectionClientException{
		
		Uri.Builder ub = Uri.parse(AppContext.API_URL).buildUpon();
		ub.appendQueryParameter(ACTION_PARAM_NAME, SEND_CODE_ACTION);
		ub.appendQueryParameter(EMAIL_PARAM_NAME, email);
		
		String response = client.sendGetRequest(ub.toString());
		
		JSONObject result = null; 
		
		try {
			result = new JSONObject(response);
			int errorCode = result.optInt(ERROR_PARAM_NAME);
			if( errorCode == OK_CODE){
				JSONObject actionResult = result.getJSONObject(SEND_CODE_ACTION); 
				if(actionResult.optBoolean(RESULT_PARAM_NAME)){
					return true;
				}else{
					return false;
				}
			} else{
				throw new ConnectionClientException("Server error code: "+ errorCode);
			}
			
		} catch (JSONException e) {
			throw new ConnectionClientException("JSONException", e);
		}
	}
	

}
