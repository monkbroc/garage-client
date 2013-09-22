package com.jvanier.android.opensesame.controllers;

import java.util.Observable;
import java.util.Observer;

import android.app.NotificationManager;
import android.content.Context;

import com.jvanier.android.opensesame.models.GarageDoorState;
import com.jvanier.android.opensesame.views.DoorNotification;

public class DoorNotificationController {
	
	private static final int DOOR_NOTIFICATION_ID = 0; 
	
	private Context context;
	private GarageDoorState model;

	public DoorNotificationController(Context context) {
		this.context = context;
		model = GarageDoorState.getInstance();
		
		setupModelObserver();
	}

	private void setupModelObserver() {
		model.addObserver(new Observer() {
			@Override
			public void update(Observable o, Object arg) {
				updateNotification();
			}
		});
	}
	
	public void updateNotification() {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		DoorNotification notification = new DoorNotification(model, context);
		mNotificationManager.notify(DOOR_NOTIFICATION_ID, notification.getNotification());
	}
}
