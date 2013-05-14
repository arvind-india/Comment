package ru.commenthere.comment.activity;


import ru.commenthere.comment.AppContext;
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

public class ConfirmationCodeActivity extends Activity implements OnClickListener {
	
	private EditText codeEditText;
	private Button sendButton;
	
	private String email;
	
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
		codeEditText = (EditText)findViewById(R.id.email);
		sendButton = (Button)findViewById(R.id.send);
		sendButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send){
			showMainActivity();
		}		
	}
	
	private void showMainActivity(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
		LoginActivity.toFinish();
	}

}
