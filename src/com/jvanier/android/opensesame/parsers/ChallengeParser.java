package com.jvanier.android.opensesame.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.models.Challenge;

public class ChallengeParser {

	public Challenge parse(String raw) throws JSONException {
		JSONObject state = new JSONObject(raw);

		Challenge challenge = new Challenge();
		challenge.setChallenge(state.getString(Config.SERVER_CHALLENGE));
		
		return challenge;
	}
}
