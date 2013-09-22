package com.jvanier.android.opensesame.controllers;

import android.app.Application;
import android.content.Context;

public class OpenSesameApp extends Application {
	
	private static OpenSesameApp instance;

	@SuppressWarnings("unused")
	private DoorNotificationController doorNotificationController;

	public OpenSesameApp() {
		instance = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		doorNotificationController = new DoorNotificationController(getApplicationContext());
	}
	
	public static Context getContext() {
		return instance.getApplicationContext();
	}
}
