package com.jvanier.android.opensesame.views;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.exhibits.GarageDoorStateExhibit;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class MainView extends LinearLayout {
	private GarageDoorState model;
	private GarageDoorStateExhibit exhibit;

	private ImageButton doorImageButton;
	private TextView textGarage;
	private TextView textSince;

	// Interface to send events from the view to the controller
	public static interface ViewListener {
		public void onDoorButtonClick();
	}

	private ViewListener viewListener;

	public void setViewListener(ViewListener viewListener) {
		this.viewListener = viewListener;
	}

	// Constructor for xml layouts
	public MainView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		setupViews();
		setupViewListeners();
	}
	
	public void setModel(GarageDoorState model) {
		this.model = model;
		exhibit = new GarageDoorStateExhibit(model, getContext());
		
		updateViewFromModel();
	}

    private void setupViews() {
    	doorImageButton = (ImageButton) findViewById(R.id.buttonOpenDoor);
    	textGarage = (TextView) findViewById(R.id.textGarage);
		textSince = (TextView) findViewById(R.id.textSince);
	}

    private void setupViewListeners() {
    	doorImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewListener.onDoorButtonClick();
			}
		});
    }

	public void registerModelObserver() {
		model.addObserver(modelObserver);
	}

	public void unregisterModelObserver() {
		model.deleteObserver(modelObserver);
	}
	
	private Observer modelObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			updateViewFromModel();
		}
	};

    @SuppressLint("SimpleDateFormat")
	public void updateViewFromModel() {
    	post(new Runnable() {
    	    public void run() {
    	    	if(exhibit.hasDataToRender()) {
    				textGarage.setText(exhibit.stringResourceForDoor());
    				textSince.setText(exhibit.sinceWithRelativeFormat());
    				doorImageButton.setImageResource(exhibit.largeDrawableResourceForDoor());
    			}
    	    }
    	});
    }
}
