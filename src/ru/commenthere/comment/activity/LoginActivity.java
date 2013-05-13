package ru.commenthere.comment.activity;


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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener, CustomAsyncTask.AsyncTaskListener {

	
	private static LoginActivity instance;
	private EditText emailEditText;
	private Button sendButton;
	
	private SendCodeTask sendCodeTask = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		instance = this;
		initViews();
	}
	
	private void initViews(){
		emailEditText = (EditText)findViewById(R.id.email);
		sendButton = (Button)findViewById(R.id.send);
		sendButton.setOnClickListener(this);
	}
	
	private boolean validate(){
		if (TextUtils.isEmpty(emailEditText.toString().trim())){
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
				processSendCode(emailEditText.toString());
			}
		}		
	}
	
	private void showConfimationCodeActivity(){
		startActivity(new Intent(this, ConfirmationCodeActivity.class));
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

}
