package ru.commenthere.comment.activity;


import ru.commenthere.comment.R;
import ru.commenthere.comment.R.id;
import ru.commenthere.comment.R.layout;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ListActivity implements OnClickListener{
	
	private Button exitButton;
	private Button aButton;
	private Button bButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}
	
	private void initViews(){
		exitButton = (Button)findViewById(R.id.exit_button);
		aButton = (Button)findViewById(R.id.a_button);
		bButton = (Button)findViewById(R.id.b_button);
		exitButton.setOnClickListener(this);
		aButton.setOnClickListener(this);
		bButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.exit_button){
			finish();
		}else 	if (v.getId() == R.id.a_button){
			showSendActivity();
		}else 	if (v.getId() == R.id.b_button){
			showSendActivity();
		}
		
	}

	private void showSendActivity() {
		Intent intent = new Intent(this, SendActivity.class);
		startActivity(intent);		
	}
}
