package ru.commenthere.comment.utils;

import java.util.HashMap;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationManager {

	private Context mContext;
	private android.app.NotificationManager manager;

	private int lastId;
	private HashMap<Integer, Notification> notificationMap;

	public NotificationManager(Context context) {
		mContext = context;
		manager = (android.app.NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationMap = new HashMap<Integer, Notification>();
	}

	public void releaseManager() {
		manager = null;
		notificationMap.clear();
		notificationMap = null;
	}

	public int createInfoNotification(String title, String msg, Intent actionIntent) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				mContext);
		builder.setSmallIcon(android.R.drawable.sym_action_chat);
		builder.setAutoCancel(true);
		builder.setTicker(title);
		builder.setContentText(msg);
		builder.setContentIntent(PendingIntent.getActivity(mContext, 0,
				actionIntent, PendingIntent.FLAG_CANCEL_CURRENT));
		builder.setWhen(System.currentTimeMillis());
		builder.setContentTitle(title);
		builder.setDefaults(Notification.DEFAULT_SOUND);

		Notification notification = builder.getNotification();
		manager.notify(lastId, notification);
		notificationMap.put(lastId, notification);
		return lastId++;
	}
}
