package com.jvanier.android.opensesame.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.models.GarageDoorState;

public class EnterPinView {
	
	private Context context;
	
	private GarageDoorState state;
	
	private AlertDialog.Builder alert;
	private EditText input;
	
	// Interface to send events from the view to the controller
	public static interface ViewListener {
		public void onOKClick(String pinStr);
		public void onCancelClick();
	}

	private ViewListener viewListener;

	public void setViewListener(ViewListener viewListener) {
		this.viewListener = viewListener;
	}

	
	public EnterPinView(Context context, GarageDoorState state) {
		this.context = context;
		this.state = state;
	}
	
	public void build() {
		alert = new AlertDialog.Builder(context);
	
		CharSequence title = context.getString(state.isUp() ? R.string.actionCloseDoor : R.string.actionOpenDoor);
		alert.setTitle(title);
		alert.setMessage(R.string.pinPrompt);
	
		// Set an EditText view to get user input 
		input = new EditText(context);
		input.setKeyListener(DigitsKeyListener.getInstance());
		input.setTransformationMethod(PasswordTransformationMethod.getInstance());
		alert.setView(input);
	
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String pinStr = input.getText().toString();
				viewListener.onOKClick(pinStr);
			}
		});

		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				viewListener.onCancelClick();
			}
		});
	}
	
	public void show() {
		alert.show();
	}

}
