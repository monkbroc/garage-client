package com.jvanier.android.opensesame.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.jvanier.android.opensesame.models.Pin;

public class PinStorage {
	
	private Context context;
	
	public PinStorage(Context context) {
		this.context = context;
	}
	
	public Pin get() {
		SharedPreferences settings = context.getSharedPreferences(null, Context.MODE_PRIVATE);
		Pin pin = new Pin();
		pin.setPin(settings.getString("pin", ""));
		
		return pin;
	}
	
	public void store(Pin pin) {
		SharedPreferences.Editor settings = context.getSharedPreferences(null, Context.MODE_PRIVATE).edit();
		settings.putString("pin", pin.getPin());
		settings.commit();
	}

}
