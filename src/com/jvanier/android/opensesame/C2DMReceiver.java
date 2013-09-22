package com.jvanier.android.opensesame;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.c2dm.C2DMBaseReceiver;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class C2DMReceiver extends C2DMBaseReceiver {
	public static final String REGISTRATION_SEND_SERVER_INTENT = "com.jvanier.android.opensesame.intent.SENDSERVER";
	public static final String REGISTRATION_SEND_SERVER_EXTRA_ID = "id";

    public C2DMReceiver() {
        super(Config.C2DM_SENDER);
    }

    @Override
    public void onError(Context context, String errorId) {
        Toast.makeText(context, "Messaging registration error: " + errorId,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        boolean up = intent.getExtras().getString(Config.C2DM_STATE_UP).equals("true");
		String since = intent.getExtras().getString(Config.C2DM_STATE_SINCE);
		
		GarageDoorState state = GarageDoorState.getInstance();
		state.setUp(up);
		state.setSince(since);
		state.notifyObservers();
	}


    @Override
	public void onRegistered(Context context, String registrationId) {
    	sendIdToServer(context, registrationId);
	}
    
    private void sendIdToServer(Context context, String registrationId) {
    	String uri = String.format(Config.SERVER_REGISTER_URL_TEMPLATE, registrationId);

		try { 

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			int timeoutConnection = 3000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpGet httpGet = new HttpGet();

			httpGet.setURI(new URI(uri));
			HttpResponse response;
			response = client.execute(httpGet);

			int statusCode = response.getStatusLine().getStatusCode();
			
			if(statusCode != HttpURLConnection.HTTP_OK) {
				throw new IOException("HTTP Status " + statusCode);
			}
			
		} catch (URISyntaxException e) {
			showException(context, e);
		} catch (ClientProtocolException e) {
			showException(context, e);
		} catch (IOException e) {
			showException(context, e);
		}
		
    	Toast.makeText(context, "Server registration complete",
                Toast.LENGTH_SHORT).show();
    }
    
    private void showException(Context context, Exception e) {
    	Toast.makeText(context, "Server registration error: " + e.toString(),
                Toast.LENGTH_LONG).show();
    	
    	// TODO: retry server registration later
    }
}
