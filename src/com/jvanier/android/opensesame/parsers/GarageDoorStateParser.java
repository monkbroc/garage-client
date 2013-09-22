package com.jvanier.android.opensesame.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class GarageDoorStateParser {
	private static final String TAG = GarageDoorStateParser.class.getSimpleName();

	public GarageDoorState parse(String raw) throws JSONException {
		GarageDoorState state = new GarageDoorState();
		
		JSONObject json = new JSONObject(raw);

		state.setUp(json.getBoolean(Config.C2DM_STATE_UP));
		state.setSince(json.optString(Config.C2DM_STATE_SINCE));

		Log.d(TAG, "State parsed OK.");

		return state;
	}

}
