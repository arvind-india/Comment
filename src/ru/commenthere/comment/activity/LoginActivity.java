package ru.commenthere.comment.activity;


import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.task.SendCodeTask;
import ru.commenthere.comment.task.CustomAsyncTask;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends Activity implements OnClickListener, CustomAsyncTask.AsyncTaskListener {

	
	private static LoginActivity instance;
	private EditText emailEditText;
	private Button sendButton;
	
	private SendCodeTask sendCodeTask = null;
	
	private String email;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AppContext appContext = Application.getInstance().getAppContext();
		if (!TextUtils.isEmpty(appContext.getUserToken())){
			showMainActivity();
			return;
		}
		
		setContentView(R.layout.activity_login);
		instance = this;
		initViews();
	}
	
	private void initViews(){
		sendButton = (Button)findViewById(R.id.send);
		sendButton.setOnClickListener(this);
		
		emailEditText = (EditText)findViewById(R.id.email);
		emailEditText.setOnEditorActionListener(new OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId,
	                KeyEvent event) {
	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || actionId == EditorInfo.IME_ACTION_DONE) {	            	
	            	onClick(sendButton);
	            	return true;
	            }
	            return false;
	        }
	    });

	}
	
	private boolean validate(){
		email = emailEditText.getText().toString().trim();
		if (TextUtils.isEmpty(email)){
			AppUtils.showAlert(this, "Заполните поле email");
			return false;
		}
		
		return true;
		
	}
	
    private void processSendCode(String email){
		if (sendCodeTask == null){
			sendCodeTask = new SendCodeTask(this);
			sendCodeTask.setShowProgress(true);
			sendCodeTask.setAsyncTaskListener(this);			
			sendCodeTask.execute(email);
		}
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send){			
			if (validate()){
		        if (AppUtils.isOnline(this)){
					processSendCode(email);
		        }else{
		        	AppUtils.showToast(this, "Отсутствует подключение к Интернету");
		        }
			}
		}		
	}
	
	private void showConfimationCodeActivity(){
		Intent intent = new Intent(this, ConfirmationCodeActivity.class); 
		intent.putExtra(AppContext.EMAIL_KEY, email);
		startActivity(intent);
		//finish();
	}
	
	public static void toFinish(){
		if (instance != null){
			instance.finish();
		}
	}

	@Override
	public void onBeforeTaskStarted(CustomAsyncTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(CustomAsyncTask<?, ?, ?> task) {
		if((Boolean)task.getResult()){
			showConfimationCodeActivity();
		} else{
			AppUtils.showAlert(this, task.getErrorMessage());
		}
		sendCodeTask = null;
	}

	private void showMainActivity(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}
