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
import org.json.JSONObject;

import android.util.Log;

import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.models.Challenge;

public class SendAnswerCommand {

	private static final String TAG = SendAnswerCommand.class.getSimpleName();

	private HttpGet get;
	private HttpClient client;
	private boolean canceled = false;

	private Challenge challenge;
	
	public SendAnswerCommand(Challenge challenge) {
		this.challenge = challenge;
	}

	public void cancel() {
		canceled = true;
		if (get != null)
			get.abort();
	}

	public Boolean execute() throws IOException, JSONException {
		setupConnection();
		boolean success = sendAnswer(Config.SERVER_OPEN_ANSWER_URL_TEMPLATE);
		if(canceled) {
			return Boolean.FALSE;
		}
		return Boolean.valueOf(success);
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
	
	private boolean sendAnswer(String urlTemplate) throws IOException, JSONException {
		Log.e(TAG, "Sending answer");
		
		String url = String.format(urlTemplate, challenge.getChallenge(), challenge.getAnswer());

		try {
			get.setURI(new URI(url));
		} catch(URISyntaxException e) {
			throw new IOException("Bad URI: " + url);
		}
		HttpResponse response = client.execute(get);

		String html = EntityUtils.toString(response.getEntity());

		// No need for a parser for these 2 lines
		JSONObject state = new JSONObject(html);
		return state.getBoolean(Config.SERVER_SUCCESS);
	}
}