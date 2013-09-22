package com.jvanier.android.opensesame.views;

import java.util.Observable;
import java.util.Observer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.models.HistoryItem;
import com.jvanier.android.opensesame.models.HistoryList;

public class HistoryView extends LinearLayout {
	private HistoryList model;

	private TextView textEmpty;
	private ListView listView;
	
	private ProgressDialog progressDialog;
	

	// Interface to send events from the view to the controller
	public static interface ViewListener {
		public void onCancelLoad();
	}

	private ViewListener viewListener;

	public void setViewListener(ViewListener viewListener) {
		this.viewListener = viewListener;
	}


	// Constructor for xml layouts
	public HistoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		setupViews();
	}

    private void setupViews() {
    	textEmpty = (TextView) findViewById(android.R.id.empty);
    	listView = (ListView) findViewById(android.R.id.list);
	}

	public void setModel(HistoryList model) {
		this.model = model;
		
		updateViewFromModel();
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

	public void updateViewFromModel() {
		ArrayAdapter<HistoryItem> arrayAdapter = new ArrayAdapter<HistoryItem>(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, model.getHistory()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				TextView secondLine = (TextView) v.findViewById(android.R.id.text2);
				secondLine.setText(model.getHistory().get(position).getSince());
				return v;
			}
			
		};

        // By using setAdapter method, you plugged the ListView with adapter
		listView.setAdapter(arrayAdapter);
    }
	
	public void setEmpty(int stringId) {
        textEmpty.setText(stringId);
	}
	
	public void showBusy(boolean busy) {
		Context context = getContext();
		if(busy) {
			// Show progress dialog
			progressDialog = ProgressDialog.show(context,
					null, context.getString(R.string.loading));
			progressDialog.setCancelable(true);
			
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					viewListener.onCancelLoad();
				}
			});
		} else {
			if(progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		}
	}

}
