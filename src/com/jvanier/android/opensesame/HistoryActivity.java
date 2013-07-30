package com.jvanier.android.opensesame;

import java.net.HttpURLConnection;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends ListActivity {
	
	private static final String TAG = "HistoryActivity";
	
	private List<HistoryItem> history;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_with_empty);
        
		DownloadHistoryTask dht = new DownloadHistoryTask();
		dht.execute(Config.SERVER_HISTORY_URL);
	}
	
	private class HistoryItem {
		@SuppressWarnings("unused")
		public boolean up;
		public String message;
		public String since;
		@Override
		public String toString() {
			return message;
		}
	}
	

	private class BackgroundTaskAbort extends Exception {
		private static final long serialVersionUID = -1829432201341253115L;
	}

	private class DownloadHistoryTask extends AsyncTask<String, Void, Boolean> {

		private HttpGet httpGet;
		private ProgressDialog progressDialog;
		
		private ArrayList<HistoryItem> historyItems;
		
		public DownloadHistoryTask()
		{
		}

		@Override
		protected void onPreExecute() {
			httpGet = new HttpGet();
			
			// Show progress dialog
			Context context = HistoryActivity.this;
			progressDialog = ProgressDialog.show(context,
					null, context.getString(R.string.loading));
			progressDialog.setCancelable(true);
			
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					DownloadHistoryTask.this.cancel(false);
					HistoryActivity.this.finish();
					
					httpGet.abort();
				}
			});
		}
		
		@Override
		protected Boolean doInBackground(String... urls) {
			try {
				String url = getUrl(urls);
				if(isCancelled()) return null;
				String json = downloadHistory(url);
				if(isCancelled()) return null;
				parseHistoryData(json);
				if(isCancelled()) return null;
				
				return Boolean.TRUE;
			} catch (BackgroundTaskAbort e) {
				return Boolean.FALSE;
			}
		}

		private String getUrl(String[] urls) throws BackgroundTaskAbort {
			if(urls.length > 0 && urls[0] != null)
			{
				return urls[0];
			}
			throw new BackgroundTaskAbort();
		}

		private String downloadHistory(String url) throws BackgroundTaskAbort {
			String html = "";
			try
			{
				Log.d(TAG, "Downloading " + url);
				
				httpGet.setURI(new URI(url));
				HttpClient client = new DefaultHttpClient();
				HttpResponse response = client.execute(httpGet);
				
				Log.d(TAG, "Downloaded list. Status: " + response.getStatusLine().getStatusCode());

				if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
				{
					throw new BackgroundTaskAbort();
				}
				
				html = EntityUtils.toString(response.getEntity());
			} catch(Exception e) {
				Log.d(TAG, "Exception while downloading address: " + e.toString());
				throw new BackgroundTaskAbort();
			}
			
			return html;
		}

		@SuppressLint("SimpleDateFormat")
		private void parseHistoryData(String json) throws BackgroundTaskAbort {

			historyItems = new ArrayList<HistoryItem>(); 
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");

			try {
				JSONArray lines = new JSONArray(json);
				for(int i = lines.length() - 1; i >= 0; i--) {
					JSONObject line = lines.getJSONObject(i);

					HistoryItem historyItem = new HistoryItem();
					historyItem.up = line.getBoolean("up");
					historyItem.message = line.getString("msg");

					String since = line.getString("since");
					Date d = formatter.parse(since);
					historyItem.since = DateUtils.getRelativeDateTimeString(HistoryActivity.this, d.getTime(), DateUtils.MINUTE_IN_MILLIS, 2*DateUtils.DAY_IN_MILLIS, 0).toString(); 

					historyItems.add(historyItem);
				}

				Log.d(TAG, "History JSON parsed OK.");
			} catch(JSONException e) {
				Log.d(TAG, "Exception while parsing history JSON: " + e.toString());
				historyItems = null;
				throw new BackgroundTaskAbort(); 
			} catch(ParseException e) {
				Log.d(TAG, "Exception while parsing history JSON: " + e.toString());
				historyItems = null;
				throw new BackgroundTaskAbort(); 
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}

			if(!isCancelled()) {
				if(result.booleanValue())
				{
					updateCursor(historyItems);
				}
				else
				{
					setEmpty(R.string.couldNotDownload);
				}
			}
		}
	}

	private void setEmpty(int stringId) {
        TextView empty = (TextView) findViewById(android.R.id.empty);
        empty.setText(stringId);
	}

	private void updateCursor(List<HistoryItem> historyItems) {
		
		history = historyItems;
		
		ListView lv = this.getListView();

		ArrayAdapter<HistoryItem> arrayAdapter = new ArrayAdapter<HistoryItem>(this, android.R.layout.simple_list_item_2, android.R.id.text1, history) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView secondLine = (TextView) v.findViewById(android.R.id.text2);
				secondLine.setText(history.get(position).since);
				return v;
			}
			
		};

        // By using setAdapter method, you plugged the ListView with adapter
		lv.setAdapter(arrayAdapter);
		
	}
	
	

}
