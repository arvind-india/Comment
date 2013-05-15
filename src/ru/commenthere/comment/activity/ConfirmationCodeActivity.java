package ru.commenthere.comment.activity;


import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import ru.commenthere.comment.model.User;
import ru.commenthere.comment.task.CustomAsyncTask;
import ru.commenthere.comment.task.SendCodeTask;
import ru.commenthere.comment.task.VerifyCodeTask;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ConfirmationCodeActivity extends Activity implements OnClickListener, CustomAsyncTask.AsyncTaskListener  {
	
	private EditText codeEditText;
	private Button sendButton;
	
	private String email;
	private String code;
	
	private VerifyCodeTask verifyCodeTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation_code);
		parseParams();
		initViews();
	}
	
	private void parseParams(){
		email = getIntent().getStringExtra(AppContext.EMAIL_KEY);
	}
	
	private void initViews(){
		sendButton = (Button)findViewById(R.id.send);
		sendButton.setOnClickListener(this);
		
		codeEditText = (EditText)findViewById(R.id.code);
		codeEditText.setOnEditorActionListener(new OnEditorActionListener() {
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
		code = codeEditText.getText().toString().trim();
		if (TextUtils.isEmpty(code)){
			AppUtils.showAlert(this, "Заполните поле код подтверждения");
			return false;
		}
		
		return true;
		
	}
	
    private void processVerifyCode(String email, String code){
		if (verifyCodeTask == null){
			verifyCodeTask = new VerifyCodeTask(this);
			verifyCodeTask.setShowProgress(true);
			verifyCodeTask.setAsyncTaskListener(this);			
			verifyCodeTask.execute(email, code);
		}
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send){
			if (validate()){
		        if (AppUtils.isOnline(this)){
					processVerifyCode(email, code);
		        }else{
		        	AppUtils.showToast(this, "Отсутствует подключение к Интернету");
		        }
			}
		}		
	}
	
	private void showMainActivity(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
		LoginActivity.toFinish();
	}

	@Override
	public void onBeforeTaskStarted(CustomAsyncTask<?, ?, ?> task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(CustomAsyncTask<?, ?, ?> task) {
		if((Boolean)task.getResult()){
			
			User user = ((VerifyCodeTask)task).getUser();
			AppContext appContext = Application.getInstance().getAppContext();
			appContext.setUserEmail(user.getEmail());
			appContext.setUserId(user.getId());
			appContext.setUserToken(user.getToken());
			
			showMainActivity();
		} else{
			AppUtils.showAlert(this, task.getErrorMessage());
		}
		verifyCodeTask = null;	

	}

}
