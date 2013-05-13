package ru.commenthere.comment;

import java.sql.SQLException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


public class AppContext {
	public static final String TAG = "AppContext";
	
	
	public static final String DEVICE_TYPE = "android";
	
	public static final String API_VERSION = "v1";
	
	public static final String API_URL = "http://comment.dnk-b.ru/api/";
	
	
	public static final boolean USE_GZIP = false; 
		
	
	private Context context;
	private SharedPreferences mPrefs;


	public AppContext(Context context){
		this.context = context;
		this.mPrefs = PreferenceManager.getDefaultSharedPreferences(context);	
	}
	
}
