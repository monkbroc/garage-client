package com.jvanier.android.opensesame.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.net.Uri;
import android.util.Log;

import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.models.GarageDoorState;
import com.jvanier.android.opensesame.parsers.GarageDoorStateParser;

public class DownloadStateCommand {
	
	private static final String TAG = DownloadStateCommand.class.getSimpleName();
	
	private HttpGet get;
	private HttpClient client;
	private boolean canceled = false;
	
	public void cancel() {
		canceled = true;
		if (get != null)
			get.abort();
	}
	
	public GarageDoorState execute() throws IOException, JSONException {

		Uri.Builder builder = Uri.parse(Config.SERVER_STATE_URL).buildUpon();
		
		String raw = requestState(builder.toString());
		GarageDoorState state = stringToState(raw);
		return state;
	}

	
	protected String requestState(String url) throws IOException {
		canceled = false;
		HttpGet get = createGet(url);
		HttpClient client = createClient();
		HttpResponse response;
		try {
			response = client.execute(get);
			return EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			if (!canceled) {
				Log.e(TAG, "IOException", e);
			}
			throw e;
		}
	}
	
	protected HttpGet createGet(String fullUrl) {
		get = new HttpGet(fullUrl);
		return get;
	}
	
	protected HttpClient createClient() {
		client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
		return client;
	}
	
	final protected GarageDoorState stringToState(String raw) throws JSONException {
		GarageDoorStateParser parser = new GarageDoorStateParser();
		return parser.parse(raw);
	}
	
}