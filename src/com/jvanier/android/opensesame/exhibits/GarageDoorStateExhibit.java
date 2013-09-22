package com.jvanier.android.opensesame.exhibits;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;

import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class GarageDoorStateExhibit {
	private GarageDoorState state;
	private Context context;

	public GarageDoorStateExhibit(GarageDoorState state, Context context) {
		this.state = state;
		this.context = context;
	}

	public boolean hasDataToRender() {
		return state.getSince() != null;
	}

	public int largeDrawableResourceForDoor() {
		if(state.isUp()) {
			return R.drawable.garage_large_open;
		} else {
			return R.drawable.garage_large_closed;
		}
	}

	public int iconDrawableResourceForDoor() {
		if(state.isUp()) {
			return R.drawable.garage_open;
		} else {
			return R.drawable.garage_closed;
		}
	}
	
	public int stringResourceForDoor() {
		if(state.isUp()) {
			return R.string.garage_open;
		} else {
			return R.string.garage_closed;
		}
	}
	
	public Date sinceDate() {
		Date d = new Date();
		
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ",  Locale.US);
			d = formatter.parse(state.getSince());
		} catch (ParseException e) {
		}
		
		return d;
	}

	@SuppressLint("SimpleDateFormat")
	public String sinceWithRelativeFormat() {
		return DateUtils.getRelativeDateTimeString(context, sinceDate().getTime(), DateUtils.MINUTE_IN_MILLIS, 2*DateUtils.DAY_IN_MILLIS, 0).toString();
	}
}

