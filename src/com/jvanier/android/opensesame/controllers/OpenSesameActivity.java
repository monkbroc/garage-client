package com.jvanier.android.opensesame.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.c2dm.C2DMessaging;
import com.jvanier.android.opensesame.Config;
import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.models.GarageDoorState;
import com.jvanier.android.opensesame.models.Pin;
import com.jvanier.android.opensesame.storage.PinStorage;
import com.jvanier.android.opensesame.tasks.DownloadStateTask;
import com.jvanier.android.opensesame.tasks.NetworkTask;
import com.jvanier.android.opensesame.tasks.OpenDoorTask;
import com.jvanier.android.opensesame.views.EnterPinView;
import com.jvanier.android.opensesame.views.MainView;

public class OpenSesameActivity extends Activity {
	private GarageDoorState model;
	private MainView view;
	
	private DownloadStateTask stateTask;
	private OpenDoorTask openTask;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        model = GarageDoorState.getInstance();
        setupView();
        
        registerC2DM();
    }
    
    private void setupView() {
        view = (MainView)View.inflate(this, R.layout.main, null);
		view.setViewListener(viewListener);
		setContentView(view);
		view.setModel(model);
    }
    
	/**
	 * This is how we receive events from the view.
	 * The view takes user actions
	 * The controller/activity responds to user actions
	 */
	private MainView.ViewListener viewListener = new MainView.ViewListener() {
		@Override
		public void onDoorButtonClick() {
			performOpenDoor();
		}
	};
	
	
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
		view.registerModelObserver();
		view.updateViewFromModel();
		requestUpdate();
	}

	@Override
	protected void onPause () {
		super.onPause();
		view.unregisterModelObserver();
	}

	private void requestUpdate() {
		if (stateTask != null && !stateTask.isComplete()) {
			stateTask.abort();
		}
		
		stateTask = new DownloadStateTask();
		stateTask.setOnCompleteListener(new NetworkTask.OnCompleteListener<GarageDoorState>() {
			@Override
			public void onComplete(GarageDoorState result) {
				model.consume(result);
			}
		});
		stateTask.setOnGenericExceptionListener(new NetworkTask.OnExceptionListener() {
			@Override
			public void onException(Exception exception) {
				Toast.makeText(OpenSesameActivity.this, R.string.errorDownload, Toast.LENGTH_SHORT).show();
			}
		});
		
		stateTask.execute();
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

	private void performOpenDoor() {
		Pin pin = (new PinStorage(this)).get();
		
		if(pin.isValid()) {
			sendOpenCommand(pin);

		} else {
			
			EnterPinView pinView = new EnterPinView(this, model);
			pinView.build();
			pinView.setViewListener(new EnterPinView.ViewListener() {
				
				@Override
				public void onOKClick(String pinStr) {
					Pin pin = new Pin();
					pin.setPin(pinStr);
					(new PinStorage(OpenSesameActivity.this)).store(pin);
					sendOpenCommand(pin);
				}
				
				@Override
				public void onCancelClick() {
					// Do nothing on cancel
				}
			});
			pinView.show();
		}
	}

	private void sendOpenCommand(Pin pin) {
		if (openTask != null && !openTask.isComplete()) {
			openTask.abort();
		}
		
		openTask = new OpenDoorTask(pin);
		openTask.setOnCompleteListener(new NetworkTask.OnCompleteListener<Boolean>() {
			@Override
			public void onComplete(Boolean success) {
				int message;
				if(success) {
					message = R.string.commandOK;
				} else {
					// Clear pin on failure
					(new PinStorage(OpenSesameActivity.this)).store(new Pin());
					message = R.string.wrongPin;
				}
				Toast.makeText(OpenSesameActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
		openTask.setOnGenericExceptionListener(new NetworkTask.OnExceptionListener() {
			@Override
			public void onException(Exception exception) {
				Toast.makeText(OpenSesameActivity.this, R.string.commandFailed, Toast.LENGTH_SHORT).show();
			}
		});
		
		openTask.execute();
	}

}
