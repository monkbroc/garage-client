package com.jvanier.android.opensesame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.c2dm.C2DMessaging;

public class OpenSesameActivity extends Activity {
	
	private boolean up;
	private String since;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        up = false;
        
        setupButtons();
        
        registerC2DM();
    }
    
    private void setupButtons() {

    	Button b = (Button) findViewById(R.id.buttonOpenDoor);
    	b.setOnClickListener(new OnOpenButtonClick());
    	
    
    }
    
    private void registerC2DM() {
    	String id = C2DMessaging.getRegistrationId(this); 
    	if(id.length() == 0) {
    		/* Do full device registration */
    		C2DMessaging.register(this, Config.C2DM_SENDER);
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
	@Override
	protected void onResume () {
		super.onResume();
		updateGarage();
		registerBroadcastReceiver();
		requestUpdate();
	}

	private void registerBroadcastReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.INTENT_STATE_UPDATE);
		registerReceiver(receiver, filter);
	}

	private void requestUpdate() {
		Intent broadcast = new Intent();
		broadcast.setAction(Config.INTENT_REQUEST_UPDATE);
		sendBroadcast(broadcast);
	}  

	@Override
	protected void onPause () {
		super.onPause();
		unregisterBroadcastReceiver();
	}

	private void unregisterBroadcastReceiver() {
        unregisterReceiver(receiver);
	}

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Config.INTENT_STATE_UPDATE)) {
				boolean up = intent.getExtras().getBoolean(Config.INTENT_STATE_UP);
				String since = intent.getExtras().getString(Config.INTENT_STATE_SINCE);
				OpenSesameActivity.this.up = up;
				OpenSesameActivity.this.since = since;
				
				updateGarage();
			}
        }
    };
    
    @SuppressLint("SimpleDateFormat")
	private void updateGarage() {
		
		if(since == null) {
			return;
		}

		int icon = (up ? R.drawable.garage_large_open : R.drawable.garage_large_closed);
		CharSequence txt = getString(up ? R.string.garage_open : R.string.garage_closed);
		CharSequence btn = getString(up ? R.string.actionCloseDoor : R.string.actionOpenDoor);

    	ImageView iv = (ImageView) findViewById(R.id.imageGarage);
    	iv.setImageResource(icon);
		
		String sincetxt = "";
		try {

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
			Date d = formatter.parse(since);
			sincetxt = DateUtils.getRelativeDateTimeString(this, d.getTime(), DateUtils.MINUTE_IN_MILLIS, 2*DateUtils.DAY_IN_MILLIS, 0).toString();
			
		} catch(ParseException e) {
				
		}
    	
    	TextView tv = (TextView) findViewById(R.id.textGarage);
    	tv.setText(txt);

		TextView sv = (TextView) findViewById(R.id.textSince);
    	sv.setText(sincetxt);
		
    	Button b = (Button) findViewById(R.id.buttonOpenDoor);
    	b.setText(btn);
    }

    private class OnOpenButtonClick implements OnClickListener {
    	EditText input;
    	
		public void onClick(View v) {
			promptPin();
		}

		private void promptPin() {
			SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);

			String pin = settings.getString("pin", "");
			
			if(pin.length() > 0) {
				sendOpenCommand(pin);
			} else {
	
				
				AlertDialog.Builder alert = new AlertDialog.Builder(OpenSesameActivity.this);
	
				CharSequence title = getString(up ? R.string.actionCloseDoor : R.string.actionOpenDoor);
				alert.setTitle(title);
				alert.setMessage(R.string.pinPrompt);
	
				// Set an EditText view to get user input 
				input = new EditText(OpenSesameActivity.this);
				input.setKeyListener(DigitsKeyListener.getInstance());
				input.setTransformationMethod(PasswordTransformationMethod.getInstance());
				input.setText(pin);
				alert.setView(input);
	
				alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String pin = input.getText().toString();
	
						SharedPreferences.Editor settings = getPreferences(Context.MODE_PRIVATE).edit();
						settings.putString("pin", pin);
						settings.commit();
						
						sendOpenCommand(pin);
					}
				});
	
				alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
	
				alert.show();
			}
		}
		
		private void sendOpenCommand(String pin) {
			SendOpenCommandTask task = new SendOpenCommandTask();
			task.setSecret(pin);
			task.execute(new String[] { Config.SERVER_OPEN_CHALLENGE_URL, Config.SERVER_OPEN_ANSWER_URL_TEMPLATE });
		}
    }

    private class BackgroundTaskAbort extends Exception {
        private static final long serialVersionUID = 7349967401205013218L;
        public BackgroundTaskAbort(int errorId)
        {
        }

    }

    private class SendOpenCommandTask extends AsyncTask<String, Boolean, Boolean> {
		private Exception exception;
    	private HttpGet httpGet;
    	private HttpClient client;
    	private String secret;
    	
    	public SendOpenCommandTask() {
    		secret = "";
    	}
    	
		@Override
		protected void onPreExecute() {
			httpGet = new HttpGet();
			client = new DefaultHttpClient();
			
		}
		
		public void setSecret(String secret) {
			this.secret = secret;
		}

		@Override
		protected Boolean doInBackground(String... urls) {
			try {
				String challenge = getChallenge(urls[0]);
				String answer = computeAnswer(challenge);
				boolean success = sendAnswer(urls[1], challenge, answer);
				return Boolean.valueOf(success);

			} catch(Exception e) {
				exception = e;
				return Boolean.FALSE;
			}
		}
		
		private String getChallenge(String url) throws URISyntaxException, ClientProtocolException, IOException, BackgroundTaskAbort, JSONException {
			httpGet.setURI(new URI(url));
			HttpResponse response = client.execute(httpGet);

			if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
			{
				throw new BackgroundTaskAbort(R.string.errorDownload);
			}
			
			String html = EntityUtils.toString(response.getEntity());
			
			JSONObject state = new JSONObject(html);
			
			return state.getString(Config.SERVER_CHALLENGE);
		}
		
		private String computeAnswer(String challenge) throws NoSuchAlgorithmException, UnsupportedEncodingException {
			String in = challenge + secret;
			return SHA1(in);
		}

		private String convertToHex(byte[] data) { 
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < data.length; i++) { 
				int halfbyte = (data[i] >>> 4) & 0x0F;
				int two_halfs = 0;
				do { 
					if ((0 <= halfbyte) && (halfbyte <= 9)) 
						buf.append((char) ('0' + halfbyte));
					else 
						buf.append((char) ('a' + (halfbyte - 10)));
					halfbyte = data[i] & 0x0F;
				} while(two_halfs++ < 1);
			} 
			return buf.toString();
		} 

		public String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
			MessageDigest md = MessageDigest.getInstance("SHA-1");        
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			byte[] sha1hash = md.digest();
			return convertToHex(sha1hash);
		} 
		
		private boolean sendAnswer(String urlTemplate, String challenge, String answer) throws URISyntaxException, ClientProtocolException, IOException, BackgroundTaskAbort, JSONException {
			String url = String.format(urlTemplate, challenge, answer);
			httpGet.setURI(new URI(url));
			HttpResponse response = client.execute(httpGet);

			if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
			{
				throw new BackgroundTaskAbort(R.string.errorDownload);
			}
			
			String html = EntityUtils.toString(response.getEntity());
			
			JSONObject state = new JSONObject(html);
			
			return state.getBoolean(Config.SERVER_SUCCESS);
		}

		@Override
		protected void onPostExecute(Boolean success) {
			
			// Clear saved pin on failure
			if(!success) {
				SharedPreferences.Editor settings = getPreferences(Context.MODE_PRIVATE).edit();
				settings.putString("pin", "");
				settings.commit();
			}
			
			String txt;
			int duration;
			if(exception != null) {
				txt = exception.toString();
				duration = Toast.LENGTH_LONG;
			} else {
				txt = getString((success && success.booleanValue()) ? R.string.commandOK : R.string.commandFailed);
				duration = Toast.LENGTH_SHORT;
			}
			Toast toast = Toast.makeText(OpenSesameActivity.this, txt, duration);
			toast.show();
		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.history:
        	Intent i = new Intent(this, HistoryActivity.class);
        	startActivity(i);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
