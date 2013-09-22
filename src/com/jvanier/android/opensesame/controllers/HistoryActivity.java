package com.jvanier.android.opensesame.controllers;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jvanier.android.opensesame.R;
import com.jvanier.android.opensesame.models.HistoryList;
import com.jvanier.android.opensesame.tasks.DownloadHistoryTask;
import com.jvanier.android.opensesame.tasks.NetworkTask;
import com.jvanier.android.opensesame.views.HistoryView;

public class HistoryActivity extends ListActivity {
	
	private HistoryList model;
	private HistoryView view;
	
	private DownloadHistoryTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		model = new HistoryList();
        setupView();
        downloadHistoryList();
	}
	
    private void setupView() {
        view = (HistoryView)View.inflate(this, R.layout.list_with_empty, null);
        view.setViewListener(viewListener);
		setContentView(view);
		view.setModel(model);
    }

	@Override
	protected void onResume () {
		super.onResume();
		view.registerModelObserver();
		view.updateViewFromModel();
	}

	@Override
	protected void onPause () {
		super.onPause();
		view.unregisterModelObserver();
	}

	/**
	 * This is how we receive events from the view.
	 * The view takes user actions
	 * The controller/activity responds to user actions
	 */
	private HistoryView.ViewListener viewListener = new HistoryView.ViewListener() {
		@Override
		public void onCancelLoad() {
			if (task != null && !task.isComplete()) {
				task.abort();
			}
			HistoryActivity.this.finish();
		}
	};

	private void downloadHistoryList() {
		if (task != null && !task.isComplete()) {
			task.abort();
		}
		
		task = new DownloadHistoryTask();
		task.setOnCompleteListener(new NetworkTask.OnCompleteListener<HistoryList>() {
			@Override
			public void onComplete(HistoryList result) {
				view.showBusy(false);
				model.consume(result);
			}
		});
		task.setOnGenericExceptionListener(new NetworkTask.OnExceptionListener() {
			@Override
			public void onException(Exception exception) {
				view.showBusy(false);
				Toast.makeText(HistoryActivity.this, R.string.couldNotDownload, Toast.LENGTH_LONG).show();
				HistoryActivity.this.finish();
			}
		});
		
		view.showBusy(true);
		task.execute();
	}

}
