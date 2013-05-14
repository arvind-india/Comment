package ru.commenthere.comment.service;

import ru.commenthere.comment.net.ConnectionClientException;
import ru.commenthere.comment.net.ConnectionProtocol;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationMonitoringService extends Service {

	public static final String ACTION_CHANGE_REFRESH_INTERVAL = "ru.commenthere.comment.service." +
			"LocationMonitoringService.ACTION_CHANGE_REFRESH_INTERVAL";
	public static final String ACTION_CHANGE_ACCURACY = "ru.commenthere.comment.service." +
	"LocationMonitoringService.ACTION_CHANGE_ACCYRACY";
	
	public static final String CHANGE_REFRESH_INTERVAL_BUNDLE = "refresh_interval_data";
	public static final String CHANGE_ACCURACY_BUNDLE = "accuracy_data";
	
	private static long refreshInterval = 10000l;
	private static float accuracy = 100f;
	
	private static Location lastLocation;
	
	private LocationManager locationManager;
	private NetworkLocationListener locationListener;
	private CommandsBroadcastReceiver commandsReceiver;
	private static ConnectionProtocol connectionProtocol;
	
	private int launchCounter;
	
	@Override
	public void onCreate() {
		super.onCreate();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new NetworkLocationListener();
		commandsReceiver = new CommandsBroadcastReceiver();
		connectionProtocol = new ConnectionProtocol(null);
		
		registerReceiver(commandsReceiver, getCommandsFilter());
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, refreshInterval,
				accuracy, locationListener);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		launchCounter = startId;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void killService() {
		for (int i = 1; i <= launchCounter; i++) {
			stopSelf(i);
		}
	}
	
	private IntentFilter getCommandsFilter() {
		IntentFilter filter = new IntentFilter();
		
		return filter;
	}

	private static class NetworkLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			lastLocation = location;
			//TODO get user token!
			try {
				connectionProtocol.getNotes(lastLocation.getLatitude(),
						lastLocation.getLongitude(), "");
			} catch (ConnectionClientException e) {
				// TODO add some error handling logic
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static class CommandsBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle data;
			if(ACTION_CHANGE_ACCURACY.equals(action)) {
				data = intent.getBundleExtra(CHANGE_ACCURACY_BUNDLE);
				if(data != null) {
					
				}
			} else if (ACTION_CHANGE_REFRESH_INTERVAL.equals(action)) {
				data = intent.getBundleExtra(CHANGE_ACCURACY_BUNDLE);
				if(data != null) {
					
				}
			} else {
				//TODO add another commands handling
			}
			
		}
		
	}
}
