package com.jvanier.android.opensesame.net;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.util.Log;

import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.models.Challenge;
import com.jvanier.android.opensesame.parsers.ChallengeParser;

public class GetChallengeCommand {

	private static final String TAG = GetChallengeCommand.class.getSimpleName();

	private HttpGet get;
	private HttpClient client;
	@SuppressWarnings("unused")
	private boolean canceled = false;

	public void cancel() {
		canceled = true;
		if (get != null)
			get.abort();
	}

	public Challenge execute() throws IOException, JSONException {
		setupConnection();
		Challenge challenge = getChallenge(Config.SERVER_OPEN_CHALLENGE_URL);
		return challenge;
	}
	
	private void setupConnection() {
		canceled = false;
		get = createGet();
		client = createClient();
	}

	protected HttpGet createGet() {
		get = new HttpGet();
		return get;
	}

	protected HttpClient createClient() {
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		return client;
	}
	
	private Challenge getChallenge(String url) throws IOException, JSONException {
		Log.e(TAG, "Getting challenge");
		
		try {
			get.setURI(new URI(url));
		} catch(URISyntaxException e) {
			throw new IOException("Bad URI: " + url);
		}
		HttpResponse response = client.execute(get);

		String raw = EntityUtils.toString(response.getEntity());
		
		ChallengeParser parser = new ChallengeParser();
		return parser.parse(raw);
	}
}