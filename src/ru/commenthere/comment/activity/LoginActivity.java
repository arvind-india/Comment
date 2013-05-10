package ru.commenthere.comment.activity;


import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	
	private static LoginActivity instance;
	private EditText emailEditText;
	private Button sendButton;
	
	
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send){
			showConfimationCodeActivity();
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

}
