package com.jvanier.android.opensesame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/* Displays notification icon when state is updated */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public final void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(Config.INTENT_STATE_UPDATE)) {
			boolean up = intent.getExtras().getBoolean(Config.INTENT_STATE_UP);
			String since = intent.getExtras().getString(Config.INTENT_STATE_SINCE);
			DoorNotification.updateNotification(context, up, since);
		}
	}
}

