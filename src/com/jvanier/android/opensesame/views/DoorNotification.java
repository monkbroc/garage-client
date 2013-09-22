package com.jvanier.android.opensesame.views;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.jvanier.android.opensesame.controllers.OpenSesameActivity;
import com.jvanier.android.opensesame.exhibits.GarageDoorStateExhibit;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class DoorNotification  {
	private Notification notification;

	@SuppressWarnings("deprecation")
	public DoorNotification(GarageDoorState model, Context context) {
		GarageDoorStateExhibit exhibit = new GarageDoorStateExhibit(model, context);
		
		int icon = exhibit.iconDrawableResourceForDoor();
		CharSequence tickerText = context.getString(exhibit.stringResourceForDoor());
		long when = exhibit.sinceDate().getTime();
		
		notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		CharSequence contentText = tickerText;
		Intent notificationIntent = new Intent(context, OpenSesameActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentText, null, contentIntent);
	}

	public Notification getNotification() {
		return notification;
	}
}
