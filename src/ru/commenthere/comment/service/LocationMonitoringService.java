package ru.commenthere.comment.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.commenthere.comment.Application;
import ru.commenthere.comment.model.Note;
import ru.commenthere.comment.net.ConnectionClientException;
import ru.commenthere.comment.net.ConnectionProtocol;
import ru.commenthere.comment.utils.AppUtils;
import ru.commenthere.comment.utils.NotificationManager;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class LocationMonitoringService extends Service {

	public static final String TAG = "LocationMonitoringService";
	
	public static final String ACTION_CHANGE_REFRESH_INTERVAL = "ru.commenthere.comment.service." +
			"LocationMonitoringService.ACTION_CHANGE_REFRESH_INTERVAL";
	public static final String ACTION_CHANGE_ACCURACY = "ru.commenthere.comment.service." +
		"LocationMonitoringService.ACTION_CHANGE_ACCYRACY";
	public static final String ACTION_STOP_SERVICE = "ru.commenthere.comment.service." +
		"LocationMonitoringService.ACTION_STOP_SERVICE";
	public static final String ACTION_NOTES_LIST_RECEIVED = "ru.commenthere.comment.service." +
		"LocationMonitoringService.ACTION_NOTES_LIST_RECEIVED";
	
	public static final String CHANGE_REFRESH_INTERVAL_BUNDLE = "refresh_interval_data";
	public static final String CHANGE_ACCURACY_BUNDLE = "accuracy_data";
	public static final String ACCURACY = "accuracy";
	public static final String REFRESH_INTERVAL = "refresh_interval";
	
	private static long locationSendingInterval = 10000l;
	private static long locationRefreshInterval = 5000l;
	private static float accuracy = 100f;
	private static boolean isDebug;
	
	private static Location lastLocation;
	
	private LocationManager locationManager;
	private NetworkLocationListener locationListener;
	private CommandsBroadcastReceiver commandsReceiver;
	private static ConnectionProtocol connectionProtocol;
	private static LocationSender sendingTimer;
	private static NotificationManager notifManager;
	private static Application application;
	private static Timer counterTimer;
	private static Context appContext;
	
	private int launchCounter;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		isDebug = true;
		if(isDebug) {
			Log.d(TAG, "Service: creating service.");
		}
		
		application = Application.getInstance();
		appContext = application.getContext();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new NetworkLocationListener();
		commandsReceiver = new CommandsBroadcastReceiver();
		connectionProtocol = new ConnectionProtocol(null);
		notifManager = new NotificationManager(appContext);
		
		LocalBroadcastManager.getInstance(appContext).registerReceiver(commandsReceiver,
				getCommandsFilter());
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				locationRefreshInterval, accuracy, locationListener);
		startLocationSending();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(isDebug) {
			Log.d(TAG, "Service: starting service.");
		}
		
		launchCounter = startId;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(isDebug) {
			Log.d(TAG, "Service: destroing service.");
		}
		
		stopLocationSending();
		LocalBroadcastManager.getInstance(appContext).unregisterReceiver(commandsReceiver);
		
		locationManager = null;
		locationListener = null;
		commandsReceiver = null;
		connectionProtocol = null;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO implement logic for binding
		return null;
	}
	
	private void killService() {
		for (int i = 1; i <= launchCounter; i++) {
			stopSelf(i);
		}
	}
	
	private IntentFilter getCommandsFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_STOP_SERVICE);
		filter.addAction(ACTION_CHANGE_ACCURACY);
		filter.addAction(ACTION_CHANGE_REFRESH_INTERVAL);
		return filter;
	}
	
	private void startLocationSending() {
		sendingTimer = new LocationSender();
		counterTimer = new Timer();
		counterTimer.schedule(sendingTimer, locationSendingInterval, locationSendingInterval);
	}
	
	private void stopLocationSending() {
		counterTimer.cancel();
		sendingTimer.cancel();
		counterTimer = null;
		sendingTimer = null;
	}
	
	private static void sendBroadcastInfo(String action) {
		Intent actionIntent = new Intent(action);
		LocalBroadcastManager.getInstance(appContext).sendBroadcast(actionIntent);
	}

	private static class NetworkLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			lastLocation = location;
			application.getAppContext().saveLastLocation(lastLocation);
			if(isDebug) {
				Log.d(TAG, "Service: received new location ("+lastLocation.getLatitude()+
						", "+lastLocation.getLongitude()+")");
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//do nothing here for now
		}

		@Override
		public void onProviderEnabled(String provider) {
			//do nothing here for now
		}

		@Override
		public void onProviderDisabled(String provider) {
			//do nothing here for now
		}
		
	}
	
	private class CommandsBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle data;
			if(ACTION_CHANGE_ACCURACY.equals(action)) {
				data = intent.getBundleExtra(CHANGE_ACCURACY_BUNDLE);
				if(data != null) {
					float acc = data.getFloat(ACCURACY);
					accuracy = acc != 0 ? acc : accuracy;
				}
			} else if (ACTION_CHANGE_REFRESH_INTERVAL.equals(action)) {
				data = intent.getBundleExtra(CHANGE_ACCURACY_BUNDLE);
				if(data != null) {
					long interval = data.getLong(REFRESH_INTERVAL);
					locationSendingInterval = interval > 0 ? interval : locationSendingInterval;
				}
			} else if(ACTION_STOP_SERVICE.equals(action)) {
				killService();
			} else {
				//TODO add another commands handling
			}
		}	
	}
	
	private static class LocationSender extends TimerTask {
		@Override
		public void run() {
			List<Note> notes = null;
			if(isDebug) {
				Log.d(TAG, "Service: sending location to the server");
			}
			try {
				String token = application.getAppContext().getUserToken();
				if(AppUtils.isOnline(appContext) && lastLocation != null
						&& token != null) {
					notes = connectionProtocol.getNotes(lastLocation.getLatitude(),
							lastLocation.getLongitude(), token);
					if(notes != null) {
						//temporary. need logic for handling received notes
						Log.d(TAG, "Received "+ notes.size() + " notes.");
						for(Note n : notes) {
							Log.d(TAG, n.toString());
						}
						
						application.getAppContext().setReceivedNotesList(notes);
						
						if(application.isForeground()) {
							sendBroadcastInfo(ACTION_NOTES_LIST_RECEIVED);
						} else {
							notifManager.createInfoNotification("Notes Available",
									"For your location new notes available.");
						}
						
						
					}
				}
			} catch (ConnectionClientException e) {
				// TODO add some error handling logic(may be send broadcast 
				//with error info or do nothing at all)
				if(isDebug) {
					Log.e(TAG, "Service: error during sending location!");
				}
			}
		}
	}
}
