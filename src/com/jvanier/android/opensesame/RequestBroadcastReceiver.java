/*
 */
package com.jvanier.android.opensesame;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.c2dm.C2DMBaseReceiver;

/**
 * Helper class to handle BroadcastReciver behavior.
 * - can only run for a limited amount of time - it must start a real service 
 * for longer activity
 * - must get the power lock, must make sure it's released when all done.
 * 
 */
public class RequestBroadcastReceiver extends BroadcastReceiver {
    
    @Override
    public final void onReceive(Context context, Intent intent) {
        // To keep things in one place.
        C2DMBaseReceiver.runIntentInService(context, intent);
        if(isOrderedBroadcast()) {
        	setResult(Activity.RESULT_OK, null /* data */, null /* extra */);
        }
    }
}
