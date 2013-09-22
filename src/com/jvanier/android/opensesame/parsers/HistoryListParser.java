package com.jvanier.android.opensesame.parsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;
import android.util.Log;

import com.jvanier.android.opensesame.controllers.OpenSesameApp;
import com.jvanier.android.opensesame.models.HistoryItem;
import com.jvanier.android.opensesame.models.HistoryList;

public class HistoryListParser {
	private static final String TAG = HistoryListParser.class.getSimpleName();

	public HistoryList parse(String raw) throws JSONException {
		
		HistoryList history = new HistoryList();

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.US);

		JSONArray lines = new JSONArray(raw);
		for(int i = lines.length() - 1; i >= 0; i--) {
			JSONObject line = lines.getJSONObject(i);

			HistoryItem historyItem = new HistoryItem();
			historyItem.setUp(line.getBoolean("up"));
			historyItem.setMessage(line.getString("msg"));

			String since = line.getString("since");
			try {
				Date d = formatter.parse(since);
				historyItem.setSince(DateUtils.getRelativeDateTimeString(OpenSesameApp.getContext(), d.getTime(), DateUtils.MINUTE_IN_MILLIS, 2*DateUtils.DAY_IN_MILLIS, 0).toString());
			} catch(ParseException e) {
				throw new JSONException("Cannot parse date " + since);
			}

			history.add(historyItem);
		}

		Log.d(TAG, "History JSON parsed OK.");

		return history;
	}

}
