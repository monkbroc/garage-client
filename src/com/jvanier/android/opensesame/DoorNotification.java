package com.jvanier.android.opensesame;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.*;
import java.text.*;

public class DoorNotification {
	
	private static final int DOOR_NOTIFICATION_ID = 0; 
	
	@SuppressWarnings("deprecation")
	public static void updateNotification(Context context, boolean up, String since) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		int icon = (up ? R.drawable.garage_open : R.drawable.garage_closed);
		CharSequence tickerText = context.getString(up ? R.string.garage_open : R.string.garage_closed);
		long when;
		
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ",  Locale.US);
			Date d = formatter.parse(since);
			when = d.getTime();
		} catch (ParseException e) {
			when = System.currentTimeMillis();
		}
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		CharSequence contentText = tickerText;
		Intent notificationIntent = new Intent(context, OpenSesameActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentText, null, contentIntent);
		mNotificationManager.notify(DOOR_NOTIFICATION_ID, notification);
	}
}
