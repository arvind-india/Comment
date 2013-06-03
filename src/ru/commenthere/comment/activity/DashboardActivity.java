package ru.commenthere.comment.activity;

import ru.commenthere.comment.AppContext;
import ru.commenthere.comment.Application;
import ru.commenthere.comment.R;
import ru.commenthere.comment.service.LocationMonitoringService;
import ru.commenthere.comment.utils.AppUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

public class DashboardActivity extends Activity  implements OnClickListener{
	
	private ImageButton aButton;
	private ImageButton bButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		initViews();
		startLocationService();
	}

	private void initViews() {
		aButton = (ImageButton) findViewById(R.id.a_button);
		bButton = (ImageButton) findViewById(R.id.b_button);
		aButton.setOnClickListener(this);
		bButton.setOnClickListener(this);
	}

	private boolean startLocationService() {
		Intent serviceIntent = new Intent(this,
				LocationMonitoringService.class);
		return startService(serviceIntent) != null;
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.a_button) {
			showSendActivity(AppContext.PRIVATE_TYPE);
		} else if (v.getId() == R.id.b_button) {
			if (Application.getInstance().getAppContext().getLastLatitude() == 0
					|| Application.getInstance().getAppContext()
							.getLastLongitude() == 0) {

				AppUtils.showAlert(this,
						"Не определена текущая локация. Попробуйте позже.");
			} else {
				showSendActivity(AppContext.EVENT_TYPE);
			}
		}

	}
	
	private void showSendActivity(int type) {
		Intent intent = new Intent(this, SendActivity.class);
		intent.putExtra(AppContext.TYPE_KEY, type);
		startActivity(intent);
	}
	
	@Override
	protected void onStop() {
		Application.getInstance().decForegroundActiviesCount();
		super.onStop();
	}

	@Override
	protected void onStart() {
		Application.getInstance().incForegroundActiviesCount();
		super.onStart();
	}


}
